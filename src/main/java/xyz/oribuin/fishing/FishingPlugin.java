package xyz.oribuin.fishing;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.augment.AugmentRegistry;
import xyz.oribuin.fishing.config.Setting;
import xyz.oribuin.fishing.listener.FishListener;
import xyz.oribuin.fishing.listener.PlayerListeners;
import xyz.oribuin.fishing.listener.TotemListeners;
import xyz.oribuin.fishing.manager.FishManager;
import xyz.oribuin.fishing.manager.MenuManager;
import xyz.oribuin.fishing.manager.TierManager;
import xyz.oribuin.fishing.manager.TotemManager;
import xyz.oribuin.fishing.manager.base.CommandManager;
import xyz.oribuin.fishing.manager.base.DataManager;
import xyz.oribuin.fishing.manager.base.LocaleManager;
import xyz.oribuin.fishing.skill.SkillRegistry;

import java.util.List;

public class FishingPlugin extends RosePlugin {

    private static FishingPlugin instance;

    public static FishingPlugin get() {
        return instance;
    }

    public FishingPlugin() {
        super(-1, -1,
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
        manager.registerEvents(new TotemListeners(this), this);
    }

    @Override
    public void reload() {
        super.reload();

        SkillRegistry.init();
        AugmentRegistry.init();
    }

    @Override
    public void disable() {

    }

    @Override
    protected @NotNull List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                TierManager.class,
                FishManager.class,
                MenuManager.class,
                TotemManager.class
        );
    }

    @Override
    protected @NotNull List<RoseSetting<?>> getRoseConfigSettings() {
        return Setting.getKeys();
    }

}
