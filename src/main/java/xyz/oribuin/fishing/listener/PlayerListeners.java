package xyz.oribuin.fishing.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.manager.base.DataManager;
import xyz.oribuin.fishing.storage.Fisher;

import java.util.concurrent.CompletableFuture;

public class PlayerListeners implements Listener {

    private final FishingPlugin plugin;

    public PlayerListeners(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Load the player's data when they join the server
     *
     * @param event The join event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        DataManager manager = this.plugin.getManager(DataManager.class);

        CompletableFuture.runAsync(() -> manager.loadUser(event.getPlayer().getUniqueId()))
                .thenRun(() -> {
                    // create a new Fisher object if the player is not found
                    Fisher fisher = manager.get(event.getPlayer().getUniqueId());
                    if (fisher == null) fisher = new Fisher(event.getPlayer().getUniqueId());

                    // Save the new user data
                    manager.saveUser(fisher);
                });
    }

    /**
     * Save a user's data when they leave the server.
     *
     * @param event The quit event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        DataManager manager = this.plugin.getManager(DataManager.class);
        Fisher fisher = manager.get(event.getPlayer().getUniqueId());
        if (fisher == null) return;

        manager.saveUser(fisher); // Clear the user's data.
        manager.all().remove(event.getPlayer().getUniqueId()); // Remove the player from the cache
    }

}
