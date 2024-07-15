package dev.rosewood.myplugin.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;

public class ExampleCommand extends BaseRoseCommand {

    public ExampleCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target, String message) {
        String toSend = message != null ? message : "Hello!";

        target.sendMessage(String.format("%s -> You: %s", context.getSender().getName(), toSend));
        context.getSender().sendMessage(String.format("You -> %s: %s", target.getName(), toSend));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("example")
                .descriptionKey("command-example-description")
                .permission("rosetemplate.use")
                .aliases("ex")
                .playerOnly(false)
                .arguments(this.createArguments())
                .build();
    }

    private ArgumentsDefinition createArguments() {
        return ArgumentsDefinition.builder()
                .required("player", ArgumentHandlers.PLAYER)
                .optional("message", ArgumentHandlers.GREEDY_STRING)
                .build();
    }

}
