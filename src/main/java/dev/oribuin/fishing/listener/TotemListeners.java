package dev.oribuin.fishing.listener;

import dev.oribuin.fishing.item.ItemRegistry;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.gui.totem.TotemMainMenu;
import dev.oribuin.fishing.manager.MenuManager;
import dev.oribuin.fishing.manager.TotemManager;
import dev.oribuin.fishing.totem.Totem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

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

        Totem totem = Totem.fromContainer(meta.getPersistentDataContainer());
        if (totem == null) return;

        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);

        if (event.getBlockFace() != BlockFace.UP) return;

        Block relative = event.getClickedBlock().getRelative(BlockFace.UP);
        if (relative.getType() != Material.AIR) {
            event.getPlayer().sendMessage("There is no space to place the totem.");
            return;
        }

        Location center = relative.getLocation().toCenterLocation();
        totem.spawn(center);

        event.getItem().setAmount(event.getItem().getAmount() - 1);
        this.plugin.getManager(TotemManager.class).registerTotem(totem);
        event.getPlayer().sendMessage("Totem placed.");
    }

    /**
     * Listen to the player interact entity event. This will listen to the player interacting with the armor stand.
     *
     * @param event The event to listen to.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand stand)) return;

        TotemManager manager = this.plugin.getManager(TotemManager.class);
        Totem totem = manager.getTotem(stand);
        if (totem == null) return;

        event.setCancelled(true);

        if (!event.getPlayer().isSneaking()) {
            TotemMainMenu.open(totem, event.getPlayer());
            return;
        }

        // Remove the totem if the player is the owner
        if (!event.getPlayer().getUniqueId().equals(totem.owner())) {
            event.getPlayer().sendMessage("You cannot remove your own totem.");
            return;
        }

        if (event.getPlayer().getInventory().firstEmpty() == -1) {
            event.getPlayer().sendMessage("Your inventory is full.");
            return;
        }

        totem.entity().remove(); // Remove the totem entity
        totem.entity(null); // Set the totem entity to null
        manager.unregisterTotem(totem); // Unregister the totem
        event.getPlayer().sendMessage("Totem removed."); // Send the player a message

        ItemStack itemStack = ItemRegistry.FISHING_TOTEM.build(totem.placeholders()); // TODO: Load the item from the configuration
        totem.saveTo(itemStack);

        event.getPlayer().getInventory().addItem(itemStack);
    }

    /**
     * Load all the totems in the chunk. This will register all the totems in the chunk.
     *
     * @param event The event to listen to.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        TotemManager totemManager = this.plugin.getManager(TotemManager.class);
        CompletableFuture.runAsync(() -> Arrays.stream(event.getChunk().getEntities()).forEach(entity -> {
            if (!(entity instanceof ArmorStand stand)) return;

            Totem totem = Totem.fromEntity(stand);
            if (totem == null) return;

            totemManager.registerTotem(totem);
        }));
    }

    /**
     * Unload all the totems in the chunk. This will unregister all the totems in the chunk.
     *
     * @param event The event to listen to.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent event) {
        TotemManager totemManager = this.plugin.getManager(TotemManager.class);
        new ArrayList<>(totemManager.totems().values()).forEach(totem -> {
            if (!totem.center().getWorld().getName().equalsIgnoreCase(event.getWorld().getName())) return;

            int chunkX = totem.center().getBlockX() >> 4;
            int chunkZ = totem.center().getBlockZ() >> 4;

            if (chunkX != event.getChunk().getX() || chunkZ != event.getChunk().getZ()) return;

            totemManager.unregisterTotem(totem);
        });
    }

}
