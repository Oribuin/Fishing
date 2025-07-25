package dev.oribuin.fishing;

import dev.oribuin.fishing.gui.MenuRegistry;
import dev.oribuin.fishing.model.augment.AugmentRegistry;
import dev.oribuin.fishing.config.Setting;
import dev.oribuin.fishing.listener.FishListener;
import dev.oribuin.fishing.listener.PlayerListeners;
import dev.oribuin.fishing.listener.TotemListeners;
import dev.oribuin.fishing.manager.CommandManager;
import dev.oribuin.fishing.manager.DataManager;
import dev.oribuin.fishing.manager.FishManager;
import dev.oribuin.fishing.manager.LocaleManager;
import dev.oribuin.fishing.manager.TierManager;
import dev.oribuin.fishing.manager.TotemManager;
import dev.oribuin.fishing.model.item.ItemRegistry;
import dev.oribuin.fishing.model.skill.SkillRegistry;
import dev.oribuin.fishing.model.totem.upgrade.UpgradeRegistry;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.SettingHolder;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        AugmentRegistry.reload();
        UpgradeRegistry.reload();
        ItemRegistry.init();
        SkillRegistry.init();
        MenuRegistry.reload();
        
        super.reload();
    }

    @Override
    public void disable() {

    }

    @Override
    protected @NotNull List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                TierManager.class,
                FishManager.class,
                TotemManager.class
        );
    }

    /**
     * @return 
     */
    @Override
    protected @Nullable SettingHolder getRoseConfigSettingHolder() {
        return super.getRoseConfigSettingHolder();
    }
}
