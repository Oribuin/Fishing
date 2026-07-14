package dev.oribuin.fishing.command.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.command.FishCommand;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.Map;

public class DebugCommand implements FishCommand {

    private final FishingPlugin plugin;

    /**
     * Create a new command instance with the provided plugin instance.
     *
     * @param plugin The plugin instance.
     */
    public DebugCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Debug all the placeholders a totem has 
     *
     * @param player The sender running the command
     */
    @Command("fishing|fish debug placeholders totem")
    @Permission("fishing.debug")
    @CommandDescription("Get the placeholders for a totem in your hand")
    public void debugTotem(Player player) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || stack.getType() == Material.AIR) {
            player.sendMessage("no item in hand :/");
            return;
        }


        if (!meta.getPersistentDataContainer().has(KeyRegistry.TOTEM_ACTIVE.key(), KeyRegistry.TOTEM_ACTIVE)) {
            player.sendMessage("no totem :(");
            return;
        }

        player.sendMessage("Totem Placeholders: ");
        Totem totem = new Totem(player.getLocation(), meta.getPersistentDataContainer());
        totem.getPlaceholders().getAll()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> player.sendMessage(
                        Component.text("<" + entry.getKey() + "> = ").append(entry.getValue()))
                );
    } 
    
    /**
     * Debug all the placeholders a totem has 
     *
     * @param player The sender running the command
     */
    @Command("fishing|fish debug placeholders augment")
    @Permission("fishing.debug")
    @CommandDescription("Get the placeholders for an augment in your hand")
    public void debugAugment(Player player) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || stack.getType() == Material.AIR) {
            player.sendMessage("no item in hand :/");
            return;
        }

        // TODO: Get augment from itemstack
    }

}
