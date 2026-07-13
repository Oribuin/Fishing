package dev.oribuin.fishing.command.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.command.FishCommand;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

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
     * Open the main fishing menu for the player
     *
     * @param sender The sender running the command
     */
    @Command("fishing|fish debug placeholders totem")
    @Permission("fishing.debug")
    @CommandDescription("Get the placeholders for a totem in your hand")
    public void executeMainMenu(Player player) {
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
        totem.getPlaceholders().getPlaceholders().forEach((s, component) -> {
            player.sendMessage(Component.text("<" + s + "> = ").append(component));
        });
    }

}
