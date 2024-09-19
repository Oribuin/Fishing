package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.fishing.storage.util.FinePosition;
import xyz.oribuin.fishing.totem.Totem;

import java.util.HashMap;
import java.util.Map;

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

}
