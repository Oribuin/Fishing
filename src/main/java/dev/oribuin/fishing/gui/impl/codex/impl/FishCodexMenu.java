package dev.oribuin.fishing.gui.impl.codex.impl;

import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.impl.codex.BasicCodexMenu;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.util.FishUtils;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.function.Predicate;

@ConfigSerializable
public class FishCodexMenu extends BasicCodexMenu<Fish> {

    /**
     * Creates a new plugin menu instance, with the specified name
     */
    public FishCodexMenu() {
        super("codex/fish");

        this.title = "Fishing Codex | Fish";
        this.rows = 6;
        this.items.put("page-backward", new MenuItem(PAGE_BACKWARD, 3));
        this.items.put("codex-main-menu", new MenuItem(CODEX_MAIN_MENU, 4));
        this.items.put("page-forward", new MenuItem(PAGE_FORWARD, 5));
        this.extraItems.put("border", new MenuItem(BORDER, FishUtils.parseList("0-8", "45-53")));
    }

    /**
     * Open the GUI for the specified player
     *
     * @param player The player to open the GUI for
     * @param tier   The tier to open the GUI for
     */
    public void open(Player player, Tier tier) {
        PaginatedGui gui = this.createPaginated();
        this.placeExtras(tier.placeholders());
        this.placeItem("page-forward", x -> gui.next());
        this.placeItem("page-backward", x -> gui.previous());

        List<Fish> content = this.getContent(player, fish -> fish.getTier().equalsIgnoreCase(tier.getName()));

        // Add all the fish to the GUI
        content.forEach(fish -> gui.addItem(new GuiItem(fish.buildItem())));

        gui.open(player);
    }

    /**
     * Get all the content that is going to be displayed in the codex
     *
     * @param player    The player to get the content for
     * @param condition The condition required to display the content
     *
     * @return The content to display in the menu
     */
    @Override
    public List<Fish> getContent(Player player, Predicate<Fish> condition) {
        return this.plugin.getTierManager().getAllFish()
                .stream()
                .filter(condition)
                .toList();
    }

}
