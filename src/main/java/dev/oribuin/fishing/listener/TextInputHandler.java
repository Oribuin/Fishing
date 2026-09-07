package dev.oribuin.fishing.listener;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.util.FishUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class TextInputHandler implements Listener {

    private static final Map<UUID, TextInput> AWAITING_INPUT = new HashMap<>();

    private final FishingPlugin plugin;

    public TextInputHandler(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Listen for chat events for totem being read
     *
     * @param event The chat event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        TextInput input = AWAITING_INPUT.get(event.getPlayer().getUniqueId());
        if (input == null) return;

        // Cancel the event
        event.setCancelled(true);
        AWAITING_INPUT.remove(event.getPlayer().getUniqueId());

        String stripped = FishUtils.PLAIN.serialize(event.message());

        // Check if the message is "cancel"
        if (stripped.equalsIgnoreCase("cancel")) {
            input.failure().accept(event.getPlayer(), stripped);
            return;
        }

        input.success().accept(event.getPlayer(), stripped);
    }

    /**
     * Write an input await for the plugin
     *
     * @param target    The target to write the input
     * @param onSuccess The function to run when the check succeeded
     * @param onFail    The function to run when the check either cancelled or failed
     */
    public static void writeInput(
            @NotNull Player target,
            BiConsumer<@NotNull Player, @Nullable String> onSuccess,
            BiConsumer<@NotNull Player, @Nullable String> onFail
    ) {
        TextInput input = new TextInput(onSuccess, onFail);
        AWAITING_INPUT.put(target.getUniqueId(), input);

        PluginScheduler.get().runTaskAtEntityLater(target, () -> {
            TextInput delayed = AWAITING_INPUT.remove(target.getUniqueId());
            if (delayed == null) return;

            Player newTarget = Bukkit.getPlayer(target.getUniqueId());
            if (newTarget != null && newTarget.isOnline()) delayed.failure().accept(newTarget, null);
        }, 5, TimeUnit.MINUTES);

    }

    /**
     * Check whether the target is already waiting for an input
     *
     * @param target The target
     *
     * @return The functionality to provide
     */
    public static boolean isAwaiting(Player target) {
        return AWAITING_INPUT.containsKey(target.getUniqueId());
    }

    /**
     * Create a collection of consumers for the player when the plugin is waiting for their text input
     *
     * @param success The success functionality
     * @param failure The failure functionality
     */
    public record TextInput(BiConsumer<Player, String> success, BiConsumer<Player, String> failure) {
        // Empty
    }


}
