package xyz.oribuin.fishing.gui;

import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.gui.PluginMenu;

public class ExampleMenu extends PluginMenu {

    protected final PaginatedGui gui = this.createPaginated();

    public ExampleMenu(FishingPlugin plugin) {
        super(plugin, "example");
    }

    /**
     * Open the GUI for the specified player
     *
     * @param player The player to open the GUI for
     */
    public void open(Player player) {
        this.placeExtras(this.gui);

        // Place the example item in the GUI
        this.placeItem(this.gui, "example-item", x -> player.sendMessage("You clicked the example item!"));

        this.gui.update(); // Update the GUI
        this.gui.open(player); // Open the GUI for the player
    }


}
