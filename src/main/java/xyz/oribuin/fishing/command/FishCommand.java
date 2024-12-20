package xyz.oribuin.fishing.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.HelpCommand;
import dev.rosewood.rosegarden.command.ReloadCommand;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import xyz.oribuin.fishing.command.impl.ApplyCommand;
import xyz.oribuin.fishing.command.impl.MenuCommand;
import xyz.oribuin.fishing.command.impl.admin.GiveCommand;

public class FishCommand extends BaseRoseCommand {

    public FishCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("fish")
                .aliases("fishing")
                .arguments(this.createArguments())
                .build();
    }

    private ArgumentsDefinition createArguments() {
        return ArgumentsDefinition.builder()
                .requiredSub(
                        new HelpCommand(this.rosePlugin, this),
                        new ReloadCommand(this.rosePlugin),
                        new ApplyCommand(this.rosePlugin),
                        new MenuCommand(this.rosePlugin),
                        new GiveCommand(this.rosePlugin)
                );
    }

}

