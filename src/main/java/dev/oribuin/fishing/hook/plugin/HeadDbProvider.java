package dev.oribuin.fishing.hook.plugin;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HeadDbProvider implements Listener {

    private static Boolean enabled;
    private static HeadDatabaseAPI api;

    /**
     * Returns true when HeadDatabase is enabled on the server, otherwise false
     */
    public static boolean isEnabled() {
        if (enabled == null) enabled = Bukkit.getPluginManager().isPluginEnabled("HeadDatabase");
        return enabled;
    }

    @EventHandler
    private void onLoad(DatabaseLoadEvent event) {
        api = new HeadDatabaseAPI();
    }

    public static HeadDatabaseAPI getApi() {
        return api;
    }
}
