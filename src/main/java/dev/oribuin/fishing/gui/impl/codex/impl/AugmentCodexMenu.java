package dev.oribuin.fishing.gui.impl.codex.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.impl.codex.BasicCodexMenu;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@ConfigSerializable
public class AugmentCodexMenu extends BasicCodexMenu<Augment> {

    /**
     * Creates a new plugin menu instance, with the specified name
     */
    public AugmentCodexMenu() {
        super("codex/augment");

        this.title = "Fishing Codex | Augment";
        this.rows = 4;
        this.items.put("page-backward", new MenuItem(PAGE_BACKWARD, 3));
        this.items.put("codex-main-menu", new MenuItem(CODEX_MAIN_MENU, 4));
        this.items.put("page-forward", new MenuItem(PAGE_FORWARD, 5));
        this.extraItems.put("border", new MenuItem(BORDER, FishUtils.parseList("0-8", "27-35")));
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
        this.placeItem("codex-main-menu");
        this.placeItem("page-forward", x -> gui.next());
        this.placeItem("page-backward", x -> gui.previous());

        List<Augment> content = this.getContent(player, x -> true);

        // Add all the fish to the GUI
        content.forEach(x -> {
            ItemConstruct construct = x.getDisplayItem();
            if (construct == null) return;

            ItemStack stack = construct.build(x.getPlaceholders());
            gui.addItem(new GuiItem(stack));
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
    public List<Augment> getContent(Player player, Predicate<Augment> condition) {
        return FishingPlugin.get().getAugmentManager().getAugments().values()
                .stream()
                .sorted(
                        Comparator.comparing(Augment::getName)
                                .thenComparing(Augment::getRequiredLevel)
                )
                .toList();
    }

}
