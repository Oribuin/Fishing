package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.config.Configurable;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

import static com.jeff_media.morepersistentdatatypes.DataType.INTEGER;

/**
 * A totem upgrade that decreases the cooldown of the totem once deactivated
 */
public class UpgradeTotemCooldown extends TotemUpgrade implements Configurable {

    private String cooldownFormula = "(60 * 60) - (%level% * 120)"; // The formula to calculate the cooldown of the totem (1 hour - 2 minute per level)

    /**
     * Create a new totem upgrade with the name "radius"
     */
    public UpgradeTotemCooldown() {
        super("cooldown", "Decreases the cooldown of the totem once deactivated");

        this.defaultLevel(0);
        this.maxLevel(15);
    }
    
    /**
     * Calculate the radius of the totem based on the level of the upgrade
     *
     * @param totem The totem to calculate the radius for
     *
     * @return The radius of the totem
     */
    public Duration calculateCooldown(Totem totem) {
        Integer level = totem.getProperty(this.key(), this.defaultLevel());
        StringPlaceholders plc = StringPlaceholders.of("level", level);
        return Duration.ofMillis((long) FishUtils.evaluate(plc.apply(this.cooldownFormula)) * 1000);
    }

    /**
     * The totem upgrade placeholders for the upgrade.
     * All upgrades are added to the totems placeholders as "upgrade_<name>_<placeholder>"
     * <p>
     * Example: upgrade_radius_value
     *
     * @param totem The totem to apply the upgrade to
     *
     * @return The value of the upgrade
     */
    @Override
    public StringPlaceholders placeholders(Totem totem) {
        return StringPlaceholders.builder()
                .addAll(super.placeholders(totem))
                .add("value", FishUtils.formatTime(this.calculateCooldown(totem).toMillis()))
                .add("timer", FishUtils.formatTime(totem.getCurrentCooldown()))
                .build();
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
        super.saveSettings(config);

        config.set("cooldown-formula", this.cooldownFormula);
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
        super.loadSettings(config);

        this.cooldownFormula = config.getString("cooldown-formula", this.cooldownFormula);
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
    @Override
    public List<String> comments() {
        return List.of(
                "Totem Upgrade [Cooldown] - Increases the cooldown of the totem when activated",
                "",
                "This upgrade will increase the cooldown of the totem by a set amount"
        );
    }

}
