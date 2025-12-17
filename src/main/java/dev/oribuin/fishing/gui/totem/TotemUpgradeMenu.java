package dev.oribuin.fishing.gui.totem;

import dev.oribuin.fishing.api.gui.PluginMenu;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.model.totem.Totem;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TotemUpgradeMenu extends PluginMenu<PaginatedGui> {

    public TotemUpgradeMenu() {
        super("totem/upgrades");
    }

    /**
     * Open the GUI for the specified player
     *
     * @param totem  The totem to open the GUI for
     * @param player The player to open the GUI for
     */
    public void open(Totem totem, Player player) {
        PaginatedGui gui = this.createPaginated();
        this.placeExtras(totem.placeholders());
        this.placeItem("forward", x -> gui.next());
        this.placeItem("back", x -> gui.previous());
        this.placeUpgrades(gui, totem, player);

        gui.open(player);
    }

    /**
     * Place the dynamic items in the GUI for the totem
     *
     * @param totem  The totem to place the items for
     * @param player The player to place the items for
     */
    private void placeUpgrades(PaginatedGui gui, Totem totem, Player player) {
        gui.clearPageItems();
        totem.upgrades().forEach((upgrade, level) -> {
            ItemConstruct displayItem = upgrade.icon();
            if (displayItem == null) return;

            ItemStack item = displayItem.build(upgrade.placeholders(totem));
            // todo: make less ugly
            gui.addItem(new GuiItem(item, x -> {
                if (upgrade.levelup(player, totem)) {
                    this.placeUpgrades(gui, totem, player);
                }
            }));
        });

        gui.update();
    }
}
