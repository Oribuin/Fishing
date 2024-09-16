package xyz.oribuin.fishing.totem.upgrade;

import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.Configurable;
import xyz.oribuin.fishing.api.FishEventHandler;
import xyz.oribuin.fishing.totem.Totem;

import java.nio.file.Path;
import java.util.Locale;

public abstract class TotemUpgrade extends FishEventHandler implements Configurable {

    /**
     * The name of the totem upgrade to be displayed to the player
     *
     * @return The name of the upgrade
     */
    public abstract String name();

    /**
     * Apply the upgrade to the totem object
     *
     * @param totem The totem object to apply the upgrade to
     * @param level The level of the upgrade
     */
    public void applyTo(Totem totem, int level) {
    }

    /**
     * The path to the configuration file to be loaded. All paths will be relative to the {@link #parentFolder()},
     * If you wish to overwrite this functionality, override the {@link #parentFolder()} method
     *
     * @return The path
     */
    @Override
    public @NotNull Path configPath() {
        return Path.of("totem", "upgrade", this.name().toLowerCase() + ".yml");
    }

}
