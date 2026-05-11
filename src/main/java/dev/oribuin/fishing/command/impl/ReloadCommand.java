package dev.oribuin.fishing.command.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.command.FishCommand;
import dev.oribuin.fishing.config.impl.PluginMessages;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class ReloadCommand implements FishCommand {

    private final FishingPlugin plugin;

    /**
     * Create a new command instance with the provided plugin instance.
     *
     * @param plugin The plugin instance.
     */
    public ReloadCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Open the main fishing menu for the player
     *
     * @param sender The sender running the command
     */
    @Command("fishing|fish reload")
    @Permission("fishing.reload")
    @CommandDescription("Reloads the plugin")
    public void executeMainMenu(CommandSender sender) {
        long start = System.currentTimeMillis();
        this.plugin.reload();
        PluginMessages.get().getReload().send(sender, "time", System.currentTimeMillis() - start);
    }

}
