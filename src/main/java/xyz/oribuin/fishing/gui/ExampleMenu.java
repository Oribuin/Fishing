package xyz.oribuin.fishing.gui;

import org.bukkit.entity.Player;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.gui.BaseGui;
import xyz.oribuin.fishing.api.gui.MenuIcon;
import xyz.oribuin.fishing.api.gui.PluginMenu;
import xyz.oribuin.fishing.api.gui.impl.StaticMenu;
import xyz.oribuin.fishing.totem.Totem;

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

        this.loadSettings();
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
