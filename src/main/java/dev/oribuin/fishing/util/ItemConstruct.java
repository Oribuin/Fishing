package dev.oribuin.fishing.util;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.Unbreakable;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.api.config.Configurable;
import dev.oribuin.fishing.util.item.ItemEdible;
import dev.oribuin.fishing.util.item.ItemEffect;
import dev.oribuin.fishing.util.item.ItemEnchant;
import dev.oribuin.fishing.util.item.ItemTexture;
import dev.oribuin.fishing.util.item.ItemValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Create a config system that will apply itemstack values to be serialized/deserialized from
 * config files
 */
@SuppressWarnings({ "unused", "FieldMayBeFinal" })
public class ItemConstruct implements Configurable {

    private Material type;
    private Integer amount;
    private String name;
    private List<String> lore;
    private ItemEnchant enchantments;
    private ItemEnchant storedEnchantments;
    private ItemEffect effects;
    private ItemTexture texture;
    private ItemEdible edible;
    private ItemValue<Boolean> unbreakable;
    private boolean glowing;
    private boolean tooltip;
    private boolean additionalTooltip;
    private boolean glider;

    /**
     * Create a new config serializable itemstack constructor with a {@link Material} type.
     * This constructor should not be used directly, instead use {@link #of(Material)}
     *
     * @param type The Material type
     */
    private ItemConstruct(Material type) {
        this.type = type;
        this.amount = 1;
        this.name = null;
        this.lore = new ArrayList<>();
        this.enchantments = new ItemEnchant();
        this.storedEnchantments = new ItemEnchant();
        this.effects = new ItemEffect();
        this.unbreakable = new ItemValue<>(false);
        this.texture = new ItemTexture(null);
        this.edible = null;
        this.glowing = false;
        this.tooltip = true;
        this.additionalTooltip = true;
        this.glider = false;
    }

    /**
     * Create a new config serializable itemstack constructor with a {@link Material} type
     * <p>
     * Usage: ItemConstruct.of(Material.STONE).name("Hello World").build();
     *
     * @param type The Material type
     */
    public static ItemConstruct of(Material type) {
        return new ItemConstruct(type);
    }

    /**
     * Convert the {@link ItemConstruct} into a new {@link ItemStack} with the default values. All placeholders will be empty
     * <p>
     * Usage: ItemConstruct.of(Material.STONE).build();
     *
     * @return The itemstack
     */
    public ItemStack build() {
        return this.build(StringPlaceholders.empty());
    }

    /**
     * Convert the {@link ItemConstruct} into a new {@link ItemStack} with the default values. Adds applied placeholders to the Name and Lore.
     * <p>
     * Uses Paper's {@link DataComponentTypes} to apply the values to the itemstack instead of the traditional {@link ItemMeta}
     * <p>
     * Usage: ItemConstruct.of(Material.STONE).name("Hello World").build(StringPlaceholders.of("name", "Oribuin").build());
     *
     * @param placeholders The {@link StringPlaceholders} to apply to the itemstack
     *
     * @return The constructed {@link ItemStack} with the applied placeholders
     */
    @SuppressWarnings({ "UnstableApiUsage", "deprecation" })
    public ItemStack build(StringPlaceholders placeholders) {
        ItemStack stack = new ItemStack(this.type, this.amount);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack; // Probably air

        if (this.name != null) stack.setData(DataComponentTypes.CUSTOM_NAME, FishUtils.kyorify(this.name, placeholders));
        if (this.lore != null) {
            List<Component> lines = new ArrayList<>();
            for (String line : this.lore) {
                String[] newLine = placeholders.apply(line).split("\n");
                for (String s : newLine) lines.add(FishUtils.kyorify(s));
            }

            stack.setData(DataComponentTypes.LORE, ItemLore.lore(lines));
        }

        if (this.amount != null) stack.setAmount(this.amount);
        if (this.unbreakable.value()) stack.setData(DataComponentTypes.UNBREAKABLE, Unbreakable.unbreakable(this.unbreakable.tooltip()));
        if (this.glowing) stack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        if (!this.tooltip) stack.setData(DataComponentTypes.HIDE_TOOLTIP);
        if (!this.additionalTooltip) stack.setData(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP);
        if (this.edible != null) stack.setData(DataComponentTypes.FOOD, this.edible.create());
        if (this.texture != null) stack.setData(DataComponentTypes.PROFILE, this.texture.create());
        if (this.enchantments != null) stack.setData(DataComponentTypes.ENCHANTMENTS, this.enchantments.create());
        if (this.storedEnchantments != null) stack.setData(DataComponentTypes.STORED_ENCHANTMENTS, this.storedEnchantments.create());
        if (this.effects != null) stack.setData(DataComponentTypes.POTION_CONTENTS, this.effects.create());
        if (this.glider) stack.setData(DataComponentTypes.GLIDER);

        // TODO: CustomModelData ?? what did they do it to it....................

        return stack;
    }

