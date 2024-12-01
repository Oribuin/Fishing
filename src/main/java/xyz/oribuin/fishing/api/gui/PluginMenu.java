package xyz.oribuin.fishing.api.gui;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.config.Configurable;

import java.nio.file.Path;
import java.util.function.BiConsumer;

public abstract class PluginMenu<T extends BaseGui> implements Configurable {

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

    /**
     * The path to the configuration file to be loaded. All paths will be relative to the {@link #parentFolder()},
     * If you wish to overwrite this functionality, override the {@link #parentFolder()} method
     *
     * @return The path
     */
    @Override
    public @Nullable Path configPath() {
        return Path.of("menus", this.id() + ".yml");
    }
}
