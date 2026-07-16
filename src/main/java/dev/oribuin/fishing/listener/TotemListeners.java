package dev.oribuin.fishing.listener;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.impl.TotemConfig;
import dev.oribuin.fishing.gui.impl.totem.TotemMainMenu;
import dev.oribuin.fishing.manager.TotemManager;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class TotemListeners implements Listener {

    private final FishingPlugin plugin;

    public TotemListeners(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Listen to the player interact event. This will listen to the player placing the totem.
     *
     * @param event The event to listen to.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getItem() == null) return;
        if (!event.getAction().isRightClick()) return;

        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.getPersistentDataContainer().has(KeyRegistry.TOTEM_ACTIVE.key(), KeyRegistry.TOTEM_ACTIVE)) return;

        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);

        if (event.getBlockFace() != BlockFace.UP) return;

        Block relative = event.getClickedBlock().getRelative(BlockFace.UP);
        if (relative.getType() != Material.AIR) {
            PluginMessages.get().getTotem().getNoSpace().send(event.getPlayer());
            return;
        }

        Location center = relative.getLocation().toCenterLocation();
        Totem totem = new Totem(center, meta.getPersistentDataContainer());
        totem.spawn(center);

        event.getItem().setAmount(event.getItem().getAmount() - 1);
        this.plugin.getTotemManager().registerTotem(totem);
        PluginMessages.get().getTotem().getPlaced().send(event.getPlayer());
    }

    /**
     * Listen to the player interact entity event. This will listen to the player interacting with the armor stand.
     *
     * @param event The event to listen to.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand stand)) return;

        TotemManager manager = this.plugin.getTotemManager();
        Totem totem = manager.getAndLoadTotem(stand);
        Player player = event.getPlayer();
        if (totem == null) return;

        event.setCancelled(true);

        if (!player.isSneaking()) {
            new TotemMainMenu(this.plugin, () -> manager.getTotem(stand.getUniqueId())).open(player);
            return;
        }

        // Remove the totem if the player is the owner
        UUID owner = totem.getOwner();
        if (owner != null && !player.getUniqueId().equals(owner)) { // TODO: Permission Bypass
            PluginMessages.get().getTotem().getCannotAccess().send(player);
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            PluginMessages.get().getFullInventory().send(player);
            return;
        }

        ItemStack itemStack = TotemConfig.get().getTotemItem().create(totem.getPlaceholders());

        manager.unregisterTotem(totem); // Unregister the totem
        totem.getDisplay().remove(); // Remove the totem entity
        totem.setDisplayId(null); // Set the totem entity to null
        PluginMessages.get().getTotem().getRemoved().send(player);

        totem.saveTo(itemStack);
        player.getInventory().addItem(itemStack);
    }

    //    /**
    //     * Unload all the totems in the chunk. This will unregister all the totems in the chunk.
    //     *
    //     * @param event The event to listen to.
    //     */
    //    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    //    public void onChunkUnload(ChunkUnloadEvent event) {
    //        TotemManager totemManager = this.plugin.getTotemManager();
    //        new ArrayList<>(totemManager.getTotems().values()).forEach(totem -> {
    //            if (!totem.getPosition().getWorld().getName().equalsIgnoreCase(event.getWorld().getName())) return;
    //
    //            int chunkX = totem.getPosition().getBlockX() >> 4;
    //            int chunkZ = totem.getPosition().getBlockZ() >> 4;
    //
    //            if (chunkX != event.getChunk().getX() || chunkZ != event.getChunk().getZ()) return;
    //
    //            totemManager.unregisterTotem(totem);
    //        });
    //    }

}
