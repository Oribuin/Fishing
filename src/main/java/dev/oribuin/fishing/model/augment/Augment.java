package dev.oribuin.fishing.model.augment;

import com.google.common.base.Supplier;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.config.ConfigOptionType;
import dev.oribuin.fishing.api.config.Configurable;
import dev.oribuin.fishing.api.config.Option;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.model.economy.Cost;
import dev.oribuin.fishing.model.economy.CurrencyRegistry;
import dev.oribuin.fishing.model.item.ItemConstruct;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import static dev.rosewood.rosegarden.config.SettingSerializers.*;

/**
 * Augments are upgrades that can be crafted and applied to fishing rods to give them unique abilities to help the player produce more fish.
 * <p>
 * Use this class to create a new augment for the plugin. Any augments created should be registered using {@link AugmentRegistry#register(Supplier)}
 * <p>
 * All augment classes should be titled AugmentName and named in snake_case.
 */
public abstract class Augment extends FishEventHandler implements Configurable {

    private static final File AUGMENTS_FOLDER = new File(FishingPlugin.get().getDataFolder(), "augments");
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Augment.class);
    private final List<ConfigOptionType<?>> options;
    
    protected final Random random = ThreadLocalRandom.current();
    protected final Logger logger;
    protected final String name;
    protected final Option<Boolean> enabled;
    protected final Option<Integer> maxLevel;
    protected final Option<Integer> requiredLevel;
    protected final Option<List<String>> description;
    protected final Option<String> displayLine;
    protected final Option<String> permission;
    protected ItemConstruct displayItem;
    protected Cost price;
    protected File file;
    protected CommentedFileConfiguration config;

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     *
     * @param name        The unique name and identifier of the augment
     * @param description The description of the augment that will be displayed in the GUI
     */
    public Augment(String name, String... description) {
        this.name = name.toLowerCase();
        this.logger = Logger.getLogger("fishing-augment:" + this.name);
        this.enabled = new Option<>(BOOLEAN, true);
        this.maxLevel = new Option<>(INTEGER, 5);
        this.requiredLevel = new Option<>(INTEGER, 1);
        this.description = new Option<>(STRING_LIST, List.of(description));
        this.displayLine = new Option<>(STRING, "&c" + StringUtils.capitalize(name.replace("_", " ")) + " %level_roman%");
        this.permission = new Option<>(STRING, "fishing.augment." + name);
        this.file = new File(AUGMENTS_FOLDER, name.toLowerCase() + ".yml");
        this.config = CommentedFileConfiguration.loadConfiguration(this.file);
        this.options = new ArrayList<>();
        
        this.registerClass();
        this.reload(this.file, this.config);
        
        // todo: make the below load from a config
        this.displayItem = this.defaultItem();
        this.price = Cost.of(CurrencyRegistry.ENTROPY, 25000);
    }

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     *
     * @param name The unique name and identifier of the augment
     */
    public Augment(String name) {
        this(name, "No Description");
    }

    /**
     * The base itemstack design for the augment, this will be handed to players and shown in the codex
     * <p>
     * TODO: Replace this method with a global itemstack registry
     *
     * @return The default {@link ItemConstruct} for the augment
     */
    private ItemConstruct defaultItem() {
        List<String> lore = new ArrayList<>(this.description.value());
        lore.addAll(List.of(
                "",
                "&#4f73d6Information",
                " &#4f73d6- &7Required Level: &f%required_level%",
                " &#4f73d6- &7Max Level: &f%max_level%",
                ""
        ));

        return ItemConstruct.of(Material.FIREWORK_STAR)
                .name("&f[&#4f73d6&l%display_name%&f]")
                .lore(lore)
                .glowing(true);
    }

    /**
     * Required list of all the config options available in the class
     *
     * @return The provided config options
     */
    @Override
    public List<ConfigOptionType<?>> options() {
        return this.options;
    }

    /**
     * The {@link NamespacedKey} for the augment, used to identify the augment in the plugin
     *
     * @return The namespace key, typically this will be "fishing:augment_name"
     */
    public final NamespacedKey key() {
        return new NamespacedKey(FishingPlugin.get(), this.name);
    }

    /**
     * The {@link NamespacedKey} for the lore of the augment, used to identify which line in the item description belongs to the augment
     *
     * @return The namespace key for the lore of the augment, typically this will be "fishing:augment_name-lore"
     */
    public final NamespacedKey loreKey() {
        return new NamespacedKey(FishingPlugin.get(), this.name + "-lore");
    }

    /**
     * All the placeholders that can be used when displaying information about the augment
     *
     * @return The {@link StringPlaceholders} for the augment
     */
    public final StringPlaceholders placeholders() {
        return StringPlaceholders.builder()
                .add("enabled", this.enabled)
                .add("id", this.name)
                .add("display_name", FishUtils.capitalizeFully(this.name.replace("_", " ")))
                .add("max_level", this.maxLevel)
                .add("required_level", this.requiredLevel)
                .add("description", String.join("\n", this.description.value()))
                .add("display_line", this.displayLine)
                .add("permission", this.permission)
                .build();
    }

    /**
     * Call an event from the handler's registered events, This will not take priority into account.
     *
     * @param event The {@link Event} to call
     * @param level The level of the event
     */
    @Override
    public <T extends Event> void callEvent(T event, int level) {
        if (!this.enabled.value()) return;

        super.callEvent(event, level);
    }

    /**
     * Checks if the augment is enabled
     *
     * @return true if the augment is enabled
     */
    public final boolean enabled() {
        return this.enabled.value();
    }

    /**
     * Set if the augment is enabled
     *
     * @param enabled If the augment is enabled
     */
    public final void enabled(boolean enabled) {
        this.enabled.value(enabled);
    }

    /**
     * the name of the augment
     *
     * @return The augment name / id
     */
    public final String name() {
        return name;
    }

    /**
     * The description of the augment
     *
     * @return The description of the augment
     */
    public final List<String> description() {
        return this.description.value();
    }

    /**
     * Set the description of the augment
     *
     * @param description The description of the augment
     */
    public final void description(List<String> description) {
        this.description.value(description);
    }

    /**
     * The augments' display item that will be shown in the codex and given to players
     *
     * @return The display item of the augment
     */
    public final ItemConstruct displayItem() {
        return displayItem;
    }

    /**
     * Set the display item of the augment
     *
     * @param displayItem The display item of the augment
     */
    public final void displayItem(ItemConstruct displayItem) {
        this.displayItem = displayItem;
    }

    /**
     * The maximum level this augment can reach when applied to a fishing rod
     *
     * @return The max level of the augment
     */
    public final int maxLevel() {
        return maxLevel.value();
    }

    /**
     * Set the max level of the augment
     *
     * @param maxLevel The max level of the augment
     */
    public final void maxLevel(int maxLevel) {
        this.maxLevel.value(maxLevel);
    }

    /**
     * The required fishing level to use and apply the augment
     *
     * @return The required level of the augment
     */
    public final int requiredLevel() {
        return requiredLevel.value();
    }

    /**
     * Set the required level of the augment
     *
     * @param requiredLevel The required level of the augment
     */
    public final void requiredLevel(int requiredLevel) {
        this.requiredLevel.value(requiredLevel);
    }

    /**
     * The line that will be described in the lore of the fishing rod when the augment is applied
     *
     * @return The display line of the augment
     */
    public final String displayLine() {
        return displayLine.value();
    }

    /**
     * Set the display line of the augment
     *
     * @param displayLine The lore line of the augment
     */
    public final void displayLine(String displayLine) {
        this.displayLine.value(displayLine);
    }

    /**
     * The required permission to use the augment in the plugin
     *
     * @return The permission required to use the augment
     */
    public final String permission() {
        return permission.value();
    }

    /**
     * Set the permission required to use the augment
     *
     * @param permission The permission required to use the augment
     */
    public final void permission(String permission) {
        this.permission.value(permission);
    }

    /**
     * The cost of the augment in the plugin
     * <p>
     * TODO: Needs to be moved into {@link dev.oribuin.fishing.api.recipe.Recipe} class when implemented
     *
     * @return The cost of the augment
     */
    public final Cost price() {
        return price;
    }

    /**
     * Set the cost of the augment
     *
     * @param price The cost of the augment
     */
    public final void price(Cost price) {
        this.price = price;
    }

}

