package dev.oribuin.fishing.model.totem.upgrade;

import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.config.Configurable;
import dev.oribuin.fishing.model.economy.Cost;
import dev.oribuin.fishing.model.item.ItemConstruct;
import dev.oribuin.fishing.model.totem.Totem;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public abstract class TotemUpgrade extends FishEventHandler implements Configurable {
    
    protected final String name;
    private boolean enabled;
    private ItemConstruct icon;
    private int maxLevel;
    
    public TotemUpgrade(String name) {
        this.name = name;
        this.enabled = true;
        this.icon = ItemConstruct.of(Material.STONE); // TODO: Change this to a custom item
        this.maxLevel = 1;
    }
    
    /**
     * The path to the configuration file to be loaded. All paths will be relative to the {@link #parentFolder()},
     * If you wish to overwrite this functionality, override the {@link #parentFolder()} method
     *
     * @return The path
     */
    @Override
    public @NotNull Path configPath() {
        return Path.of("totem", "upgrade", this.name.toLowerCase() + ".yml");
    }

}
