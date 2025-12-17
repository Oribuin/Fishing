package dev.oribuin.fishing.model.condition.impl;

import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.model.condition.CatchCondition;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.util.Placeholders;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition that is checked when a player is trying to catch a fish
 * <p>
 * First, {@link #shouldRun(Fish)} is called to check if the fish has the condition type
 * If the fish has the condition type, {@link #check(Fish, Player, ItemStack, FishHook)} is called to check if the player meets the condition to catch the fish
 *
 * @see dev.oribuin.fishing.model.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook)  to see how this is used
 */
@SuppressWarnings({ "deprecation", "removal" })
@ConfigSerializable
public class BiomeCondition extends CatchCondition {

    @Comment("The required list of biomes to catch a specified fish. Add '!' in front of a biome to invert the check")
    private List<String> biomes = new ArrayList<>();

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
        return this.enabled && !this.biomes.isEmpty();
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
        Biome current = hook.getLocation().getBlock().getBiome();
        return this.biomes.stream().anyMatch(x -> {
            Key biomeKey = this.from(x.startsWith("!") ? x.substring(1) : x);
            
            if (x.startsWith("!")) return !current.key().equals(biomeKey);
            else return current.key().equals(biomeKey);
        });
    }

    /**
     * All the placeholders that can be used in the configuration file for this configurable class
     *
     * @return The placeholders
     */
    @Override
    public Placeholders placeholders() {
        return Placeholders.of("biomes",
                this.biomes.isEmpty() ? "Any" : String.join(", ", this.biomes)
        );
    }

    private Key from(String name) {
        @Subst("minecraft:plains") 
        String[] split = name.toLowerCase().split(":");
        if (split.length >= 2) {
            return Key.key(split[0], split[1]);
        }
        
        // Assume minecraft namespace key if none specified
        return Key.key(Key.MINECRAFT_NAMESPACE, split[0]); 
    }

}
