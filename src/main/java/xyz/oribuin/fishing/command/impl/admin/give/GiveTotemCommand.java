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
import xyz.oribuin.fishing.totem.Totem;

public class GiveTotemCommand extends BaseRoseCommand {

    public GiveTotemCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player target, Integer amount) {
        int fixedAmount = Math.max(1, Math.min(amount == null ? 1 : amount, 64));

        CommandSender sender = context.getSender();
        Totem totem = new Totem(target.getUniqueId(), target.getName());

        if (target.getInventory().firstEmpty() == -1) {
            // TODO: Plugin Message
            sender.sendMessage(target.getName() + "'s inventory is full.");
            return;
        }

        ItemStack itemStack = Totem.DEFAULT_ITEM.build(totem.placeholders());
        totem.saveTo(itemStack);

        target.getInventory().addItem(itemStack);
        sender.sendMessage("You have given " + target.getName() + " " + fixedAmount + " Fishing Totem(s).");
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("totem")
                .descriptionKey("command-give-description")
                .permission("fishing.admin")
                .arguments(this.createArguments())
                .build();
    }

    private ArgumentsDefinition createArguments() {
        return ArgumentsDefinition.builder()
                .required("player", ArgumentHandlers.PLAYER)
                .optional("amount", ArgumentHandlers.INTEGER)
                .build();
    }
}
