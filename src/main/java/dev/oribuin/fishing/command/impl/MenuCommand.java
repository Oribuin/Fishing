package dev.oribuin.fishing.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import dev.oribuin.fishing.gui.StatsMenu;
import dev.oribuin.fishing.manager.MenuManager;

// suppress experimental
public class MenuCommand extends BaseRoseCommand {

    public MenuCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player player = (Player) context.getSender();

        // Open the menu for the player
        StatsMenu menu = MenuManager.from(StatsMenu.class);
        if (menu == null) return;

        menu.open(player);

    }

    /**
     * Define the information for the command.
     *
     * @return The command information.
     */
    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("menu")
                .permission("fishing.menu")
                .playerOnly()
                .build();
    }

}
