package dev.oribuin.fishing.command.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.command.FishCommand;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.impl.TotemConfig;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.loot.FishLoot;
import dev.oribuin.fishing.model.loot.LootRegistry;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.Fisher;
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

        ItemStack item = LootRegistry.from("augment_" + augment.getName());
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
     * @param sender The sender running the command
     * @param target The target receiving the augment
     * @param amount The amount of augments being given
     */
    @Command("fishing|fish give <target> totem [amount]")
    @Permission("fishing.give")
    @CommandDescription("Gives the player a specified augment")
    public void giveTotem(CommandSender sender, Player target, Integer amount) {
        if (amount == null || amount < 0) amount = 1;

        Totem totem = new Totem(null, null, target.getUniqueId());
        ItemStack itemStack = TotemConfig.get().getTotemItem().create(totem.getPlaceholders()).clone();
        totem.saveTo(itemStack);

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

    /**
     * Give the player a specified amount of entropy
     *
     * @param sender The sender running the command
     * @param target The target the item
     * @param amount The amount of loot being given
     */
    @Command("fishing|fish give <target> entropy <amount>")
    @Permission("fishing.give")
    @CommandDescription("Gives the player a specified amount of entropy")
    public void giveEntropy(CommandSender sender, Player target, Integer amount) {

        Fisher fisher = this.plugin.getDataManager().get(target.getUniqueId());
        fisher.setEntropy(fisher.getEntropy() + amount);
        this.plugin.getDataManager().saveUser(fisher);

        PluginMessages.get().getGivenAmount().send(sender,
                "target", target.getName(),
                "amount", amount,
                "type", "entropy",
                "total", fisher.getEntropy()
        );
    }

    /**
     * Give the player a specified amount of entropy
     *
     * @param sender The sender running the command
     * @param target The target the item
     * @param amount The amount of loot being given
     */
    @Command("fishing|fish give <target> experience <amount>")
    @Permission("fishing.give")
    @CommandDescription("Gives the player a specified amount of experience")
    public void giveExperience(CommandSender sender, Player target, Integer amount) {

        Fisher fisher = this.plugin.getDataManager().get(target.getUniqueId());
        fisher.setExperience(fisher.getExperience() + amount);
        while (fisher.canLevelUp()) fisher.levelUp(); // level up multiple times
        this.plugin.getDataManager().saveUser(fisher);
        
        PluginMessages.get().getGivenAmount().send(sender,
                "target", target.getName(),
                "amount", amount,
                "type", "experience",
                "total", fisher.getExperience()
        );
    }
    
    /**
     * Give the player a specified amount of entropy
     *
     * @param sender The sender running the command
     * @param target The target the item
     * @param amount The amount of loot being given
     */
    @Command("fishing|fish give <target> level <amount>")
    @Permission("fishing.give")
    @CommandDescription("Gives the player additional levels")
    public void giveLevel(CommandSender sender, Player target, Integer amount) {

        Fisher fisher = this.plugin.getDataManager().get(target.getUniqueId());
        fisher.setExperience(0);
        fisher.setLevel(fisher.getLevel() + amount);
        fisher.setSkillPoints(fisher.getSkillPoints() + amount);
        this.plugin.getDataManager().saveUser(fisher);

        PluginMessages.get().getGivenAmount().send(sender,
                "target", target.getName(),
                "amount", amount,
                "type", "level",
                "total", fisher.getLevel()
        );
    }

    /**
     * Give a loot item to a specified player
     *
     * @param sender The sender running the command
     * @param target The target the item
     * @param loot   The fishing loot
     * @param amount The amount of loot being given
     */
    @Command("fishing|fish give <target> loot <loot> [amount]")
    @Permission("fishing.give")
    @CommandDescription("Gives the player a specified augment")
    public void giveLoot(CommandSender sender, Player target, FishLoot loot, Integer amount) {
        if (amount == null || amount < 0) amount = 1;

        ItemStack item = loot.create();
        if (target.getInventory().firstEmpty() == -1) {
            PluginMessages.get().getFullInventory().send(sender); // TODO: Drop items on the ground
            return;
        }

        target.getInventory().addItem(item.asQuantity(amount));
        PluginMessages.get().getGivenItem().send(sender,
                "target", target.getName(),
                "amount", amount,
                "name", item.displayName(),
                "type", "Loot"
        );
    }

}
