package dev.oribuin.fishing.model.augment;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.config.item.ConstructComponent;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.manager.AugmentManager;
import dev.oribuin.fishing.model.economy.Cost;
import dev.oribuin.fishing.model.economy.CurrencyRegistry;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.storage.persistent.PDCSerializable;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.jeff_media.morepersistentdatatypes.DataType.TAG_CONTAINER;
import static dev.oribuin.fishing.storage.util.KeyRegistry.*;

/**
 * Augments are upgrades that can be crafted and applied to fishing rods to give them unique abilities to help the player produce more fish.
 * <p>
 * Use this class to create a new augment for the plugin. Any augments created should be registered using {@link AugmentManager#register(String, Class)}
 * <p>
 * All augment classes should be titled AugmentName and named in snake_case.
 */
@ConfigSerializable
public abstract class Augment extends FishEventHandler implements PDCSerializable {

    public static final BiConsumer<Augment, ItemStack> STACK_FUNCTION = (augment, stack) ->
            stack.editPersistentDataContainer(container -> {
                container.set(AUGMENT_TYPE.key(), AUGMENT_TYPE, augment.getName());
                augment.writeContainer(container);});

    protected transient final Random random = ThreadLocalRandom.current();
    protected transient final String name;
    protected transient int level;

    protected Boolean enabled;
    protected Integer maxLevel;
    protected Integer requiredLevel;
    protected List<String> description;
    protected String displayLine;
    protected String permission;
    protected List<String> conflictsWith;
    protected ItemConstruct displayItem;
    protected int baseUpgradeCost;
    protected String upgradeCost;

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
        this.enabled = true;
        this.maxLevel = 5;
        this.requiredLevel = 1;
        this.level = 1;
        this.description = List.of(description);
        this.displayLine = "<#93bc80>- <white>" + FishUtils.niceify(name) + " <#93bc80><level_roman>";
        this.permission = "fishing.augment." + name;
        this.conflictsWith = new ArrayList<>();
        this.baseUpgradeCost = 25000; // x + x * (i - 1) * 0.15
        this.upgradeCost = "<base_cost> + <base_cost> * ((<increase> - 1) * 0.15)";
        this.displayItem = ItemConstruct.of(Material.FIREWORK_STAR)
                .setName("<#94bc80><bold><display_name></bold> <gray>- <white>(<level><gray>/<white><max_level>)")
                .setLore(
                        this.description,
                        "",
                        "<#94bc80>Information",
                        " <#94bc80>- <gray>Required Level: <white><required_level>",
                        " <#94bc80>- <gray>Max Level: <white><max_level>",
                        ""
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .setFunction(stack -> STACK_FUNCTION.accept(this, stack));

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
     * Load and deserialize data from a data container
     *
     * @param container The container to read from
     */
    @Override
    public void readContainer(PersistentDataContainerView container) {
        this.level = container.getOrDefault(AUGMENT_LEVEL.key(), AUGMENT_LEVEL, 1);
    }

    /**
     * Write data into a data container
     *
     * @param container The container to write into
     */
    @Override
    public void writeContainer(PersistentDataContainer container) {
        container.set(AUGMENT_LEVEL.key(), AUGMENT_LEVEL, Math.min(this.getLevel(), this.getMaxLevel()));
    }

    /**
     * Get container where all important augment data is stored
     *
     * @param rod The fishing rod to get the data from
     */
    public void updateAugmentContainer(@NotNull ItemStack rod) {
        rod.editPersistentDataContainer(container -> {
            PersistentDataAdapterContext context = container.getAdapterContext();
            PersistentDataContainer augmentsContainer = context.newPersistentDataContainer();
            PersistentDataContainer augmentContainer = context.newPersistentDataContainer();
            this.writeContainer(augmentContainer);
            augmentsContainer.set(this.getNamespace(), TAG_CONTAINER, augmentContainer);
            container.set(ROD_AUGMENTS.key(), ROD_AUGMENTS, augmentsContainer);
        });
    }

    /**
     * Check whether the player is allowed to use the augment
     *
     * @param player The player to check
     *
     * @return Whether the player can use the augment
     */
    public boolean canUse(@NotNull Player player) {
        Fisher fisher = FishingPlugin.get().getDataManager().get(player.getUniqueId());

        // Check whether the player has access to use the augment
        if (this.permission != null && !player.hasPermission(this.permission)) return false;

        // Check whether the player is the correct level for the augment
        return fisher.getLevel() >= this.requiredLevel;
    }

    /**
     * Check whether an augment can be applied to the fishing rod
     *
     * @param stack The stack to check against
     * @param level The level to add to the rod
     *
     * @return Whether the fishing rod can apply
     */
    public boolean doesAccept(@Nullable ItemStack stack, int level) {
        if (stack == null || stack.getType() == Material.AIR) return false;

        AugmentManager manager = FishingPlugin.get().getAugmentManager();
        Map<String, Integer> current = manager.getAugments(stack).entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        o -> o.getValue().getLevel()
                ));

