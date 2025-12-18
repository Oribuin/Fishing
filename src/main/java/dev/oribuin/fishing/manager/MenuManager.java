package dev.oribuin.fishing.manager;

import com.google.common.base.Supplier;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.ConfigLoader;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.gui.impl.totem.TotemMainMenu;
import dev.oribuin.fishing.model.augment.Augment;

import javax.swing.plaf.MenuBarUI;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuManager implements Manager {

    public static final File MENUS_FOLDER = new File(FishingPlugin.get().getDataFolder(), "menus");
    private static final Map<Class<?>, PluginMenu<?>> menus = new HashMap<>();
    private static final ConfigLoader loader = new ConfigLoader(MENUS_FOLDER.toPath());
    private final FishingPlugin plugin;

    public MenuManager(FishingPlugin plugin) {
        this.plugin = plugin;
        this.reload(this.plugin);
    }

    /**
     * The task that runs when the plugin is loaded/reloaded
     *
     * @param plugin The plugin reloading
     */
    @Override
    public void reload(FishingPlugin plugin) {
        // Codex menus
        //        register(AugmentCodexMenu::new);
        //        register(FishCodexMenu::new);

        // Totem Menus
        register(TotemMainMenu::new);
        //        register(TotemUpgradeMenu::new);

        this.plugin.getLogger().info("Loaded a total of [" + menus.size() + "] menus into the plugin");
    }

    /**
     * The task that runs when the plugin is disabled, usually takes priority over {@link Manager#reload(FishingPlugin)}
     *
     * @param plugin The plugin being disabled
     */
    @Override
    public void disable(FishingPlugin plugin) {
        loader.close();
        menus.clear();
    }

    /**
     * Loads an augment into the registry to be used in the plugin and caches it.
     *
     * @param supplier The {@link Augment} to register
     */
    @SuppressWarnings("unchecked")
    public static <T extends PluginMenu<?>> void register(Supplier<T> supplier) {
        T menu = supplier.get();
        menu = (T) loader.loadConfig(menu.getClass(), menu.name());
        menus.put(menu.getClass(), menu);
    }

    /**
     * Get a registered menu by the specified class
     *
     * @param menu The menu class
     *
     * @return The menu
     */
    @SuppressWarnings("unchecked")
    public static <T extends PluginMenu<?>> T get(Class<T> menu) {
        return (T) menus.get(menu);
    }

}
