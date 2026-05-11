package dev.oribuin.fishing.command.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.command.FishCommand;
import dev.oribuin.fishing.gui.impl.user.FishMainMenu;
import dev.oribuin.fishing.manager.MenuManager;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class MenuCommand implements FishCommand {

    private final FishingPlugin plugin;

    /**
     * Create a new command instance with the provided plugin instance.
     *
     * @param plugin The plugin instance.
     */
    public MenuCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Open the main fishing menu for the player
     *
     * @param sender The sender running the command
     * @param target The target that might open the menu
     */
    @Command("fishing|fish menu [target]")
    @Permission("fishing.menu")
    @CommandDescription("Opens the main menu for the player or target")
    public void executeMainMenu(CommandSender sender, Player target) {
        if (target == null && sender instanceof Player player) target = player;
        if (target == null) return;

        Player finalTarget = target;
        PluginScheduler.get().runTask(() -> MenuManager.get(FishMainMenu.class).open(finalTarget));
    }

    /**
     * Open the fishing stats menu for the player
     *
     * @param sender The sender running the command
     * @param target The target that might open the menu
     */
    @Command("fishing|fish stats [target]")
    @Permission("fishing.stats")
    @CommandDescription("Opens the main menu for the player or target")
    public void executeStatsMenu(CommandSender sender, Player target) {
        if (target == null && sender instanceof Player player) target = player;
        if (target == null) return;

        Player finalTarget = target;
        //        PluginScheduler.get().runTask(() -> MenuManager.get(FishMainMenu.class).open(finalTarget));
    }

}