        int currentLevel = current.getOrDefault(this.name, 0);
        return currentLevel + level <= maxLevel;
    }

    /**
     * Get the upgrade cost for the augment level
     *
     * @param increase The increase in level s
     *
     * @return The resulting upgrade
     */
    public Cost<Integer> getUpgradeCost(int increase) {
        Placeholders placeholders = Placeholders.of(
                "base_cost", this.baseUpgradeCost,
                "level", this.level,
                "increase", Math.max(1, increase),
                "max", this.maxLevel
        );
        return new Cost<>(
                CurrencyRegistry.ENTROPY,
                (int) FishUtils.evaluate(placeholders.applyString(this.upgradeCost))
        );
    }

    public ItemStack getItemWithLevel() {
        return this.displayItem.createCustom(
                this.getPlaceholders(),
                stack -> STACK_FUNCTION.accept(this, stack)
        );
    }

    /**
     * The {@link NamespacedKey} for the augment, used to identify the augment in the plugin
     *
     * @return The namespace key, typically this will be "fishing:augment_name"
     */
    public final NamespacedKey getNamespace() {
        return new NamespacedKey(FishingPlugin.get(), this.name);
    }

    /**
     * The {@link NamespacedKey} for the lore of the augment, used to identify which line in the item description belongs to the augment
     *
     * @return The namespace key for the lore of the augment, typically this will be "fishing:augment_name-lore"
     */
    public final NamespacedKey getLoreNamespace() {
        return new NamespacedKey(FishingPlugin.get(), this.name + "-lore");
    }

    /**
     * All the placeholders that can be used when displaying information about the augment
     *
     * @return The {@link Placeholders} for the augment
     */
    public Placeholders getPlaceholders() {
        return Placeholders.builder()
                .add("enabled", this.enabled)
                .add("id", this.name)
                .add("display_name", FishUtils.capitalizeFully(this.name.replace("_", " ")))
                .add("max_level", this.maxLevel)
                .add("required_level", this.requiredLevel)
                .add("description", String.join("\n", this.description))
                .add("display_line", this.displayLine)
                .add("permission", this.permission)
                .add("level", this.level)
                .build();
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
        return displayItem.setFunction(stack -> STACK_FUNCTION.accept(this, stack));
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Augment{" +
               "name='" + name + '\'' +
               ", level=" + level +
               ", enabled=" + enabled +
               ", maxLevel=" + maxLevel +
               ", requiredLevel=" + requiredLevel +
               ", description=" + description +
               ", displayLine='" + displayLine + '\'' +
               ", permission='" + permission + '\'' +
               ", conflictsWith=" + conflictsWith +
               ", baseUpgradeCost=" + baseUpgradeCost +
               ", upgradeCost='" + upgradeCost + '\'' +
               '}';
    }
}
