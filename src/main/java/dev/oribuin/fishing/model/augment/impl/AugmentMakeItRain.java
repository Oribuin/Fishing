package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.impl.FishCatchEvent;
import dev.oribuin.fishing.api.event.impl.InitialFishCatchEvent;
import dev.oribuin.fishing.manager.TierManager;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.condition.ConditionRegistry;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Chance for it to rain ~8 random fish ontop of you when catching fish
 */
public class AugmentMakeItRain extends Augment {

    private Map<UUID, Long> cooldown = new HashMap<>();
    private String chanceFormula = "%level% * 0.5";
    private int minAttempts = 2;
    private int maxAttempts = 6;

    public static final Duration COOLDOWN = Duration.ofSeconds(10);

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentMakeItRain() {
        super("make_it_rain");

        this.register(InitialFishCatchEvent.class, this::onInitialCatch);
        this.register(PlayerFishEvent.class, this::onBite);
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
        StringPlaceholders plc = StringPlaceholders.of("level", level);
        double chance = FishUtils.evaluate(plc.apply(this.chanceFormula));
//        double current = this.random.nextDouble(100);
//        if (current <= chance) return;

        List<Fish> result = new ArrayList<>();
        for (int i = this.minAttempts; i < this.maxAttempts; i++) {
            Fish fish = this.generateFish(event);
            if (fish != null) result.add(fish);
        }

        Location location = event.getPlayer().getLocation().clone();
        result.forEach(fish -> {
            double randX = FishUtils.RANDOM.nextDouble(-0.5, 0.5);
            double randZ = FishUtils.RANDOM.nextDouble(-0.5, 0.5);

            location.getWorld().dropItem(location.clone().add(randX, 3, randZ), fish.createItemStack());
        });
    }

    /**
     * Basic generation of different fish 
     * @param event The catch event
     * @return The fish that was generated
     */
    @Nullable
    private Fish generateFish(InitialFishCatchEvent event) {
        TierManager tierProvider = FishingPlugin.get().getManager(TierManager.class);

        Tier quality = tierProvider.selectTier(FishUtils.RANDOM.nextDouble(100));
        if (quality == null) return null;

        // Make sure the quality is not null
        List<Fish> canCatch = quality.fish().values().stream()
                .filter(x -> ConditionRegistry.check(x, event.getPlayer(), event.getRod(), event.getHook()))
                .toList();

        if (canCatch.isEmpty()) return null;

        // Pick a random fish from the list
        return canCatch.get(FishUtils.RANDOM.nextInt(canCatch.size()));
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
        this.minAttempts = config.getInt("min-attempts", 2);
        this.maxAttempts = config.getInt("max-attempts", 6);
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
        config.set("min-fish", this.minAttempts);
        config.set("max-fish", this.maxAttempts);
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
    @Override
    public List<String> comments() {
        return List.of(
                "Augment [Make It Rain] - Chance of spawning additional fish falling from the sky",
                "in a single catch.",
                "",
                "chance-formula: The formula to calculate the chance this augment triggers",
                "min-attempts: The minimum additional fish caught",
                "max-attempts: The maximum additional fish caught"
        );
    }
}
