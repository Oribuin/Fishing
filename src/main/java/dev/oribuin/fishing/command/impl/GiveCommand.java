package dev.oribuin.fishing.command.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.command.FishCommand;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.item.ItemRegistry;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.totem.Totem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class GiveCommand implements FishCommand {

    private final FishingPlugin plugin;

    /**
     * Create a new command instance with the provided plugin instance.
     *
     * @param plugin The plugin instance.
     */
    public GiveCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Give a fish to a specified player
     *
     * @param sender The sender running the command
     * @param target The target receiving the fish
     * @param fish   The fish being given
     * @param amount The amount of fish being given
     */
    @Command("fishing|fish give <target> fish <fish> [amount]")
    @Permission("fishing.give")
    @CommandDescription("Gives the player a specified fish")
    public void giveFish(CommandSender sender, Player target, Fish fish, Integer amount) {
        if (amount == null || amount < 0) amount = 1;

        ItemStack item = fish.buildItem();
        if (item == null) {
            sender.sendMessage("An error occurred while creating the fish item."); // TODO: Plugin Message
            return;
        }

        if (target.getInventory().firstEmpty() == -1) {
            PluginMessages.get().getFullInventory().send(sender); // TODO: Drop items on the ground
            return;
        }

        target.getInventory().addItem(item.asQuantity(amount));
        PluginMessages.get().getGivenItem().send(sender,
                "target", target.getName(),
                "amount", amount,
                "name", item.displayName(),
                "type", "Fish"
        );
    }

    /**
     * Give an augment to a specified player
     *
     * @param sender  The sender running the command
     * @param target  The target receiving the augment
     * @param augment The augment being given
     * @param amount  The amount of augments being given
     */
    @Command("fishing|fish give <target> augment <augment> [amount]")
    @Permission("fishing.give")
    @CommandDescription("Gives the player a specified augment")
    public void giveAugment(CommandSender sender, Player target, Augment augment, Integer amount) {
        if (amount == null || amount < 0) amount = 1;

        ItemStack item = augment.getDisplayItem().build(augment.getPlaceholders());
        if (item == null) {
            sender.sendMessage("An error occurred while creating the fish item."); // TODO: Plugin Message
            return;
        }

        if (target.getInventory().firstEmpty() == -1) {
            PluginMessages.get().getFullInventory().send(sender); // TODO: Drop items on the ground
            return;
        }

        target.getInventory().addItem(item.asQuantity(amount));
        PluginMessages.get().getGivenItem().send(sender,
                "target", target.getName(),
                "amount", amount,
                "name", item.displayName(),
                "type", "Augment"
        );
    }

    /**
     * Give a totem to a specified player
     *
     * @param sender  The sender running the command
     * @param target  The target receiving the augment
     * @param amount  The amount of augments being given
     */
    @Command("fishing|fish give <target> totem [amount]")
    @Permission("fishing.give")
    @CommandDescription("Gives the player a specified augment")
    public void giveTotem(CommandSender sender, Player target, Integer amount) {
        if (amount == null || amount < 0) amount = 1;

        Totem totem = new Totem(null, target);
        ItemStack itemStack = ItemRegistry.FISHING_TOTEM.build(totem.placeholders()); // todo: make not horrific, move to TotemManager
        totem.saveTo(itemStack);
        
        if (itemStack == null) {
            sender.sendMessage("An error occurred while creating the fish item."); // TODO: Plugin Message
            return;
        }

        if (target.getInventory().firstEmpty() == -1) {
            PluginMessages.get().getFullInventory().send(sender); // TODO: Drop items on the ground
            return;
        }

        target.getInventory().addItem(itemStack.asQuantity(amount));
        PluginMessages.get().getGivenItem().send(sender,
                "target", target.getName(),
                "amount", amount,
                "name", itemStack.displayName(),
                "type", "Totem"
        );
    }

}
