package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.ConfigLoader;
import dev.oribuin.fishing.gui.GuiConfig;
import dev.oribuin.fishing.gui.impl.codex.impl.AugmentCodexMenu;
import dev.oribuin.fishing.gui.impl.codex.impl.FishCodexMenu;
import dev.oribuin.fishing.gui.impl.codex.impl.TierCodexMenu;
import dev.oribuin.fishing.gui.impl.totem.TotemMainMenu;
import dev.oribuin.fishing.gui.impl.totem.TotemUpgradeMenu;
import dev.oribuin.fishing.gui.impl.user.FishAugmentMenu;
import dev.oribuin.fishing.gui.impl.user.FishGutMenu;
import dev.oribuin.fishing.gui.impl.user.FishMainMenu;
import dev.oribuin.fishing.gui.impl.user.FishSellMenu;
import dev.oribuin.fishing.model.augment.Augment;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.io.File;

public class MenuManager implements Manager {

    private static final File MENUS_FOLDER = new File(FishingPlugin.get().getDataFolder(), "menus");
    private static final ConfigLoader loader = new ConfigLoader(MENUS_FOLDER.toPath());
    private final FishingPlugin plugin;

    public MenuManager(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * The task that runs when the plugin is loaded/reloaded
     *
     * @param plugin The plugin reloading
     */
    @Override
    public void reload(FishingPlugin plugin) {
        // Codex menus
        register("codex/augments", AugmentCodexMenu.Config.class);
        register("codex/fish", FishCodexMenu.Config.class);
        register("codex/tiers", TierCodexMenu.Config.class);

        // Totem Menus
        register("totem/main_menu", TotemMainMenu.Config.class);
        register("totem/upgrades", TotemUpgradeMenu.Config.class);

        // User Menus
        register("main_menu", FishMainMenu.Config.class);
        register("gutting_menu", FishGutMenu.Config.class);
        register("selling_menu", FishSellMenu.Config.class);
        register("augment_menu", FishAugmentMenu.Config.class);

        this.plugin.getLogger().info("Loaded a total of [" + loader.getConfigs().size() + "] menus into the plugin");
    }

    /**
     * The task that runs when the plugin is disabled, usually takes priority over {@link Manager#reload(FishingPlugin)}
     *
     * @param plugin The plugin being disabled
     */
    @Override
    public void disable(FishingPlugin plugin) {
        loader.close();
    }

    /**
     * Loads an augment into the registry to be used in the plugin and caches it.
     *
     * @param supplier The {@link Augment} to register
     */
    public static <T extends GuiConfig> void register(String identifier, Class<T> configClass) {

        if (!configClass.isAnnotationPresent(ConfigSerializable.class)) {
            FishingPlugin.get().getLogger().warning("Menu[" + identifier + "] in class[" + configClass.getSimpleName() + "] does not have ConfigSerializible annotation");
            return;
        }

        loader.loadConfig(configClass, identifier);
    }

    public static ConfigLoader getLoader() {
        return loader;
    }

}
