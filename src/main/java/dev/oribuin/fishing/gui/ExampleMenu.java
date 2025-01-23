package dev.oribuin.fishing.gui;

import org.bukkit.entity.Player;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.gui.MenuIcon;
import dev.oribuin.fishing.api.gui.PluginMenu;
import dev.oribuin.fishing.api.gui.impl.StaticMenu;
import dev.oribuin.fishing.totem.Totem;

public class ExampleMenu extends PluginMenu<StaticMenu> {

    private static final String ID = "example-menu";

    /**
     * Construct a new PluginMenu with the given plugin and gui.
     *
     * @param plugin The plugin
     * @param gui    The gui
     */
    protected ExampleMenu(FishingPlugin plugin, StaticMenu gui) {
        super(plugin, gui);
    }

    /**
     * Open the GUI for the specified player and totem
     *
     * @param player the player to open the GUI for
     * @param totem  the totem to open the GUI for
     */
    public void open(Player player, Totem totem) {
        this.gui.placeStatic(MenuIcon.construct("static-icon"));
    }

}