    /**
     * Initialize a {@link CommentedConfigurationSection} from a configuration file to establish the settings
     * for the configurable class, will be automatically called when the configuration file is loaded using {@link #reload()}
     * <p>
     * If your class inherits from another configurable class, make sure to call super.loadSettings(config)
     * to save the settings from the parent class
     * <p>
     * A class must be initialized before settings are loaded, If you wish to have a configurable data class style, its best to create a
     * static method that will create a new instance and call this method on the new instance
     * <p>
     * The {@link CommentedConfigurationSection} should never be null, when creating a new section,
     * use {@link #pullSection(CommentedConfigurationSection, String)} to establish new section if it doesn't exist
     *
     * @param config The {@link CommentedConfigurationSection} to load the settings from, this cannot be null.
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.type = Material.getMaterial(config.getString("type", "STONE"));
        this.amount = config.getInt("amount", 1);
        this.name = config.getString("name");
        this.lore = config.getStringList("lore");
        this.glowing = config.getBoolean("glowing", false);
        this.tooltip = config.getBoolean("tooltip", true);
        this.additionalTooltip = config.getBoolean("additional-tooltip", true);
        this.glider = config.getBoolean("glider", false);

        // advanced component values <3
        this.unbreakable.loadSettings(this.pullSection(config, "unbreakable"));
        this.enchantments.loadSettings(this.pullSection(config, "enchants"));
        this.storedEnchantments.loadSettings(this.pullSection(config, "stored-enchants"));
        this.effects.loadSettings(this.pullSection(config, "effects"));
        this.texture.loadSettings(this.pullSection(config, "texture"));

        // these components are extra weird and cant really be null
        CommentedConfigurationSection edible = config.getConfigurationSection("edible");
        if (edible != null) {
            this.edible = new ItemEdible(1, 1, false);
            this.edible.loadSettings(edible);
        }
    }

    /**
     * Serialize the settings of the configurable class into a {@link CommentedConfigurationSection} to be saved later
     * <p>
     * This functionality will not update the configuration file, it will only save the settings into the section to be saved later.
     * <p>
     * The function {@link #reload()} will save the settings on first load, please override this method if you wish to save the settings regularly
     * New sections should be created using {@link #pullSection(CommentedConfigurationSection, String)}
     *
     * @param config The {@link CommentedConfigurationSection} to save the settings to, this cannot be null.
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("type", this.type.name());
        config.set("amount", this.amount);
        config.set("name", this.name);
        config.set("lore", this.lore);
        config.set("glowing", this.glowing);
        config.set("tooltip", this.tooltip);
        config.set("additional-tooltip", this.additionalTooltip);
        config.set("glider", this.glider);

        // advanced component values <3
        if (this.unbreakable != null) this.unbreakable.saveSettings(this.pullSection(config, "unbreakable"));
        if (this.enchantments != null) this.enchantments.saveSettings(this.pullSection(config, "enchants"));
        if (this.storedEnchantments != null) this.storedEnchantments.saveSettings(this.pullSection(config, "stored-enchants"));
        if (this.effects != null) this.effects.saveSettings(this.pullSection(config, "effects"));
        if (this.texture != null) this.texture.saveSettings(this.pullSection(config, "texture"));
        if (this.edible != null) this.edible.saveSettings(this.pullSection(config, "edible"));
    }

    /**
     * Deserialize the item construct from a configuration file
     *
     * @param config The configuration file to deserialize
     *
     * @return The item construct
     */
    public static ItemConstruct deserialize(CommentedConfigurationSection config) {
        ItemConstruct construct = ItemConstruct.of(Material.STONE);
        construct.loadSettings(config);
        return construct;
    }

    /**
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "ItemConstruct{" +
               "type=" + this.type +
               ", amount=" + this.amount +
               ", name='" + this.name + '\'' +
               ", lore=" + this.lore +
               ", enchantments=" + this.enchantments +
               ", storedEnchantments=" + this.storedEnchantments +
               ", effects=" + this.effects +
               ", texture=" + this.texture +
               ", edible=" + this.edible +
               ", unbreakable=" + this.unbreakable +
               ", glowing=" + this.glowing +
               ", tooltip=" + this.tooltip +
               ", additionalTooltip=" + this.additionalTooltip +
               ", glider=" + this.glider +
               '}';
    }

    public Material type() {
        return type;
    }

    public ItemConstruct setType(Material type) {
        this.type = type;
        return this;
    }

    public Integer amount() {
        return amount;
    }

    public ItemConstruct amount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public String name() {
        return name;
    }

    public ItemConstruct name(String name) {
        this.name = name;
        return this;
    }

    public List<String> lore() {
        return lore;
    }

    public ItemConstruct lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemConstruct lore(String... lore) {
        this.lore = List.of(lore);
        return this;
    }

    public ItemEnchant enchantments() {
        return enchantments;
    }

    public ItemConstruct enchantments(ItemEnchant enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    public ItemEnchant storedEnchantments() {
        return storedEnchantments;
    }

    public ItemConstruct storedEnchantments(ItemEnchant storedEnchantments) {
        this.storedEnchantments = storedEnchantments;
        return this;
    }

    public ItemEffect effects() {
        return effects;
    }

    public ItemConstruct effects(ItemEffect effects) {
        this.effects = effects;
        return this;
    }

    public ItemTexture texture() {
        return texture;
    }

    public ItemConstruct texture(String texture) {
        this.texture = new ItemTexture(texture);
        return this;
    }

    public ItemConstruct texture(ItemTexture texture) {
        this.texture = texture;
        return this;
    }

    public ItemEdible edible() {
        return edible;
    }

    public ItemConstruct edible(ItemEdible edible) {
        this.edible = edible;
        return this;
    }

    public ItemValue<Boolean> unbreakable() {
        return unbreakable;
    }

    public ItemConstruct unbreakable(ItemValue<Boolean> unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public boolean glowing() {
        return glowing;
    }

    public ItemConstruct glowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public boolean tooltip() {
        return tooltip;
    }

    public ItemConstruct tooltip(boolean tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public boolean additionalTooltip() {
        return additionalTooltip;
    }

    public ItemConstruct additionalTooltip(boolean additionalTooltip) {
        this.additionalTooltip = additionalTooltip;
        return this;
    }

    public boolean glider() {
        return glider;
    }

    public ItemConstruct glider(boolean glider) {
        this.glider = glider;
        return this;
    }

}
