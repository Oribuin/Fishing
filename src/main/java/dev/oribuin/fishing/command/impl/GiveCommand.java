package dev.oribuin.fishing.command.impl;

import dev.oribuin.fishing.command.impl.admin.give.GiveAugmentCommand;
import dev.oribuin.fishing.command.impl.admin.give.GiveFishCommand;
import dev.oribuin.fishing.command.impl.admin.give.GiveTotemCommand;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandInfo;

public class GiveCommand extends BaseRoseCommand {

    /**
     * Create a new command instance with the provided plugin instance.
     *
     * @param rosePlugin The plugin instance.
     */
    public GiveCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    /**
     * Define the information for the command.
     *
     * @return The command information.
     */
    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("give")
                .descriptionKey("command-give-description")
                .permission("fishing.admin")
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
                        new GiveAugmentCommand(this.rosePlugin),
                        new GiveFishCommand(this.rosePlugin),
                        new GiveTotemCommand(this.rosePlugin)
                );
    }

}
