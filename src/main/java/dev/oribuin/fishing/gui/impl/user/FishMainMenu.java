package dev.oribuin.fishing.gui.impl.user;

import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.item.component.TextureConstructType;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Supplier;

@ConfigSerializable
public class FishMainMenu extends PluginMenu<Gui> {

    public FishMainMenu() {
        super("main_menu");

        this.title = "Fishing | Main Menu";
        this.rows = 5;
        this.items.put("user-stats", new MenuItem(USER_STATS, 4));
        this.items.put("codex-menu", new MenuItem(CODEX_MENU, 21));
        this.extraItems.put("border", new MenuItem(BORDER, FishUtils.parseList("0-8", "36-44")));

    }

    /**
     * Open the menu for the player synchronously and mark the menu as being viewed
     *
     * @param player The player opening the menu
     */
    @Override
    public void open(Player player) {
        Fisher fisher = this.plugin.getDataManager().get(player.getUniqueId());

        this.gui = this.createMenu().get();
        Placeholders placeholders = Placeholders.builder()
                .add("player", player.getName())
                .addAll(fisher.getPlaceholders())
                .build();

        this.placeExtras(placeholders);
        this.placeItem("user-stats", placeholders); // TODO: Open stats menu
        this.placeItem("codex-menu", placeholders); // TODO: Open codex main menu
        super.open(player);
    }

    /**
     * Creates the menu for the plugin
     *
     * @return the resulting menu
     */
    @Override
    public Supplier<Gui> createMenu() {
        return () -> Gui.gui()
                .title(Component.text(this.title))
                .rows(this.rows)
                .disableAllInteractions()
                .create();
    }

    // region Items
    private static final ItemConstruct BORDER = new ItemConstruct(Material.BLACK_STAINED_GLASS_PANE)
            .setTooltip(false);

    private static final ItemConstruct USER_STATS = new ItemConstruct(Material.PLAYER_HEAD)
            .setName("<white>[<#94bc80>Your Stats<white>]")
            .setLore(
                    "<gray>Click here to view your current",
                    "<gray>fishing statistics",
                    "",
                    "<#94bc80>Information:",
                    " <#94bc80>- <white>Level: <#94bc80><level>",
                    " <#94bc80>- <white>Experience: <#94bc80><experience><gray>/<#94bc80><required_exp>",
                    " <#94bc80>- <white>Skill Points: <#94bc80><skill_points>"
            )
            .setTexture(new TextureConstructType("player-<player>"))
            .setGlowing(true);

    private static final ItemConstruct CODEX_MENU = new ItemConstruct(Material.KNOWLEDGE_BOOK)
            .setName("<white>[<#94bc80>The Codex<white>]")
            .setLore(
                    "<gray>Click to view information about",
                    "<gray>varies things within the plugin"
            );

    // endregion

}
