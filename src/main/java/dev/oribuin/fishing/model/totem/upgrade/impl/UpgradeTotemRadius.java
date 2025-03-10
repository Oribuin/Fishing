package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.config.Configurable;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A totem upgrade that increases the effective range of the totem
 */
public class UpgradeTotemRadius extends TotemUpgrade implements Configurable {

    private String radiusFormula = "%level% * 5"; // The formula to calculate the radius of the totem (5 blocks per level)

    /**
     * Create a new totem upgrade with the name "radius"
     */
    public UpgradeTotemRadius() {
        super("radius", "Increases the effective range of the totem");

        this.defaultLevel(1);
        this.maxLevel(5);
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
                .add("value", this.calculateRadius(totem))
                .build();
    }

    /**
     * Calculate the radius of the totem based on the level of the upgrade.
     * <p>
     * Radius is divided by 2 so it acts as a radius instead of a diameter.
     *
     * @param totem The totem to calculate the radius for
     *
     * @return The radius of the totem
     */
    public int calculateRadius(Totem totem) {
        Integer level = totem.getProperty(this.key(), this.defaultLevel());
        StringPlaceholders plc = StringPlaceholders.of("level", level);
        return (int) FishUtils.evaluate(plc.apply(this.radiusFormula)) / 2;
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

        config.set("radius-formula", this.radiusFormula);
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

        this.radiusFormula = config.getString("radius-formula", this.radiusFormula);
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
    @Override
    public List<String> comments() {
        return List.of(
                "Totem Upgrade [Radius] - Increases the effective range of the totem",
                "",
                "This upgrade will increase the radius of the totem by a set amount"
        );
    }

}
