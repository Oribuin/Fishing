package dev.oribuin.fishing.command.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.command.FishCommand;
import dev.oribuin.fishing.model.augment.Augment;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.HashMap;
import java.util.Map;

public class ApplyCommand implements FishCommand {

    private final FishingPlugin plugin;

    /**
     * Create a new command instance with the provided plugin instance.
     *
     * @param plugin The plugin instance.
     */
    public ApplyCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Give an augment to a specified player
     *
     * @param sender  The sender running the command
     * @param target  The target receiving the augment
     * @param augment The augment being applied
     * @param level   The level of augment being applied
     */
    @Command("fishing|fish apply <target> augment <augment> <level>")
    @Permission("fishing.give")
    @CommandDescription("Gives the player a specified augment")
    public void execute(CommandSender sender, Player target, Augment augment, Integer level) {

        ItemStack item = target.getInventory().getItemInMainHand();
        if (item.getType() != Material.FISHING_ROD) {
            target.sendMessage("You must be holding a fishing rod to apply an augment.");
            return;
        }

        // Get the augment from the argument
        Map<Augment, Integer> augments = new HashMap<>(this.plugin.getAugmentManager().from(item));
        augments.put(augment, Math.min(level, augment.getMaxLevel()));

        // Apply the augment to the fishing rod
        this.plugin.getAugmentManager().save(item, augments);

        target.sendMessage("Successfully applied the augment to the fishing rod.");
    }

}
