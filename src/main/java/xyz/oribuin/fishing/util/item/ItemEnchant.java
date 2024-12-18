package xyz.oribuin.fishing.util.item;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.util.FishUtils;

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
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("tooltip", this.tooltip);
        this.enchantments.forEach((enchantment, level) ->
                config.set("enchantments." + enchantment.key().namespace(), level)
        );

    }

    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
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