package dev.oribuin.fishing.command;

import dev.oribuin.fishing.command.impl.ApplyCommand;
import dev.oribuin.fishing.command.impl.CodexCommand;
import dev.oribuin.fishing.command.impl.MenuCommand;
import dev.oribuin.fishing.command.impl.GiveCommand;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.HelpCommand;
import dev.rosewood.rosegarden.command.ReloadCommand;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandInfo;

public class FishCommand extends BaseRoseCommand {

    public FishCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    /**
     * Define the information for the command.
     *
     * @return The command information.
     */
    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("fish")
                .aliases("fishing")
                .arguments(this.createArguments())
                .build();
    }

    /**
     * Define the arguments for the command.
     *
     * @return The arguments required for the command.
     */
    private ArgumentsDefinition createArguments() {
        return ArgumentsDefinition.builder()
                .requiredSub(
                        new HelpCommand(this.rosePlugin, this),
                        new ReloadCommand(this.rosePlugin),
                        new ApplyCommand(this.rosePlugin),
                        new MenuCommand(this.rosePlugin),
                        new GiveCommand(this.rosePlugin),
                        new CodexCommand(this.rosePlugin)
                );
    }

}

