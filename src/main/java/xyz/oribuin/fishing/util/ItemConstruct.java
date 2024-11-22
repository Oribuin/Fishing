package xyz.oribuin.fishing.util;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.util.nms.SkullUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Create a config system that will apply itemstack values to be serialized/deserialized from
 * config files
 */
@SuppressWarnings({ "deprecation", "unused" })
public class ItemConstruct {

    public static ItemConstruct EMPTY = ItemConstruct.of(Material.STONE); // Empty itemstack

    private final Material type;
    private int amount;
    private String name;
    private List<String> lore;
    private int model;
    private Map<Enchantment, Integer> enchantment;
    private boolean unbreakable;
    private List<ItemFlag> flags;
    private String texture;
    private List<Effect> effects;
    private Color color;

    /**
     * Create a new Item Construct with a Material
     *
     * @param type The Material
     */
    private ItemConstruct(Material type) {
        this.type = type;
        this.amount = 1;
    }

    /**
     * Create a new Item Construct with a Material
     *
     * @param type The Material
     *
     * @return The itemstack constructor
     */
    public static ItemConstruct of(Material type) {
        return new ItemConstruct(type);
    }

    /**
     * Convert the itemstack into a configuration section
     *
     * @param config The configuration section
     */
    public void serialize(CommentedConfigurationSection config) {
        if (config == null) return;

        config.set("type", this.type.name());
        config.set("amount", this.amount);
        if (this.name != null) config.set("name", this.name);
        if (this.lore != null && !this.lore.isEmpty()) config.set("lore", this.lore);
        if (this.model != 0) config.set("model", this.model);
        if (this.enchantment != null) {
            for (Map.Entry<Enchantment, Integer> entry : this.enchantment.entrySet()) {
                config.set("enchantments." + entry.getKey().getKey().getKey(), entry.getValue());
            }
        }

        config.set("unbreakable", this.unbreakable);
        if (this.flags != null && !this.flags.isEmpty()) config.set("flags", this.flags);
        if (this.texture != null) config.set("texture", this.texture);
        if (this.color != null) config.set("color", this.color.asRGB());
        if (this.effects != null && !this.effects.isEmpty()) {
            for (int i = 0; i < this.effects.size(); i++) {
                Effect effect = this.effects.get(i);
                config.set("effects." + i + ".type", effect.type.getName());
                config.set("effects." + i + ".duration", effect.duration);
                config.set("effects." + i + ".amplifier", effect.amp);
            }
        }
    }

