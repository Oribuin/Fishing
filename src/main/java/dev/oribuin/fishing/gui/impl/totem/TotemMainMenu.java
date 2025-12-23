package dev.oribuin.fishing.gui.impl.totem;

import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.item.component.TooltipConstructType;
import dev.oribuin.fishing.manager.MenuManager;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class TotemMainMenu extends PluginMenu<Gui> {

    public TotemMainMenu() {
        super("totem/main_menu");

        this.title = "Fishing Totem";
        this.rows = 3;
        this.items.put("totem-upgrade", new MenuItem(TOTEM_UPGRADE, 3));
        this.items.put("totem-activate", new MenuItem(TOTEM_ACTIVATE, 13));
        this.items.put("totem-cooldown", new MenuItem(TOTEM_COOLDOWN, 13));
        this.items.put("totem-active", new MenuItem(TOTEM_ACTIVE, 13));
        this.extraItems.put("totem-stats", new MenuItem(TOTEM_STATS, 4));
        this.extraItems.put("border", new MenuItem(BORDER, FishUtils.parseList("0-8", "18-26", "9", "17")));
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
        this.placeItem("totem-upgrade", placeholders, x -> MenuManager.get(TotemUpgradeMenu.class).open(totem, player));

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

    // region Items
    private static final ItemConstruct BORDER = new ItemConstruct(Material.BLACK_STAINED_GLASS_PANE)
            .setTooltip(new TooltipConstructType().setVisible(false));

    private static final ItemConstruct TOTEM_STATS = new ItemConstruct(Material.OAK_HANGING_SIGN)
            .setName("<white>[<#94bc80>Totem Details<white>]")
            .setLore(
                    "<gray>Here are the current upgrades",
                    "<gray>active for this fishing totem",
                    "",
                    "<#94bc80>Statistics:",
                    " <#94bc80>- <white>Active: <#94bc80><active>",
                    " <#94bc80>- <white>Owner: <#94bc80><owner>",
                    " <#94bc80>- <white>Radius: <#94bc80><upgrade_radius_value>",
                    " <#94bc80>- <white>Duration: <#94bc80><upgrade_duration_value>",
                    " <#94bc80>- <white>Cooldown: <#94bc80><upgrade_cooldown_value>"
            )
            .setGlowing(true);

    private static final ItemConstruct TOTEM_UPGRADE = new ItemConstruct(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
            .setName("<white>[<#94bc80>Totem Upgrades<white>")
            .setLore(
                    "<gray>Click here to view and level",
                    "<gray>up this fishing totem",
                    "",
                    "<#94bc80>Levels:",
                    " <#94bc80>- <white>Radius: <#94bc80><upgrade_radius_value>",
                    " <#94bc80>- <white>Duration: <#94bc80><upgrade_duration_value>",
                    " <#94bc80>- <white>Cooldown: <#94bc80><upgrade_cooldown_value>"
            )
            .setGlowing(true)
            .setTooltip(new TooltipConstructType().setHiddenComponents(List.of("TRIM_MATERIAL")));

    private static final ItemConstruct TOTEM_ACTIVATE = new ItemConstruct(Material.LIME_DYE)
            .setName("<white>[<#05e653>Activate Totem<white>")
            .setLore(
                    "<gray>Click here to active this totem",
                    "",
                    "<#05e653>Details:",
                    " <#05e653>- <white>Radius: <#05e653><upgrade_radius_value>",
                    " <#05e653>- <white>Duration: <#05e653><upgrade_duration_value>",
                    " <#05e653>- <white>Cooldown: <#05e653><upgrade_cooldown_value>"
            )
            .setGlowing(true);

    private static final ItemConstruct TOTEM_COOLDOWN = new ItemConstruct(Material.RED_DYE)
            .setName("<white>[<#e60505>On Cooldown<white>")
            .setLore(
                    "<gray>This totem is currently on cooldown",
                    "",
                    "<#e60505>Details:",
                    " <#e60505>- <white>Radius: <#e60505><upgrade_radius_value>",
                    " <#e60505>- <white>Duration: <#e60505><upgrade_duration_value>",
                    " <#e60505>- <white>Cooldown: <#e60505><upgrade_cooldown_value>"
            )
            .setGlowing(true);

    private static final ItemConstruct TOTEM_ACTIVE = new ItemConstruct(Material.LIME_DYE)
            .setName("<white>[<#e65f05>Currently Active<white>")
            .setLore(
                    "<gray>Your totem is currently active",
                    "",
                    "<#e65f05>- <white>Time Remaining: <#e65f05>%upgrade_duration_timer%"
            )
            .setGlowing(true);

    // endregion

}
