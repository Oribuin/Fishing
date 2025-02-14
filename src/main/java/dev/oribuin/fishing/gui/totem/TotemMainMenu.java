package dev.oribuin.fishing.gui.totem;

import dev.oribuin.fishing.api.gui.PluginMenu;
import dev.oribuin.fishing.model.totem.Totem;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class TotemMainMenu extends PluginMenu<Gui> {

    public TotemMainMenu() {
        super("totem/main_menu");
    }

    /**
     * Open the GUI for the specified player
     *
     * @param totem  The totem to open the GUI for
     * @param player The player to open the GUI for
     */
    public void open(Totem totem, Player player) {
        Gui gui = this.createRegular();
        this.placeExtras(totem.placeholders());
        this.updateTask(() -> this.placeDynamics(totem, player));
        
        gui.open(player);
    }

    /**
     * Place the dynamic items in the GUI for the totem
     *
     * @param totem  The totem to place the items for
     * @param player The player to place the items for
     */
    private void placeDynamics(Totem totem, Player player) {
        this.placeItem("totem-stats", totem.placeholders());

        // The totem is active, display the active totem item
        if (totem.active()) {
            this.placeItem("totem-active", totem.placeholders());
        }

        // The totem is not active and is on cooldown, display the cooldown item
        if (!totem.active() && totem.onCooldown()) {
            this.placeItem("totem-cooldown", totem.placeholders());
        }

        // The totem is not active and is not on cooldown, display the button to activate the totem
        if (!totem.active() && !totem.onCooldown()) {
            this.placeItem("totem-activate", totem.placeholders(), x -> {
                totem.activate(); // Activate the totem
                player.sendMessage("§aYou have activated the totem!");
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN); // Close the player's inventory
            });
        }

        // TODO: Add Totem Upgrades to the GUI
        gui.update();
    }

}
