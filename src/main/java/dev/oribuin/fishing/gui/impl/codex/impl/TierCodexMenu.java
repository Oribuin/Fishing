package dev.oribuin.fishing.gui.impl.codex.impl;

import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.impl.codex.BasicCodexMenu;
import dev.oribuin.fishing.manager.MenuManager;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@ConfigSerializable
public class TierCodexMenu extends BasicCodexMenu<Tier> {

    /**
     * Creates a new plugin menu instance, with the specified name
     */
    public TierCodexMenu() {
        super("codex/tier");

        this.title = "Fishing Codex | Tiers";
        this.rows = 3;
        this.items.put("page-backward", new MenuItem(PAGE_BACKWARD, 3));
        this.items.put("codex-main-menu", new MenuItem(CODEX_MAIN_MENU, 4));
        this.items.put("page-forward", new MenuItem(PAGE_FORWARD, 5));
        this.extraItems.put("border", new MenuItem(BORDER, FishUtils.parseList("0-8", "18-26", "9", "17")));
    }

    /**
     * Open the GUI for the specified player
     *
     * @param player The player to open the GUI for
     */
    @Override
    public void open(Player player) {
        this.gui = this.createMenu().get();
        this.placeExtras(Placeholders.empty());
        this.placeItem("page-forward", x -> gui.next());
        this.placeItem("page-backward", x -> gui.previous());

        List<Tier> content = new ArrayList<>(this.getContent(player, tier -> true));
        content.sort(Comparator.comparingDouble(Tier::getChance));
        Collections.reverse(content);

        // Add all the fish to the GUI
        content.forEach(tier -> {
            ItemStack stack = tier.getTierDisplay().build();
            GuiItem guiItem = new GuiItem(stack, action -> MenuManager.get(FishCodexMenu.class)
                    .open((Player) action.getWhoClicked(), tier));
            gui.addItem(guiItem);
        });

        super.open(player);
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
    public List<Tier> getContent(Player player, Predicate<Tier> condition) {
        return this.plugin.getTierManager().getTiers().values().stream().toList();
    }

}
