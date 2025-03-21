package dev.oribuin.fishing.command.impl.admin.give;

import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.command.argument.AugmentArgument;
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

public class GiveAugmentCommand extends BaseRoseCommand {

    /**
     * Create a new command instance with the provided plugin instance.
     *
     * @param rosePlugin The plugin instance.
     */
    public GiveAugmentCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target, Augment augment, Integer amount) {
        int fixedAmount = Math.max(1, Math.min(amount == null ? 1 : amount, 64));

        CommandSender sender = context.getSender();
        ItemStack item = augment.displayItem().build(augment.placeholders()).asQuantity(fixedAmount);

        if (target.getInventory().firstEmpty() == -1) {
            // TODO: Plugin Message
            sender.sendMessage(target.getName() + "'s inventory is full.");
            return;
        }

        target.getInventory().addItem(item);
        sender.sendMessage("You have given " + target.getName() + " " + fixedAmount + " " + augment.name() + " augment(s).");
    }

    /**
     * Define the information for the command.
     *
     * @return The command information.
     */
    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("augment")
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
                .required("player", ArgumentHandlers.PLAYER)
                .required("augment", new AugmentArgument())
                .optional("amount", ArgumentHandlers.INTEGER)
                .build();
    }
}
