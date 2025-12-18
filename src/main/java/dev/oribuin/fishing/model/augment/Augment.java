package dev.oribuin.fishing.model.augment;

import com.google.common.base.Supplier;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.config.ConfigHandler;
import dev.oribuin.fishing.manager.AugmentManager;
import dev.oribuin.fishing.model.economy.Cost;
import dev.oribuin.fishing.model.economy.CurrencyRegistry;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * Augments are upgrades that can be crafted and applied to fishing rods to give them unique abilities to help the player produce more fish.
 * <p>
 * Use this class to create a new augment for the plugin. Any augments created should be registered using {@link AugmentManager#register(Supplier)}
 * <p>
 * All augment classes should be titled AugmentName and named in snake_case.
 */
@ConfigSerializable
public abstract class Augment extends FishEventHandler {

    protected transient final Random random = ThreadLocalRandom.current();
    protected transient final Logger logger;
    protected transient final String name;
    
    protected Boolean enabled;
    protected Integer maxLevel;
    protected Integer requiredLevel;
    protected List<String> description;
    protected String displayLine;
    protected String permission;
    protected List<String> conflictsWith; 
    protected ItemConstruct displayItem;
    protected Cost price;

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
        this.enabled = true;
        this.maxLevel = 5;
        this.requiredLevel = 1;
        this.description = List.of(description);
        this.displayLine = "<red>" + StringUtils.capitalize(name.replace("_", " ")) + " <level_roman>";
        this.permission = "fishing.augment." + name;
        this.conflictsWith = new ArrayList<>();
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
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public Augment() {
        this("unknown", "No Description");
    }

    /**
     * The base itemstack design for the augment, this will be handed to players and shown in the codex
     * <p>
     * TODO: Replace this method with a global itemstack registry
     *
     * @return The default {@link ItemConstruct} for the augment
     */
    private ItemConstruct defaultItem() {
        List<String> lore = new ArrayList<>(this.description);
        lore.addAll(List.of(
                "",
                "<#94bc80>Information",
                " <#94bc80>- <gray>Required Level: <white><required_level>",
                " <#94bc80>- <gray>Max Level: <white><max_level>",
                ""
        ));

        return new ItemConstruct(Material.FIREWORK_STAR)
                .setName("<white>[<#94bc80><bold><display_name></bold><white>]")
                .setLore(lore)
                .setGlowing(true);
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
     * @return The {@link Placeholders} for the augment
     */
    public final Placeholders getPlaceholders() {
        return Placeholders.builder()
                .add("enabled", this.enabled)
                .add("id", this.name)
                .add("display_name", FishUtils.capitalizeFully(this.name.replace("_", " ")))
                .add("max_level", this.maxLevel)
                .add("required_level", this.requiredLevel)
                .add("description", String.join("\n", this.description))
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
        if (this.enabled) {
            super.callEvent(event, level);
        }
    }

    /**
     * Checks if the augment is enabled
     *
     * @return true if the augment is enabled
     */
    public final boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Set if the augment is enabled
     *
     * @param enabled If the augment is enabled
     */
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * the name of the augment
     *
     * @return The augment name / id
     */
    public final String getName() {
        return name;
    }

    /**
     * The description of the augment
     *
     * @return The description of the augment
     */
    public final List<String> getDescription() {
        return this.description;
    }

    /**
     * Set the description of the augment
     *
     * @param description The description of the augment
     */
    public final void setDescription(List<String> description) {
        this.description = description;
    }

    /**
     * The augments' display item that will be shown in the codex and given to players
     *
     * @return The display item of the augment
     */
    public final ItemConstruct getDisplayItem() {
        return displayItem;
    }

    /**
     * Set the display item of the augment
     *
     * @param displayItem The display item of the augment
     */
    public final void setDisplayItem(ItemConstruct displayItem) {
        this.displayItem = displayItem;
    }

    /**
     * The maximum level this augment can reach when applied to a fishing rod
     *
     * @return The max level of the augment
     */
    public final int getMaxLevel() {
        return this.maxLevel;
    }

    /**
     * Set the max level of the augment
     *
     * @param maxLevel The max level of the augment
     */
    public final void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * The required fishing level to use and apply the augment
     *
     * @return The required level of the augment
     */
    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    /**
     * Set the required level of the augment
     *
     * @param requiredLevel The required level of the augment
     */
    public final void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    /**
     * The line that will be described in the lore of the fishing rod when the augment is applied
     *
     * @return The display line of the augment
     */
    public final String getDisplayLine() {
        return this.displayLine;
    }

    /**
     * Set the display line of the augment
     *
     * @param displayLine The lore line of the augment
     */
    public final void setDisplayLine(String displayLine) {
        this.displayLine = displayLine;
    }

    /**
     * The required permission to use the augment in the plugin
     *
     * @return The permission required to use the augment
     */
    public final String getPermission() {
        return this.permission;
    }

    /**
     * Set the permission required to use the augment
     *
     * @param permission The permission required to use the augment
     */
    public final void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * The cost of the augment in the plugin
     * <p>
     * TODO: Needs to be moved into Recipe class when implemented
     *
     * @return The cost of the augment
     */
    public final Cost getPrice() {
        return price;
    }

    /**
     * Set the cost of the augment
     *
     * @param price The cost of the augment
     */
    public final void setPrice(Cost price) {
        this.price = price;
    }

}
