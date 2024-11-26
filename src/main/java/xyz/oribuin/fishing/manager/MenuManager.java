package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.gui.PluginMenu;
import xyz.oribuin.fishing.gui.ExampleMenu;
import xyz.oribuin.fishing.gui.StatsMenu;
import xyz.oribuin.fishing.gui.totem.TotemMainMenu;

import java.util.HashMap;
import java.util.Map;

public class MenuManager extends Manager {

    private static final Map<Class<?>, PluginMenu> menus = new HashMap<>();

    public MenuManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        menus.clear();

        // Register the menus
        menus.put(TotemMainMenu.class, new TotemMainMenu(FishingPlugin.get()));

        // Load all the menus
        menus.values().forEach(PluginMenu::reload);
    }


    /**
     * Get a registered menu
     *
     * @param clazz The class of the menu
     * @param <T>   The type of the menu
     *
     * @return The menu
     */
    @SuppressWarnings("unchecked")
    public static <T extends PluginMenu> T from(Class<T> clazz) {
        try {
            return (T) menus.get(clazz);
        } catch (Exception e) {
            FishingPlugin.get().getLogger().severe("Failed to get menu from class: " + clazz.getSimpleName());
            return null;
        }
    }

    @Override
    public void disable() {
        menus.clear();
    }

}
