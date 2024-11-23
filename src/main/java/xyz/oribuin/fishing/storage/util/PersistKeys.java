package xyz.oribuin.fishing.storage.util;

import org.bukkit.NamespacedKey;
import xyz.oribuin.fishing.FishingPlugin;

public class PersistKeys {

    // Plugin Instance
    private static final FishingPlugin PLUGIN = FishingPlugin.get();

    // Fish Namespaces
    public static NamespacedKey FISH_TYPE = new NamespacedKey(PLUGIN, "fish_type");
    public static NamespacedKey FISH_TIER = new NamespacedKey(PLUGIN, "fish_tier");

    // Totem Namespaces
    public static final NamespacedKey TOTEM_OWNER = new NamespacedKey(PLUGIN, "totem_owner");
    public static final NamespacedKey TOTEM_OWNERNAME = new NamespacedKey(PLUGIN, "totem_ownername");
    public static final NamespacedKey TOTEM_RADIUS = new NamespacedKey(PLUGIN, "totem_radius");
    public static final NamespacedKey TOTEM_ACTIVE = new NamespacedKey(PLUGIN, "totem_active");
    public static final NamespacedKey TOTEM_DURATION = new NamespacedKey(PLUGIN, "totem_duration");
    public static final NamespacedKey TOTEM_COOLDOWN = new NamespacedKey(PLUGIN, "totem_cooldown");
    public static final NamespacedKey TOTEM_LASTACTIVE = new NamespacedKey(PLUGIN, "totem_lastactive");

}

