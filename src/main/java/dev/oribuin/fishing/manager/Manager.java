package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.FishingPlugin;

public interface Manager {

    /**
     * The task that runs when the plugin is loaded/reloaded
     *
     * @param plugin The plugin reloading
     */
    void reload(FishingPlugin plugin);

    /**
     * The task that runs when the plugin is disabled, usually takes priority over {@link Manager#reload(FishingPlugin)}
     *
     * @param plugin The plugin being disabled
     */
    void disable(FishingPlugin plugin);

}
