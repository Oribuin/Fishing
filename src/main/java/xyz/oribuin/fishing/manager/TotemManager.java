package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.fishing.storage.FinePosition;
import xyz.oribuin.fishing.totem.Totem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TotemManager extends Manager {

    private final Map<FinePosition, Totem> totems = new HashMap<>();

    public TotemManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
    }

    @Override
    public void disable() {

    }

    public void loadTotem()
}
