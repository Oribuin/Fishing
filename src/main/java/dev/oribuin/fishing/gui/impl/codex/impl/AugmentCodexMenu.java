package dev.oribuin.fishing.gui.impl.codex.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.gui.impl.codex.BasicCodexMenu;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.model.augment.Augment;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class AugmentCodexMenu extends BasicCodexMenu<Augment> {

    /**
     * Creates a new plugin menu instance, with the specified name
     */
    public AugmentCodexMenu() {
        super("codex/augment");
    }


    /**
     * Open the GUI for the specified player
     *
     * @param player The player to open the GUI for
     */
    public void open(Player player) {
        PaginatedGui gui = this.createPaginated();
        this.placeItem("forward", x -> gui.next());
        this.placeItem("back", x -> gui.previous());

        List<Augment> content = this.getContent(player, x -> true);

        // Add all the fish to the GUI
        content.forEach(x -> {
            ItemConstruct construct = x.getDisplayItem();
            if (construct == null) return;

            ItemStack stack = construct.build(x.getPlaceholders());
            gui.addItem(new GuiItem(stack));
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
    public List<Augment> getContent(Player player, Predicate<Augment> condition) {
        return FishingPlugin.get().getAugmentManager().getAugments().values().stream().toList();
    }

}
