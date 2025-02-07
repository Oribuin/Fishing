package dev.oribuin.fishing.model.item.component;

import dev.oribuin.fishing.config.Configurable;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public final class ItemEnchant implements Configurable {

    private Map<Enchantment, Integer> enchantments;
    private boolean tooltip;

    /**
     * Define the enchants of an item
     *
     * @param enchantment The enchantment to apply
     * @param level       The level of the enchantment
     */
    public ItemEnchant(Enchantment enchantment, int level) {
        this.enchantments = new HashMap<>();
        this.enchantments.put(enchantment, level);
        this.tooltip = true;
    }

    /**
     * Define the texture of an item that can be enchanted
     */
    public ItemEnchant() {
        this.enchantments = new HashMap<>();
        this.tooltip = true;
    }

    /**
     * Create a new potion effect from the builder
     *
     * @return The potion effect
     */
    public ItemEnchantments create() {
        return ItemEnchantments.itemEnchantments(this.enchantments, this.tooltip);
    }

    /**
     * Create a new potion effect from the builder
     *
     * @param config The configuration section to load the potion effect from
     *
     * @return The potion effect
     */
    public static ItemEnchant of(CommentedConfigurationSection config) {
        ItemEnchant effect = new ItemEnchant(Enchantment.SHARPNESS, 1);
        effect.loadSettings(config);
        return effect;
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
        config.set("tooltip", this.tooltip);
        this.enchantments.forEach((enchantment, level) ->
                config.set("enchantments." + enchantment.key().namespace(), level)
        );

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
        this.tooltip = config.getBoolean("tooltip", true);

        CommentedConfigurationSection enchantments = config.getConfigurationSection("enchantments");
        if (enchantments == null) return;

        for (String key : enchantments.getKeys(false)) {
            Enchantment enchantment = FishUtils.REGISTRY.getRegistry(RegistryKey.ENCHANTMENT).get(FishUtils.key(key));
            if (enchantment == null) continue;

            this.enchantments.put(enchantment, enchantments.getInt(key));
        }
    }

    /**
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "ItemEnchant{" +
               "enchantments=" + enchantments +
               ", tooltip=" + tooltip +
               '}';
    }

    public Map<Enchantment, Integer> enchantments() {
        return enchantments;
    }

    public void enchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public boolean tooltip() {
        return tooltip;
    }

    public void tooltip(boolean tooltip) {
        this.tooltip = tooltip;
    }

}