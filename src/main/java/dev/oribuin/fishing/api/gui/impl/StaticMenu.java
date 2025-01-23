package dev.oribuin.fishing.api.gui.impl;

import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.api.gui.BaseGui;

public class StaticMenu extends BaseGui {

    /**
     * Create a new gui instance with the specified plugin and id
     *
     * @param plugin The plugin instance
     * @param id     The id of the gui
     */
    public StaticMenu(RosePlugin plugin, String id) {
        super(plugin, id);
    }

}
