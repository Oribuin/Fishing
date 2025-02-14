package dev.oribuin.fishing.gui;

import dev.oribuin.fishing.api.gui.PluginMenu;
import dev.oribuin.fishing.gui.codex.impl.AugmentCodexMenu;
import dev.oribuin.fishing.gui.codex.impl.FishCodexMenu;
import dev.oribuin.fishing.gui.totem.TotemMainMenu;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MenuRegistry {

    private static final Map<Class<?> , PluginMenu<?>> menus = new HashMap<>();
    
    static {
        // Codex menus
        register(AugmentCodexMenu::new);
        register(FishCodexMenu::new);
        
        // Totem Menus
        register(TotemMainMenu::new);
    }
    /**
     * Register a new menu to the registry with the specified name
     *
     * @param menu The menu to register
     *
     * @return The registered menu
     */
    public static <T extends PluginMenu<?>> T register(@NotNull Supplier<T> menu) {
        T instance = menu.get();
        if (menus.containsKey(instance.getClass())) menus.get(instance.getClass());

        instance.reload(); // Reload the menu
        menus.put(instance.getClass(), instance);
        return instance;
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
