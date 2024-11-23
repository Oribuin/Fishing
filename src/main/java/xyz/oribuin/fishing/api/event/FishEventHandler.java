package xyz.oribuin.fishing.api.event;

/**
 * A global handler to parse any fishing related events, used to detect
 * and modify fish when they are caught, generated and given
 */
public abstract class FishEventHandler {

    /**
     * The functionality provided by the augment when a player catches a fish
     * This is run before the fish are generated, Used to modify the amount of fish caught
     *
     * @param event The initial fish catch event
     * @param level The level of the augment that was used
     */
    public void onInitialCatch(InitialFishCatchEvent event, int level) {
    }

    /**
     * The functionality provided when a fish is generated for the player
     *
     * @param event The event that is fired when a fish is generated
     * @param level The level of the augment that was used
     */
    public void onGenerate(FishGenerateEvent event, int level) {
    }

    /**
     * The functionality provided by the augment when a player obtains a fish from the initial catch
     * This method is run for each fish caught
     *
     * @param event The context of the fish event
     * @param level The level of the augment that was used
     */
    public void onFishCatch(FishCatchEvent event, int level) {
    }

    /**
     * The functionality provided when a condition is checked, used to modify the result of the condition
     *
     * @param event The context of the fish event
     * @param level The level of the augment that was used
     */
    public void onConditionCheck(ConditionCheckEvent event, int level) {
    }

}
