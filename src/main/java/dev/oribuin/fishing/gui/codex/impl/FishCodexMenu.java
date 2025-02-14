package dev.oribuin.fishing.gui.codex.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.gui.codex.BasicCodexMenu;
import dev.oribuin.fishing.gui.totem.TotemMainMenu;
import dev.oribuin.fishing.manager.MenuManager;
import dev.oribuin.fishing.manager.TierManager;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.model.totem.Totem;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.naming.Context;
import java.util.List;
import java.util.function.Predicate;

public class FishCodexMenu extends BasicCodexMenu<Fish> {

    /**
     * Creates a new plugin menu instance, with the specified name
     *
     * @param plugin The plugin instance
     */
    public FishCodexMenu(FishingPlugin plugin) {
        super(plugin, "codex/fish");
    }

    /**
     * Open the GUI for the specified player
     *
     * @param player The player to open the GUI for
     * @param tier   The tier to open the GUI for
     */
    public static void open(Player player, Tier tier) {
        FishCodexMenu menu = MenuManager.from(FishCodexMenu.class);
        if (menu == null) return;

        PaginatedGui gui = menu.createPaginated();
        menu.placeExtras(tier.placeholders());
        menu.placeItem("forward", x -> gui.next());
        menu.placeItem("back", x -> gui.previous());

        List<Fish> content = menu.getContent(player, fish -> fish.tierName().equalsIgnoreCase(tier.name()));

        // Add all the fish to the GUI
        content.forEach(fish -> {
            gui.addItem(new GuiItem(fish.createItemStack()));
        });

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
        return this.plugin.getManager(TierManager.class)
                .fish()
                .stream()
                .filter(condition)
                .toList();
    }

}
