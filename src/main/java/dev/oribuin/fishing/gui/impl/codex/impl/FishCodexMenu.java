package dev.oribuin.fishing.gui.impl.codex.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.impl.codex.BasicCodexMenu;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.util.FishUtils;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FishCodexMenu extends BasicCodexMenu<Fish, FishCodexMenu.Config> {

    private final Tier tier;

    /**
     * Creates a new plugin menu instance, with the specified name
     */
    public FishCodexMenu(FishingPlugin plugin, Tier tier) {
        super(plugin, FishCodexMenu.Config.class);
        this.tier = tier;
        this.gui = this.createMenu().get();
        this.getContent().forEach(x -> this.gui.addItem(new GuiItem(this.getStack(x))));
    }

    /**
     * Get all the content that is going to be displayed in the codex
     *
     * @return The content to display in the menu
     */
    @Override
    public List<Fish> getContent() {
        return new ArrayList<>(this.tier.getFish().values())
                .stream()
                .sorted(Comparator.comparing(Fish::getName))
                .toList();
    }

    /**
     * Get the t value as an itemstack to display
     *
     * @param value The value to show
     *
     * @return The itemstack form
     */
    @Override
    public @NotNull ItemStack getStack(@NonNull Fish value) {
        return value.buildItem();
    }

    @ConfigSerializable
    public static class Config extends CodexGuiConfig {
        public Config() {
            this.title = "Fishing Codex | Fish";
            this.rows = 5;
            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList(
                    "0-9", "17-18", "26-27", "35-44"
            )));
        }
    }

}
