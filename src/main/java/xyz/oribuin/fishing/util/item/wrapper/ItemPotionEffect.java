package xyz.oribuin.fishing.util.item.wrapper;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.util.FishUtils;

public final class ItemPotionEffect implements Configurable {

    private PotionEffectType type;
    private int duration;
    private int amp;
    private boolean particles;

    /**
     * Create a new potion effect for the item builder class
     *
     * @param type     The type of potion
     * @param duration The duration of the potion
     * @param amp      The potion level +1
     */
    public ItemPotionEffect(PotionEffectType type, int duration, int amp) {
        this.type = type;
        this.duration = duration;
        this.amp = amp;
        this.particles = false;
    }

    /**
     * Create a new potion effect from the builder
     *
     * @return The potion effect
     */
    public PotionEffect create() {
        return new PotionEffect(this.type, this.duration, this.amp, false, this.particles);
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
    public static ItemPotionEffect of(PotionEffectType type, int duration, int amp) {
        return new ItemPotionEffect(type, duration, amp);
    }

    /**
     * Create a new potion effect from the builder
     *
     * @param config The configuration section to load the potion effect from
     *
     * @return The potion effect
     */
    public static ItemPotionEffect of(CommentedConfigurationSection config) {
        ItemPotionEffect itemEffect = new ItemPotionEffect(PotionEffectType.SPEED, 0, 0);
        itemEffect.loadSettings(config);
        return itemEffect;
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("type", this.type.key().value());
        config.set("duration", this.duration);
        config.set("amplifier", this.amp);
        config.set("particles", this.particles);
    }

    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        NamespacedKey typeKey = FishUtils.key(config.getString("type"));
        if (typeKey == null) return;

        this.type = Registry.POTION_EFFECT_TYPE.get(typeKey); // paper registry is a bit weird with potion effects and not finished
        this.duration = config.getInt("duration");
        this.amp = config.getInt("amplifier");
        this.particles = config.getBoolean("particles");
    }

    /**
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "ItemPotionEffect{" +
               "type=" + type +
               ", duration=" + duration +
               ", amp=" + amp +
               ", particles=" + particles +
               '}';
    }

    public PotionEffectType type() {
        return type;
    }

    public void type(PotionEffectType type) {
        this.type = type;
    }

    public int duration() {
        return duration;
    }

    public void duration(int duration) {
        this.duration = duration;
    }

    public int amp() {
        return amp;
    }

    public void amp(int amp) {
        this.amp = amp;
    }

    public boolean particles() {
        return particles;
    }

    public void particles(boolean particles) {
        this.particles = particles;
    }

}