package xyz.oribuin.fishing.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.fishing.gui.ExampleMenu;
import xyz.oribuin.fishing.manager.MenuManager;

public class MenuCommand extends BaseRoseCommand {

    public MenuCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player player = (Player) context.getSender();

        // Open the menu for the player
        ExampleMenu menu = MenuManager.from(ExampleMenu.class);
        if (menu == null) return;

        menu.open(player);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("menu")
                .permission("fishing.menu")
                .playerOnly()
                .build();
    }

}
