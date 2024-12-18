package xyz.oribuin.fishing.command.impl.admin.give;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.command.argument.FishArgument;
import xyz.oribuin.fishing.fish.Fish;

public class GiveFishCommand extends BaseRoseCommand {

    public GiveFishCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target, Fish fish, Integer amount) {
        int fixedAmount = Math.max(1, Math.min(amount == null ? 1 : amount, 64));

        CommandSender sender = context.getSender();
        ItemStack item = fish.createItemStack();
        if (item == null) {
            sender.sendMessage("An error occurred while creating the fish item."); // TODO: Plugin Message
            return;
        }


        if (target.getInventory().firstEmpty() == -1) {
            // TODO: Plugin Message
            sender.sendMessage(target.getName() + "'s inventory is full.");
            return;
        }

        target.getInventory().addItem(item.asQuantity(fixedAmount));
        sender.sendMessage("You have given " + target.getName() + " " + fixedAmount + " " + fish.name() + " fish.");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("fish")
                .descriptionKey("command-give-description")
                .permission("fishing.admin")
                .arguments(this.createArguments())
                .build();
    }

    private ArgumentsDefinition createArguments() {
        return ArgumentsDefinition.builder()
                .required("player", ArgumentHandlers.PLAYER)
                .required("fish", new FishArgument())
                .optional("amount", ArgumentHandlers.INTEGER)
                .build();
    }
}
