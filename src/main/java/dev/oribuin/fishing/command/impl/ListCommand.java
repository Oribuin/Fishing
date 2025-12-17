package dev.oribuin.fishing.command.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.command.FishCommand;
import dev.oribuin.fishing.model.fish.Fish;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.List;
import java.util.stream.Collectors;

public class ListCommand implements FishCommand {

    private final FishingPlugin plugin;

    /**
     * Create a new command instance with the provided plugin instance.
     *
     * @param plugin The plugin instance.
     */
    public ListCommand(FishingPlugin plugin) {
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
    @Command("fishing|fish list fish")
    @Permission("fishing.list")
    @CommandDescription("Gives the player a specified augment")
    public void listFish(CommandSender sender) {
        String result = this.plugin.getTierManager().getAllFish()
                .stream()
                .map(Fish::getName)
                .collect(Collectors.joining(", "));

        sender.sendMessage(result);
    }

    @Command("fishing|fish list tiers")
    @Permission("fishing.list")
    @CommandDescription("Gives the player a specified augment")
    public void listTiers(CommandSender sender) {
        List<String> tiers = this.plugin.getTierManager().getTiers()
                .keySet()
                .stream()
                .toList();

        sender.sendMessage(String.join(", ", tiers));
    }

}
