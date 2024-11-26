package xyz.oribuin.fishing.command.impl.admin;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import xyz.oribuin.fishing.command.impl.admin.give.GiveAugmentCommand;
import xyz.oribuin.fishing.command.impl.admin.give.GiveFishCommand;
import xyz.oribuin.fishing.command.impl.admin.give.GiveTotemCommand;

public class GiveCommand extends BaseRoseCommand {

    public GiveCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("give")
                .descriptionKey("command-give-description")
                .permission("fishing.admin")
                .arguments(this.createArguments())
                .build();
    }

    private ArgumentsDefinition createArguments() {
        return ArgumentsDefinition.builder()
                .requiredSub(
                        new GiveAugmentCommand(this.rosePlugin),
                        new GiveFishCommand(this.rosePlugin),
                        new GiveTotemCommand(this.rosePlugin)
                );
    }

}
