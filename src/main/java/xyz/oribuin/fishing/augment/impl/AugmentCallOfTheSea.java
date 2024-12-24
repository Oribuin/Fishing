package xyz.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.event.FishGenerateEvent;
import xyz.oribuin.fishing.api.event.FishingEvents;
import xyz.oribuin.fishing.api.event.InitialFishCatchEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.fish.condition.Weather;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.List;

/**
 * When it is raining, there is a chance to catch multiple fish in a single catch.
 */
public class AugmentCallOfTheSea extends Augment {

    private String chanceFormula = "%level% * 0.05"; // 5% per level
    private int minFish = 1;
    private int maxFish = 3;

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentCallOfTheSea() {
        super("call_of_the_sea", "&7Increases the amount of fish", "&7caught when the weather is raining");

        this.maxLevel(15);
        this.register(InitialFishCatchEvent.class, this::onInitialCatch);
    }

    /**
     * The functionality provided when a player is first starting to catch a fish, Use this to determine how many fish should be generated.
     * <p>
     * Use {@link InitialFishCatchEvent#setAmountToCatch(int)} to set the amount of fish to catch
     * <p>
     * Use {@link FishGenerateEvent#addIncrease(double)} to change the chances of catching a fish
     *
     * @param event The event that was called when the fish was caught
     * @param level The level of the ability that was used, if applicable (0 if not)
     */
    @Override
    public void onInitialCatch(InitialFishCatchEvent event, int level) {
        if (Weather.CLEAR.isState(event.getHook().getLocation())) return;

        StringPlaceholders plc = StringPlaceholders.of("level", level);
        double chance = FishUtils.evaluate(plc.apply(this.chanceFormula));
        if (Math.random() > chance) return;

        int fishCaught = this.minFish + (int) (Math.random() * (this.maxFish - this.minFish));
        event.setAmountToCatch(event.getAmountToCatch() + fishCaught);
        event.getPlayer().sendActionBar(Component.text("You have caught more fish due to the Call of the Sea augment!"));
        // TODO: Tell player that they have caught more fish
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

        this.chanceFormula = config.getString("chance-formula", this.chanceFormula); // 5% per level
        this.minFish = config.getInt("min-fish", 1); // Minimum fish caught
        this.maxFish = config.getInt("max-fish", 3); // Maximum fish caught
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

        config.set("chance-formula", this.chanceFormula);
        config.set("min-fish", this.minFish);
        config.set("max-fish", this.maxFish);
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
    @Override
    public List<String> comments() {
        return List.of(
                "Augment [Call Of The Sea] - When it is raining, there is a chance to catch multiple fish",
                "in a single catch.",
                "",
                "chance-formula: The formula to calculate the chance this augment triggers",
                "min-fish: The minimum additional fish caught",
                "max-fish: The maximum additional fish caught"
        );
    }

}
