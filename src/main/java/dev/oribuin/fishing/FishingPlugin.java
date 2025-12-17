package dev.oribuin.fishing;

import dev.oribuin.fishing.config.ConfigLoader;
import dev.oribuin.fishing.config.impl.Config;
import dev.oribuin.fishing.config.impl.MySQLConfig;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.gui.MenuRegistry;
import dev.oribuin.fishing.hook.plugin.HeadDbProvider;
import dev.oribuin.fishing.listener.FishListener;
import dev.oribuin.fishing.listener.PlayerListeners;
import dev.oribuin.fishing.listener.TotemListeners;
import dev.oribuin.fishing.manager.CommandManager;
import dev.oribuin.fishing.manager.DataManager;
import dev.oribuin.fishing.manager.FishManager;
import dev.oribuin.fishing.manager.TierManager;
import dev.oribuin.fishing.manager.TotemManager;
import dev.oribuin.fishing.model.augment.AugmentRegistry;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingPlugin extends JavaPlugin {

    private static FishingPlugin instance;
    private ConfigLoader configLoader;
    private CommandManager commandManager;
    private DataManager dataManager;
    private FishManager fishManager;
    private TierManager tierManager;
    private TotemManager totemManager;


    @Override
    public void onEnable() {
        instance = this;

        // Load this plugin configs
        this.configLoader = new ConfigLoader();
        this.configLoader.loadConfig(Config.class, "config");
        this.configLoader.loadConfig(PluginMessages.class, "messages");
        this.configLoader.loadConfig(MySQLConfig.class, "mysql-config");
        
        // Load the plugin managers
        this.commandManager = new CommandManager(this);
        this.dataManager = new DataManager(this);
        this.tierManager = new TierManager(this);
        this.fishManager = new FishManager(this);
        this.totemManager = new TotemManager(this);

        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new FishListener(this), this);
        manager.registerEvents(new PlayerListeners(this), this);
        manager.registerEvents(new TotemListeners(this), this);

        if (HeadDbProvider.isEnabled()) {
            manager.registerEvents(new HeadDbProvider(), this);
        }
    }

    public void reload() {
        this.commandManager.reload(this);
        this.tierManager.reload(this);
        this.fishManager.reload(this);
        this.totemManager.reload(this);

        AugmentRegistry.reload();
        MenuRegistry.reload();
        
        this.dataManager.reload(this);
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

    public FishManager getFishManager() {
        return fishManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }
    
}
