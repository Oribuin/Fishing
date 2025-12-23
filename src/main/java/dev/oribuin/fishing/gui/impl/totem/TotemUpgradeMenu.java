package dev.oribuin.fishing.gui.impl.totem;

import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.item.component.TooltipConstructType;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.util.FishUtils;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class TotemUpgradeMenu extends PluginMenu<PaginatedGui> {

    public TotemUpgradeMenu() {
        super("totem/upgrades");

        this.title = "Fishing Totem - Upgrades";
        this.rows = 3;
        this.items.put("page-forward", new MenuItem(PAGE_FORWARD, 5));
        this.items.put("page-backward", new MenuItem(PAGE_BACKWARD, 3));
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
        PaginatedGui gui = this.createPaginated();
        this.placeExtras(totem.placeholders());
        this.placeItem("page-forward", x -> gui.next());
        this.placeItem("page-backward", x -> gui.previous());
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
            ItemStack item = UPGRADE_STYLE.build(upgrade.placeholders(totem));
            // todo: make less ugly
            gui.addItem(new GuiItem(item, x -> {
                if (upgrade.levelup(player, totem)) {
                    this.placeUpgrades(gui, totem, player);
                }
            }));
        });

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

    private static final ItemConstruct PAGE_FORWARD = new ItemConstruct(Material.ARROW)
            .setName("<white>[<#94bc80>Next Page<white>")
            .setLore("<gray>Click here to go to the next page");

    private static final ItemConstruct PAGE_BACKWARD = new ItemConstruct(Material.ARROW)
            .setName("<white>[<#94bc80>Previous Page<white>")
            .setLore("<gray>Click here to go to the previous page");

    private static final ItemConstruct UPGRADE_STYLE = new ItemConstruct(Material.HEART_OF_THE_SEA) // Upgrades will choose their own item, idgaf
            .setName("<white>[<#94bc80><bold><name><white>]")
            .setLore(
                    "<gray><description>",
                    "",
                    "<#94bc80>Information",
                    " <#94bc80>- <gray>Current: <white><level>",
                    " <#94bc80>- <gray>Max Level: <white><max_level>",
                    ""
            )
            .setGlowing(true);

    // endregion


}
