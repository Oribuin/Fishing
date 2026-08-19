package dev.oribuin.fishing;

import dev.oribuin.fishing.config.ConfigLoader;
import dev.oribuin.fishing.config.impl.Config;
import dev.oribuin.fishing.config.impl.LootConfig;
import dev.oribuin.fishing.config.impl.MySQLConfig;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.impl.TotemConfig;
import dev.oribuin.fishing.hook.plugin.HeadDbProvider;
import dev.oribuin.fishing.hook.plugin.PAPIProvider;
import dev.oribuin.fishing.listener.FishListener;
import dev.oribuin.fishing.listener.PlayerListeners;
import dev.oribuin.fishing.listener.TotemListeners;
import dev.oribuin.fishing.manager.AugmentManager;
import dev.oribuin.fishing.manager.CommandManager;
import dev.oribuin.fishing.manager.DataManager;
import dev.oribuin.fishing.manager.MenuManager;
import dev.oribuin.fishing.manager.RodManager;
import dev.oribuin.fishing.manager.TierManager;
import dev.oribuin.fishing.manager.TotemManager;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgradeRegistry;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingPlugin extends JavaPlugin {

    private static FishingPlugin instance;
    private ConfigLoader configLoader;
    private AugmentManager augmentManager;
    private CommandManager commandManager;
    private DataManager dataManager;
    private MenuManager menuManager;
    private TierManager tierManager;
    private TotemManager totemManager;
    private RodManager rodManager;
    
    @Override
    public void onEnable() {
        instance = this;

        // Load this plugin configs
        this.configLoader = new ConfigLoader();
        this.configLoader.loadConfig(Config.class, "settings");
        this.configLoader.loadConfig(PluginMessages.class, "messages");
        this.configLoader.loadConfig(MySQLConfig.class, "database");
        this.configLoader.loadConfig(LootConfig.class, "loot-settings");
        this.configLoader.loadConfig(TotemConfig.class, "totem-settings");

        // Load the plugin managers
        this.commandManager = new CommandManager(this);
        this.dataManager = new DataManager(this);
        this.tierManager = new TierManager(this);
        this.augmentManager = new AugmentManager(this);
        this.totemManager = new TotemManager(this);
        this.rodManager = new RodManager(this);
        this.menuManager = new MenuManager(this);
        this.reload();

        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new FishListener(this), this);
        manager.registerEvents(new PlayerListeners(this), this);
        manager.registerEvents(new TotemListeners(this), this);

        // register plugin hooks
        if (HeadDbProvider.isEnabled()) manager.registerEvents(new HeadDbProvider(), this);
        if (PAPIProvider.isEnabled()) new PAPIProvider(this).register();
    }

    public void reload() {
        this.configLoader.reload();
        this.dataManager.reload(this);
        this.commandManager.reload(this);
        this.tierManager.reload(this);
        this.augmentManager.reload(this);
        this.totemManager.reload(this);
        this.rodManager.reload(this);
        this.menuManager.reload(this);

        TotemUpgradeRegistry.register();
    }

    public static FishingPlugin get() {
        return instance;
    }

    public TotemManager getTotemManager() {
        return totemManager;
    }

    public TierManager getTierManager() {
        return tierManager;
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public AugmentManager getAugmentManager() {
        return augmentManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public RodManager getRodManager() {
        return rodManager;
    }
}
