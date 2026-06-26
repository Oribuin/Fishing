package dev.oribuin.fishing.gui.impl.totem;

import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.item.component.TooltipConstructType;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.util.FishUtils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Supplier;

@ConfigSerializable
public class TotemUpgradeMenu extends PluginMenu<PaginatedGui> {

    public TotemUpgradeMenu() {
        super("totem/upgrades");

        this.title = "Fishing Totem - Upgrades";
        this.rows = 3;
        this.items.put("page-forward", new MenuItem(PAGE_FORWARD, 5));
        this.items.put("page-backward", new MenuItem(PAGE_BACKWARD, 3));
        this.items.put("totem-name", new MenuItem(TOTEM_NAME, 16));
        this.items.put("totem-privacy", new MenuItem(TOTEM_PRIVACY, 25));
        this.extraItems.put("totem-stats", new MenuItem(TOTEM_STATS, 4));
        this.extraItems.put("border", new MenuItem(BORDER, FishUtils.parseList("0-9", "26-39","17-18")));
    }

    /**
     * Open the GUI for the specified player
     *
     * @param totem  The totem to open the GUI for
     * @param player The player to open the GUI for
     */
    public void open(Totem totem, Player player) {
        this.gui = this.createMenu().get();
        this.placeExtras(totem.placeholders());
        this.placeItem("page-forward", x -> gui.next());
        this.placeItem("page-backward", x -> gui.previous());
        this.placeItem("totem-name", );
        
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
        totem.getUpgrades().forEach((upgrade, level) -> {
            ItemStack item = UPGRADE_STYLE.build(upgrade.getPlaceholders(totem));
            // todo: make less ugly
            gui.addItem(new GuiItem(item, x -> {
                if (upgrade.increaseLevel(player, totem)) {
                    this.placeUpgrades(gui, totem, player);
                }
            }));
        });

        gui.update();
    }

    /**
     * Creates the menu for the plugin
     *
     * @return the resulting menu
     */
    @Override
    public Supplier<PaginatedGui> createMenu() {
        return () -> Gui.paginated()
                .title(Component.text(this.title))
                .rows(this.rows)
                .disableAllInteractions()
                .create();
    }

    // region Items
    private static final ItemConstruct BORDER = new ItemConstruct(Material.BLACK_STAINED_GLASS_PANE)
            .setTooltip(new TooltipConstructType(false));

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
            .setName("<white>[<#94bc80>Next Page<white>]")
            .setLore("<gray>Click here to go to the next page");

    private static final ItemConstruct PAGE_BACKWARD = new ItemConstruct(Material.ARROW)
            .setName("<white>[<#94bc80>Previous Page<white>]")
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

    private static final ItemConstruct TOTEM_NAME = new ItemConstruct(Material.NAME_TAG)
            .setName("<white>[<#94bc80>Totem Name<white>]")
            .setLore(
                    "<gray>Change the display name for this",
                    "<gray>fishing totem"

            )
            .setGlowing(true);

    private static final ItemConstruct TOTEM_PRIVACY = new ItemConstruct(Material.TRIAL_KEY)
            .setName("<white>[<#94bc80>Totem Privacy<white>]")
            .setLore(
                    "<gray>Change the level of access that others",
                    "<gray>have to this fishing totem",
                    "",
                    " <#94bc80>- <white>Status: <#94bc80><privacy>"

            )
            .setGlowing(true);

    // endregion


}
