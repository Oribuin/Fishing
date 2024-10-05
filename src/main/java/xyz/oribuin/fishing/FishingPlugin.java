package xyz.oribuin.fishing;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.augment.AugmentRegistry;
import xyz.oribuin.fishing.listener.FishListener;
import xyz.oribuin.fishing.listener.PlayerListeners;
import xyz.oribuin.fishing.manager.CommandManager;
import xyz.oribuin.fishing.manager.ConfigurationManager;
import xyz.oribuin.fishing.manager.DataManager;
import xyz.oribuin.fishing.manager.FishManager;
import xyz.oribuin.fishing.manager.LocaleManager;
import xyz.oribuin.fishing.manager.TierManager;
import xyz.oribuin.fishing.skill.SkillRegistry;

import java.util.List;

public class FishingPlugin extends RosePlugin {

    private static FishingPlugin instance;

    public static FishingPlugin get() {
        return instance;
    }

    public FishingPlugin() {
        super(-1, -1,
                ConfigurationManager.class,
                DataManager.class,
                LocaleManager.class,
                CommandManager.class
        );

        instance = this;
    }

    @Override
    public void enable() {
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new FishListener(this), this);
        manager.registerEvents(new PlayerListeners(this), this);

        SkillRegistry.init();
        AugmentRegistry.init();
    }

    @Override
    public void disable() {

    }

    @Override
    protected @NotNull List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(TierManager.class, FishManager.class);
    }

}
