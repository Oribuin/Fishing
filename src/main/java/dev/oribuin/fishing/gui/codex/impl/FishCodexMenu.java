package dev.oribuin.fishing.gui.codex.impl;

import dev.oribuin.fishing.gui.codex.BasicCodexMenu;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Predicate;

public class FishCodexMenu extends BasicCodexMenu<Fish> {

    /**
     * Creates a new plugin menu instance, with the specified name
     */
    public FishCodexMenu() {
        super("codex/fish");
    }

    /**
     * Open the GUI for the specified player
     *
     * @param player The player to open the GUI for
     * @param tier   The tier to open the GUI for
     */
    public void open(Player player, Tier tier) {
        PaginatedGui gui = this.createPaginated();
        //        this.placeExtras(tier.placeholders()); // todo: tier placeholders
        this.placeItem("forward", x -> gui.next());
        this.placeItem("back", x -> gui.previous());

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
