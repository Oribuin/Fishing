package dev.oribuin.fishing.command.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.command.FishCommand;
import dev.oribuin.fishing.gui.impl.codex.impl.AugmentCodexMenu;
import dev.oribuin.fishing.gui.impl.codex.impl.TierCodexMenu;
import dev.oribuin.fishing.manager.MenuManager;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class CodexCommand implements FishCommand {

    private final FishingPlugin plugin;

    /**
     * Create a new command instance with the provided plugin instance.
     *
     * @param plugin The plugin instance.
     */
    public CodexCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Open the fish codex for a player
     *
     * @param sender The sender running the command
     * @param type   The type of codex being opened
     * @param target The target receiving the augment
     */
    @Command("fishing|fish codex <type> [target]")
    @Permission("fishing.give")
    @CommandDescription("Gives the player a specified augment")
    public void execute(CommandSender sender, CodexType type, Player target) {
        if (target == null && sender instanceof Player player) target = player;
        if (target == null) return;

        Player finalTarget = target;
        PluginScheduler.get().runTask(() -> {
            switch (type) {
                case TIER -> MenuManager.get(TierCodexMenu.class).open(finalTarget);
                case AUGMENT -> MenuManager.get(AugmentCodexMenu.class).open(finalTarget);
                default -> finalTarget.sendMessage("who");
            }
        });
    }

    public enum CodexType {
        TIER,
        AUGMENT,
        //        SKILL,
        //        TOTEM_UPGRADE, 
        // general info

    }
}
