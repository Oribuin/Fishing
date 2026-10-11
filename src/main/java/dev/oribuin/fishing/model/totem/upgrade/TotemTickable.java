package dev.oribuin.fishing.model.totem.upgrade;

import dev.oribuin.fishing.model.totem.Totem;
import org.jetbrains.annotations.NotNull;

/**
 * Create a new tickable task that will run while the totem is active
 */
public interface TotemTickable {

    /**
     * The functionality to run async while the totem is active
     *
     * @param totem The totem that is active
     */
    void tick(@NotNull Totem totem);

    /**
     * Whether the task should be run asynchronously, If the server is running folia this is ignored
     *
     * @return Whether the task needs to be run synchronously
     */
    default boolean isAsync() {
        return true;
    }

}
