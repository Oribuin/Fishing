package dev.oribuin.fishing.api.gui;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.config.Configurable;
import org.bukkit.Material;
import dev.oribuin.fishing.util.ItemConstruct;

public abstract class PluginMenu<T extends BaseGui> implements Configurable {

    public static final ItemConstruct BORDER = ItemConstruct.of(Material.BLACK_STAINED_GLASS_PANE).tooltip(false);

    protected final FishingPlugin plugin;
    protected final T gui;

    /**
     * Construct a new PluginMenu with the given plugin and gui.
     *
     * @param plugin The plugin
     * @param gui    The gui
     */
    protected PluginMenu(FishingPlugin plugin, T gui) {
        this.plugin = plugin;
        this.gui = gui;
    }
}
