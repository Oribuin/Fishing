package xyz.oribuin.fishing.util.item;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import io.papermc.paper.datacomponent.item.PotionContents;
import org.bukkit.Color;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.util.item.wrapper.ItemPotionEffect;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class ItemEffect implements Configurable {

    private List<ItemPotionEffect> effects;
    private ItemColor color;

    /**
     * Create a new potion effect for the item builder class
     *
     * @param type     The type of potion
     * @param duration The duration of the potion
     * @param amp      The potion level +1
     */
    public ItemEffect(PotionEffectType type, int duration, int amp) {
        this.effects = new ArrayList<>();
        this.effects.add(new ItemPotionEffect(type, duration, amp));
        this.color = new ItemColor(Color.BLUE);
    }

    /**
     * Create a new empty potion effect for the item builder class
     */
    public ItemEffect() {
        this.effects = new ArrayList<>();
        this.color = new ItemColor(Color.BLUE);
    }

    /**
     * Create a new potion effect from the builder
     *
     * @return The potion effect
     */
    public PotionContents create() {
        PotionContents.Builder builder = PotionContents.potionContents();
        builder.customColor(this.color.create());
        this.effects.forEach(effect -> builder.addCustomEffect(effect.create()));
        return builder.build();
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
    public static ItemEffect of(PotionEffectType type, int duration, int amp) {
        return new ItemEffect(type, duration, amp);
    }

    /**
     * Create a new potion effect from the builder
     *
     * @param config The configuration section to load the potion effect from
     *
     * @return The potion effect
     */
    public static ItemEffect of(CommentedConfigurationSection config) {
        ItemEffect itemEffect = new ItemEffect(PotionEffectType.SPEED, 0, 0);
        itemEffect.loadSettings(config);
        return itemEffect;
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
        this.color.saveSettings(config);
        config.set("effects", null); // reset the configuration section

        for (int i = 0; i < this.effects.size(); i++) {
            ItemPotionEffect effect = this.effects.get(i);
            config.set("effects." + i, effect);
        }
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
        this.color.loadSettings(config);

        this.effects.clear();
        this.pullSection(config, "effects").getKeys(false).forEach(key -> {
            ItemPotionEffect effect = new ItemPotionEffect(PotionEffectType.SPEED, 0, 0); // example
            effect.loadSettings(this.pullSection(config, "effects." + key));
            this.effects.add(effect);
        });
    }

    /**
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "ItemEffect{" +
               "effects=" + effects +
               ", color=" + color +
               '}';
    }

    public List<ItemPotionEffect> effects() {
        return effects;
    }

    public void effects(List<ItemPotionEffect> effects) {
        this.effects = effects;
    }

    public ItemColor color() {
        return color;
    }

    public void color(ItemColor color) {
        this.color = color;
    }

}