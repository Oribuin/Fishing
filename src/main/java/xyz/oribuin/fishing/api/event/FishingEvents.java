package xyz.oribuin.fishing.api.event;

import xyz.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import xyz.oribuin.fishing.api.event.impl.FishCatchEvent;
import xyz.oribuin.fishing.api.event.impl.FishGenerateEvent;
import xyz.oribuin.fishing.api.event.impl.FishGutEvent;
import xyz.oribuin.fishing.api.event.impl.InitialFishCatchEvent;

/**
 * A global list of methods that can be used to modify all the relevant events in the plugin
 * <p>
 * This is to be used as a template reference for all augments, skills and other classes that need to modify the fish events
 * <p>
 * If you are implementing these methods, They should be registered through {@link FishEventHandler#register(FishEventHandler.EventWrapper)} class.
 */
public interface FishingEvents {

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
    default void onInitialCatch(InitialFishCatchEvent event, int level) {}

    /**
     * The functionality provided when a fish is generated, Use this to modify the fish that are caught
     * <p>
     * Use {@link InitialFishCatchEvent#setAmountToCatch(int)} to change the amount of fish caught in the initial catch
     * <p>
     * Use {@link FishGenerateEvent#addIncrease(double)} to change the chances of catching a fish
     * <p>
     * Use {@link ConditionCheckEvent} to modify the conditions that are checked
     *
     * @param event The event that was called when the fish was generated
     * @param level The level of the ability that was used, if applicable (0 if not)
     */
    default void onFishGenerate(FishGenerateEvent event, int level) {}

    /**
     * The functionality provided when a player has finished catching a fish, Use this to modify the rewards given to the player once caught
     * <p>
     * Use {@link FishCatchEvent#entropy(int)} to change the entropy received
     * <p>
     * Use {@link FishCatchEvent#naturalExp(float)} to change the minecraft experience received
     * <p>
     * Use {@link FishCatchEvent#fishExp(int)} to change the fishing experience received
     *
     * @param event The event that was called when the fish was caught
     * @param level The level of the ability that was used, if applicable (0 if not)
     */
    default void onFishCatch(FishCatchEvent event, int level) {}

    /**
     * The functionality provided when a player has gutted a fish, Use this to modify the rewards given to the player once gutted
     *
     * @param event The event that was called when the fish was gutted
     * @param level The level of the ability that was used, if applicable (0 if not)
     */
    default void onFishGut(FishGutEvent event, int level) {}

    /**
     * The functionality provided when the plugin checks if a player could catch a fish. Use this to modify the outcome of the check
     * <p>
     * Use {@link ConditionCheckEvent#result(boolean)} change the result of the condition check
     * Use {@link FishGenerateEvent#addIncrease(double)} to change the chances of catching a fish
     *
     * @param event The event that was called when the fish was gutted
     * @param level The level of the ability that was used, if applicable (0 if not)
     */
    default void onConditionCheck(ConditionCheckEvent event, int level) {}

}
