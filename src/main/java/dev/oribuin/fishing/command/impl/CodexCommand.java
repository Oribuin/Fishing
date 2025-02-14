package dev.oribuin.fishing.command.impl;

import dev.oribuin.fishing.command.impl.codex.CodexFishCommand;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandInfo;

public class CodexCommand extends BaseRoseCommand {
    
    public CodexCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    /**
     * @return The command information
     */
    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("codex")
                .descriptionKey("command-codex-description")
                .permission("fishing.codex")
                .arguments(this.createArguments())
                .build();
    }

    private ArgumentsDefinition createArguments() {
        return ArgumentsDefinition.builder().requiredSub(
                new CodexFishCommand(this.rosePlugin)
        );
    }
}
