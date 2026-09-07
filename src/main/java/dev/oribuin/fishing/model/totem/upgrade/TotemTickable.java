package dev.oribuin.fishing.model.totem.upgrade;

import dev.oribuin.fishing.model.totem.Totem;
import org.jetbrains.annotations.NotNull;

/**
 * Create a new tickable task that will run while the totem is active
 */
public interface TotemTickable {

    /**
     * The functionality to run while the totem is active
     *
     * @param totem The totem that is active
     */
    void tick(@NotNull Totem totem);
}
