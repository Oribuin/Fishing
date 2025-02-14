package dev.oribuin.fishing.command.impl.codex;

import dev.oribuin.fishing.gui.MenuRegistry;
import dev.oribuin.fishing.gui.codex.impl.AugmentCodexMenu;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;

public class CodexAugmentCommand extends BaseRoseCommand {

    public CodexAugmentCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player sender = (Player) context.getSender(); // todo: allow /fishing codex augment [player]
        MenuRegistry.get(AugmentCodexMenu.class).open(sender);
    }

    /**
     * @return The command information
     */
    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("augment")
                .descriptionKey("command-codex-augment")
                .playerOnly(true) // todo: allow /fishing codex [player]
                .permission("fishing.codex.augment")
                .build();
    }

}
