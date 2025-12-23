package dev.oribuin.fishing.hook.plugin;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.storage.Fisher;
import jdk.jfr.Event;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIProvider extends PlaceholderExpansion {

    private final FishingPlugin plugin;
    private static Boolean enabled;

    public PAPIProvider(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "fishing";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Oribuin";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    /**
     * Request placeholder results from the plugin
     *
     * @param player The player who's requested
     * @param params The fishing parameters
     *
     * @return The returning placeholders
     */
    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        Fisher fisher = this.plugin.getDataManager().get(player.getUniqueId());
        if (fisher == null) return "N/A";
        
        return switch (params.toLowerCase()) {
            case "entropy" -> String.valueOf(fisher.entropy());
            case "level" -> String.valueOf(fisher.level());
            case "exp" -> String.valueOf(fisher.experience());
            case "required_exp" -> String.valueOf(fisher.requiredExp());
            case "progress" -> String.valueOf(fisher.experience() / fisher.requiredExp());
            default -> null;
        };
    }

    /**
     * Returns true when PlaceholderAPI is enabled on the server, otherwise false
     */
    public static boolean isEnabled() {
        if (enabled == null) enabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        return enabled;
    }

    /**
     * Apply the PlaceholderAPI to the given text. If the player is null, it will return the text as is.
     *
     * @param player The player to apply the PlaceholderAPI to.
     * @param text   The text to apply the PlaceholderAPI to.
     *
     * @return The text with the PlaceholderAPI applied.
     */
    public static String apply(@Nullable OfflinePlayer player, String text) {
        if (!isEnabled()) return text;

        return PlaceholderAPI.setPlaceholders(player, text);
    }

    /**
     * Apply the PlaceholderAPI to the given text. If the player is null, it will return the text as is.
     *
     * @param first  The first player to apply the PlaceholderAPI to.
     * @param second The second player to apply the PlaceholderAPI to.
     * @param text   The text to apply the PlaceholderAPI to.
     *
     * @return The text with the PlaceholderAPI applied.
     */
    public static String applyRelational(@Nullable Player first, Player second, String text) {
        if (!isEnabled()) return text;

        return PlaceholderAPI.setRelationalPlaceholders(first, second, text);
    }

}
