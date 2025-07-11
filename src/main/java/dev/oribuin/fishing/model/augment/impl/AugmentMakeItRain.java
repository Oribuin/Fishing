package dev.oribuin.fishing.model.augment.impl;

import com.destroystokyo.paper.ParticleBuilder;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.config.Option;
import dev.oribuin.fishing.api.event.impl.InitialFishCatchEvent;
import dev.oribuin.fishing.manager.TierManager;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.condition.ConditionRegistry;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.rosewood.rosegarden.config.SettingSerializers.INTEGER;
import static dev.rosewood.rosegarden.config.SettingSerializers.STRING;

/**
 * Chance for it to rain ~8 random fish ontop of you when catching fish
 */
public class AugmentMakeItRain extends Augment {

    private final Option<String> FORMULA = new Option<>(STRING, "%level% * 0.5");
    private final Option<Integer> MIN_ATTEMPTS = new Option<>(INTEGER, 2, "Minimum fish to be spawned");
    private final Option<Integer> MAX_ATTEMPTS = new Option<>(INTEGER, 5, "Maximum fish to be spawned");

    
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
        double chance = FishUtils.evaluate(plc.apply(FORMULA.value()));
        double current = this.random.nextDouble(100);
        if (current <= chance) return;

        List<Fish> result = new ArrayList<>();
        for (int i = MIN_ATTEMPTS.value(); i < MAX_ATTEMPTS.value(); i++) {
            Fish fish = this.generateFish(event);
            if (fish != null) result.add(fish);
        }

        Location location = event.getPlayer().getLocation().clone();
        ParticleBuilder rainCloud = new ParticleBuilder(Particle.FALLING_DUST) // falling dust looks better than cloud
                .data(Material.WHITE_CONCRETE.createBlockData())
                .count(20)
                .extra(0)
                .offset(0.5, 0.5, 0.5);

        result.forEach(fish -> {
            double randX = FishUtils.RANDOM.nextDouble(-1, 1);
            double randZ = FishUtils.RANDOM.nextDouble(-1, 1);
            Location spawnPoint = location.clone().add(randX, 4, randZ);

            location.getWorld().dropItem(spawnPoint, fish.createItemStack());
            rainCloud.clone().location(spawnPoint)
                    .receivers(10)
                    .spawn(); // spawn rain clouds
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
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
//    @Override
//    public List<String> comments() {
//        return List.of(
//                "Augment [Make It Rain] - Chance of spawning additional fish falling from the sky",
//                "in a single catch.",
//                "",
//                "chance-formula: The formula to calculate the chance this augment triggers",
//                "min-attempts: The minimum additional fish caught",
//                "max-attempts: The maximum additional fish caught"
//        );
//    }
}