    /**
     * Convert a config section into a new itemstack
     *
     * @param config The configuration section
     *
     * @return The itemstack
     */
    public static ItemConstruct deserialize(CommentedConfigurationSection config) {
        if (config == null) return null;

        Material type = Material.getMaterial(config.getString("type", "STONE"));
        if (type == null || type == Material.AIR) return null;

        ItemConstruct construct = ItemConstruct.of(type);
        construct.amount(config.getInt("amount", 1));
        construct.name(config.getString("name"));
        construct.lore(config.getStringList("lore"));
        construct.model(config.getInt("model"));
        construct.unbreakable(config.getBoolean("unbreakable"));
        construct.flagSerialize(config.getStringList("flags"));
        construct.texture(config.getString("texture"));
        construct.color(Color.fromRGB(config.getInt("color")));

        // Serialize the enchants
        CommentedConfigurationSection enchantments = config.getConfigurationSection("enchantments");
        if (enchantments != null) {
            for (String key : enchantments.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key));
                if (enchantment == null) continue;

                construct.enchant(enchantment, enchantments.getInt(key));
            }
        }

        // Serialize the effects
        CommentedConfigurationSection effects = config.getConfigurationSection("effects");
        if (effects != null) {
            List<Effect> effectList = new ArrayList<>();
            for (String key : effects.getKeys(false)) {
                String typeStr = effects.getString(key + ".type");
                if (typeStr == null) continue;

                PotionEffectType potionType = PotionEffectType.getByName(typeStr);
                int duration = effects.getInt(key + ".duration");
                int amplifier = effects.getInt(key + ".amplifier");
                effectList.add(Effect.of(potionType, duration, amplifier));
            }

            construct.effects(effectList);
        }

        return construct;
    }

    /**
     * Serialize the itemconstruct to a new itemstack with no placeholders
     *
     * @return The itemstack
     */
    public ItemStack build() {
        return this.build(StringPlaceholders.empty());
    }

    /**
     * Serialize the itemconstruct to a new itemstack
     *
     * @param placeholders The placeholders to be applied to the Name and Lore
     *
     * @return The itemstack
     */
    public ItemStack build(StringPlaceholders placeholders) {
        ItemStack stack = new ItemStack(this.type, this.amount);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack; // Probably air

        // REGULAR STUFF :D
        if (this.name != null) meta.setDisplayName(HexUtils.colorify(placeholders.apply(this.name)));
        if (this.lore != null && !this.lore.isEmpty()) {
            List<String> lore = new ArrayList<>();
            for (String line : this.lore) lore.add(HexUtils.colorify(placeholders.apply(line)));
            meta.setLore(lore);
        }
        if (this.model != 0) meta.setCustomModelData(this.model);
        if (this.amount > 1) stack.setAmount(this.amount);
        if (this.unbreakable) meta.setUnbreakable(true);
        if (this.flags != null && !this.flags.isEmpty()) this.flags.forEach(meta::addItemFlags);
        if (this.enchantment != null) {
            for (Map.Entry<Enchantment, Integer> entry : this.enchantment.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }

        // Apply the new color
        if (this.color != null) {
            if (meta instanceof LeatherArmorMeta armorMeta) armorMeta.setColor(this.color);
            if (meta instanceof PotionMeta potionMeta) potionMeta.setColor(this.color);
        }

        // Apply the potion effects
        if (this.effects != null && !this.effects.isEmpty() && meta instanceof PotionMeta potionMeta) {
            for (Effect effect : this.effects) {
                potionMeta.addCustomEffect(effect.create(), true);
            }
        }

        // Apply the skull texture
        if (this.texture != null && meta instanceof SkullMeta skullMeta) {
            SkullUtils.setSkullTexture(skullMeta, this.texture);
        }

        // Apply the item meta
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Apply the itemstack amount
     *
     * @param amount The amount of the item
     *
     * @return The itemstack constructor
     */
    public ItemConstruct amount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Apply the itemstack name
     *
     * @param name The name of the item
     *
     * @return The itemstack constructor
     */
    public ItemConstruct name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Apply the itemstack lore
     *
     * @param lore The lore of the item
     *
     * @return The itemstack constructor
     */
    public ItemConstruct lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Apply the itemstack lore
     *
     * @param lore The lore of the item
     *
     * @return The itemstack constructor
     */
    public ItemConstruct lore(String... lore) {
        return this.lore(List.of(lore));
    }

    /**
     * Apply the itemstack enchantments
     *
     * @param model The model of the item
     *
     * @return The itemstack constructor
     */
    public ItemConstruct model(int model) {
        this.model = model;
        return this;
    }

    /**
     * Apply the itemstack enchantments
     *
     * @param enchantments The enchantments
     *
     * @return The itemstack constructor
     */
    public ItemConstruct enchant(Map<Enchantment, Integer> enchantments) {
        this.enchantment = enchantments;
        return this;
    }

    /**
     * Apply a new enchantment to the itemstack
     *
     * @param enchantment The enchantment
     * @param level       The level of the enchantment
     *
     * @return The itemstack constructor
     */
    public ItemConstruct enchant(Enchantment enchantment, int level) {
        this.enchantment.put(enchantment, level);
        return this;
    }

    /**
     * Make the itemstack unbreakable
     *
     * @param unbreakable If the itemstack is unbreakable
     *
     * @return The itemstack constructor
     */
    public ItemConstruct unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    /**
     * Apply the itemstack flags
     *
     * @param flags The flags of the item
     *
     * @return The itemstack constructor
     */
    public ItemConstruct flags(List<ItemFlag> flags) {
        this.flags = flags;
        return this;
    }

    /**
     * Apply the itemstack flags
     *
     * @param flags The flags of the item
     *
     * @return The itemstack constructor
     */
    public ItemConstruct flags(ItemFlag... flags) {
        return this.flags(List.of(flags));
    }

    /**
     * Apply the itemstack flags
     *
     * @param flags The flags of the item
     *
     * @return The itemstack constructor
     */
    public ItemConstruct flagSerialize(List<String> flags) {
        List<ItemFlag> newFlags = new ArrayList<>();
        try {
            for (String flag : flags) {
                newFlags.add(ItemFlag.valueOf(flag));
            }
        } catch (IllegalArgumentException ignored) {
        }

        return this.flags(newFlags);
    }

    /**
     * Apply the itemstack texture
     *
     * @param texture The texture of the item
     *
     * @return The itemstack constructor
     */
    public ItemConstruct texture(String texture) {
        this.texture = texture;
        return this;
    }

    /**
     * Apply the itemstack color
     *
     * @param color The color of the item
     *
     * @return The itemstack constructor
     */
    public ItemConstruct color(Color color) {
        this.color = color;
        return this;
    }

    /**
     * Apply the itemstack color through hex
     *
     * @param hex The hex color
     *
     * @return The itemstack constructor
     */
    public ItemConstruct color(String hex) {
        try {
            java.awt.Color awtColor = java.awt.Color.decode(hex);
            this.color = Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
        } catch (IllegalArgumentException ignored) {
        }

        return this;
    }

    /**
     * Apply the itemstack effects
     *
     * @param effects The effects of the item
     *
     * @return The itemstack constructor
     */
    public ItemConstruct effects(List<Effect> effects) {
        this.effects = effects;
        return this;
    }

    /**
     * Apply the itemstack effects
     *
     * @param effects The effects of the item
     *
     * @return The itemstack constructor
     */
    public ItemConstruct effects(Effect... effects) {
        return this.effects(List.of(effects));
    }

    /**
     * A data class to hold all the potion effects for Potion
     *
     * @param type     The type of potion
     * @param duration The duration of the potion
     * @param amp      The potion level +1
     */
    public record Effect(PotionEffectType type, int duration, int amp) {

        /**
         * Create a new potion effect from the builder
         *
         * @return The potion effect
         */
        public PotionEffect create() {
            return new PotionEffect(this.type, this.duration, this.amp);
        }

        /**
         * Create a new potion effect from the builder
         *
         * @param type     The potion effect type
         * @param duration The duration of the potion effect
         * @param amp      The amplifier of the potion effect
         *
         * @return The potion effect
         */
        public static Effect of(PotionEffectType type, int duration, int amp) {
            return new Effect(type, duration, amp);
        }

    }

}
