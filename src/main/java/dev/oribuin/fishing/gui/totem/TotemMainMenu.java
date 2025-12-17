package dev.oribuin.fishing.gui.totem;

import dev.oribuin.fishing.api.gui.PluginMenu;
import dev.oribuin.fishing.gui.MenuRegistry;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import dev.oribuin.fishing.util.Placeholders;
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
        Placeholders placeholders = totem.placeholders();
        this.placeItem("totem-stats", placeholders);
        this.placeItem("totem-upgrade", placeholders, x -> MenuRegistry.get(TotemUpgradeMenu.class).open(totem, player));

        // The totem is active, display the active totem item
        boolean active = totem.getProperty(KeyRegistry.TOTEM_ACTIVE, false);
        if (active) {
            this.placeItem("totem-active", placeholders);
        }

        // The totem is not active and is on cooldown, display the cooldown item
        if (!active && totem.onCooldown()) {
            this.placeItem("totem-cooldown", placeholders);
        }

        // The totem is not active and is not on cooldown, display the button to activate the totem
        if (!active && !totem.onCooldown()) {
            this.placeItem("totem-activate", placeholders, x -> {
                totem.activate(player); // Activate the totem
                player.sendMessage("§aYou have activated the totem!");
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN); // Close the player's inventory
            });
        }

        // TODO: Add Totem Upgrades to the GUI
        gui.update();
    }

}
