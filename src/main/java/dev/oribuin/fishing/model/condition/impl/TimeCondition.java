package dev.oribuin.fishing.model.condition.impl;

import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.model.condition.CatchCondition;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.util.Placeholders;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * A condition that is checked when a player is trying to catch a fish
 * <p>
 * First, {@link #shouldRun(Fish)} is called to check if the fish has the condition type
 * If the fish has the condition type, {@link #check(Fish, Player, ItemStack, FishHook)} is called to check if the player meets the condition to catch the fish
 *
 * @see dev.oribuin.fishing.model.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook)  to see how this is used
 */

//             "The time range where the fish is available to catch",
//            "ANY_TIME = Available All Day",
//            "DAY = 6am -> 6pm",
//            "NIGHT = 6pm -> 6am",
//            "SUNSET = 6pm -> 8pm",
//            "SUNRISE = 6am -> 8am"
@ConfigSerializable
public class TimeCondition extends CatchCondition {

    @Comment("Should the server use system time for the time condition?")
    private boolean useSystemTime = false;

    //    @Comment("The required time ")
    //    private final Option<DefinedTime> requiredTime = new Option<>(ofEnum(DefinedTime.class), DefinedTime.ANY_TIME,
    //            "The time range where the fish is available to catch",
    //            "ANY_TIME = Available All Day",
    //            "DAY = 6am -> 6pm",
    //            "NIGHT = 6pm -> 6am",
    //            "SUNSET = 6pm -> 8pm",
    //            "SUNRISE = 6am -> 8am"
    //    );
    //

    /**
     * Decides whether the condition should be checked in the first place,
     * <p>
     * This is to prevent unnecessary checks on fish that don't have the condition type.
     *
     * @param fish The fish to check for
     *
     * @return true if the fish has the condition applied. @see {@link #check(Fish, Player, ItemStack, FishHook)} for the actual condition check
     */
    @Override
    public boolean shouldRun(Fish fish) {
        return this.enabled;
        //        return requiredTime.value() != DefinedTime.ANY_TIME;
    }

    /**
     * Check if the player meets the condition to catch the fish or not, Requires {@link #shouldRun(Fish)} to return true before running
     * <p>
     * To see how this is used, check {@link dev.oribuin.fishing.model.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook)}
     * <p>
     * All conditions are passed through {@link ConditionCheckEvent} to overwrite the result if needed
     *
     * @param fish   The fish the player is trying to catch
     * @param player The player to check
     * @param rod    The fishing rod the player is using
     * @param hook   The fishhook the player is using
     *
     * @return Results in true if the player can catch the fish
     */
    @Override
    public boolean check(Fish fish, Player player, ItemStack rod, FishHook hook) {
        // TODO: Add time system
        //        if (!useSystemTime.value()) {
        //            return requiredTime.value().matches(player.getWorld());
        //        }
        //
        //        // please actually use the system time
        //        return requiredTime.value().matches(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        return true;
    }

    /**
     * All the placeholders that can be used in the configuration file for this configurable class
     *
     * @return The placeholders
     */
    @Override
    public Placeholders placeholders() {
        return Placeholders.empty();
        //        return Placeholders.builder()
        //                .add("time", StringUtils.capitalize(this..value().name().toLowerCase().replace("_", " ")))
        //                .add("use_system_time", useSystemTime.value())
        //                .build();
    }

}
