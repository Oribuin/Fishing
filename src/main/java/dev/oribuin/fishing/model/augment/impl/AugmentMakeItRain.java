package dev.oribuin.fishing.model.augment.impl;

/**
 * Chance for it to rain ~8 random fish ontop of you when catching fish
 */
// TODO: Redo this entire augment
public class AugmentMakeItRain {
    //        extends Augment {
    //
    //    @Comment("The required formula for the augment to trigger")
    //    private String formula = "%level% * 0.5";
    //    private final Option<Integer> MIN_ATTEMPTS = new Option<>(INTEGER, 2, "Minimum fish to be spawned");
    //    private final Option<Integer> MAX_ATTEMPTS = new Option<>(INTEGER, 5, "Maximum fish to be spawned");
    //
    //    
    //    /**
    //     * Create a new type of augment with a name and description.
    //     * <p>
    //     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
    //     */
    //    public AugmentMakeItRain() {
    //        super("make_it_rain");
    //
    //        this.register(InitialFishCatchEvent.class, this::onInitialCatch);
    //        this.register(PlayerFishEvent.class, this::onBite);
    //    }
    //
    //    /**
    //     * The functionality provided when a player is first starting to catch a fish, Use this to determine how many fish should be generated.
    //     * <p>
    //     * Use {@link InitialFishCatchEvent#setAmountToCatch(int)} to set the amount of fish to catch
    //     * <p>
    //     * Use {@link FishGenerateEvent#addIncrease(double)} to change the chances of catching a fish
    //     *
    //     * @param event The event that was called when the fish was caught
    //     * @param level The level of the ability that was used, if applicable (0 if not)
    //     */
    //    @Override
    //    public void onInitialCatch(InitialFishCatchEvent event, int level) {
    //        Placeholders plc = Placeholders.of("level", level);
    //        double chance = FishUtils.evaluate(plc.apply(FORMULA.value()));
    //        double current = this.random.nextDouble(100);
    //        if (current <= chance) return;
    //
    //        List<Fish> result = new ArrayList<>();
    //        for (int i = MIN_ATTEMPTS.value(); i < MAX_ATTEMPTS.value(); i++) {
    //            Fish fish = this.generateFish(event);
    //            if (fish != null) result.add(fish);
    //        }
    //
    //        Location location = event.getPlayer().getLocation().clone();
    //        ParticleBuilder rainCloud = new ParticleBuilder(Particle.FALLING_DUST) // falling dust looks better than cloud
    //                .data(Material.WHITE_CONCRETE.createBlockData())
    //                .count(20)
    //                .extra(0)
    //                .offset(0.5, 0.5, 0.5);
    //
    //        result.forEach(fish -> {
    //            double randX = FishUtils.RANDOM.nextDouble(-1, 1);
    //            double randZ = FishUtils.RANDOM.nextDouble(-1, 1);
    //            Location spawnPoint = location.clone().add(randX, 4, randZ);
    //
    //            location.getWorld().dropItem(spawnPoint, fish.createItemStack());
    //            rainCloud.clone().location(spawnPoint)
    //                    .receivers(10)
    //                    .spawn(); // spawn rain clouds
    //        });
    //    }
    //
    //    /**
    //     * Basic generation of different fish 
    //     * @param event The catch event
    //     * @return The fish that was generated
    //     */
    //    @Nullable
    //    private Fish generateFish(InitialFishCatchEvent event) {
    ////        TierManager tierProvider = FishingPlugin.get().getManager(TierManager.class);
    ////
    ////        Tier quality = tierProvider.selectTier(FishUtils.RANDOM.nextDouble(100));
    ////        if (quality == null) return null;
    ////
    ////        // Make sure the quality is not null
    ////        List<Fish> canCatch = quality.fish().values().stream()
    ////                .filter(x -> ConditionRegistry.check(x, event.getPlayer(), event.getRod(), event.getHook()))
    ////                .toList();
    ////
    ////        if (canCatch.isEmpty()) return null;
    ////
    ////        // Pick a random fish from the list
    ////        return canCatch.get(FishUtils.RANDOM.nextInt(canCatch.size()));
    //        return null;
    //    }
    //
    //    /**
    //     * Information about the augment which will be displayed in top of the augment configuration file
    //     *
    //     * @return The comments for the augment
    //     */
    ////    @Override
    ////    public List<String> comments() {
    ////        return List.of(
    ////                "Augment [Make It Rain] - Chance of spawning additional fish falling from the sky",
    ////                "in a single catch.",
    ////                "",
    ////                "chance-formula: The formula to calculate the chance this augment triggers",
    ////                "min-attempts: The minimum additional fish caught",
    ////                "max-attempts: The maximum additional fish caught"
    ////        );
    ////    }
}
