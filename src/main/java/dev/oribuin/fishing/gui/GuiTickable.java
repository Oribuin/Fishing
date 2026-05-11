package dev.oribuin.fishing.gui;

import java.time.Duration;

public interface GuiTickable {

    /**
     * Creates a tickable task for a {@link PluginMenu}
     */
    void tick();

    /**
     * Get the duration delay between each gui refresh
     *
     * @return The gui to refresh
     */
    default Duration getTickDelay() {
        return Duration.ofSeconds(1);
    }

}
