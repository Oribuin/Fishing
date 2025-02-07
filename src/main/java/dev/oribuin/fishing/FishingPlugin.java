package dev.oribuin.fishing;

import dev.oribuin.fishing.model.item.ItemRegistry;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.augment.AugmentRegistry;
import dev.oribuin.fishing.config.Setting;
import dev.oribuin.fishing.listener.FishListener;
import dev.oribuin.fishing.listener.PlayerListeners;
import dev.oribuin.fishing.listener.TotemListeners;
import dev.oribuin.fishing.manager.FishManager;
import dev.oribuin.fishing.manager.MenuManager;
import dev.oribuin.fishing.manager.TierManager;
import dev.oribuin.fishing.manager.TotemManager;
import dev.oribuin.fishing.manager.CommandManager;
import dev.oribuin.fishing.manager.DataManager;
import dev.oribuin.fishing.manager.LocaleManager;
import dev.oribuin.fishing.model.skill.SkillRegistry;

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
        ItemRegistry.init();
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
