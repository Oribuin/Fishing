package xyz.oribuin.fishing;

import xyz.oribuin.fishing.manager.CommandManager;
import xyz.oribuin.fishing.manager.ConfigurationManager;
import xyz.oribuin.fishing.manager.DataManager;
import xyz.oribuin.fishing.manager.LocaleManager;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.jetbrains.annotations.NotNull;

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
    }

    @Override
    public void disable() {

    }

    @Override
    protected @NotNull List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of();
    }

}
