package dev.oribuin.fishing.command.impl.codex;

import dev.oribuin.fishing.command.argument.TierArgument;
import dev.oribuin.fishing.gui.MenuRegistry;
import dev.oribuin.fishing.gui.codex.impl.FishCodexMenu;
import dev.oribuin.fishing.model.fish.Tier;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;

public class CodexFishCommand extends BaseRoseCommand {

    public CodexFishCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Tier tier) {
        Player sender = (Player) context.getSender(); // todo: allow /fishing codex fish <tier> [player]
        MenuRegistry.get(FishCodexMenu.class).open(sender, tier);
    }

    /**
     * @return The command information
     */
    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("fish")
                .arguments(ArgumentsDefinition.of("tier", new TierArgument()))
                .descriptionKey("command-codex-fish")
                .playerOnly(true) // todo: allow /fishing codex fish <tier> [player]
                .permission("fishing.codex.fish")
                .build();
    }

}
