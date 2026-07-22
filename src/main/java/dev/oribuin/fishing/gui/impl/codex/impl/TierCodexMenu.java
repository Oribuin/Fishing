package dev.oribuin.fishing.gui.impl.codex.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.impl.codex.BasicCodexMenu;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.util.FishUtils;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TierCodexMenu extends BasicCodexMenu<Tier, TierCodexMenu.Config> {

    /**
     * Creates a new plugin menu instance, with the specified name
     */
    public TierCodexMenu(FishingPlugin plugin) {
        super(plugin, TierCodexMenu.Config.class);
        this.gui = this.createMenu().get();

        this.getContent().forEach(t -> {
            ItemStack stack = this.getStack(t);
            if (stack == null) return;

            this.gui.addItem(new GuiItem(stack, event -> {
                FishCodexMenu codexMenu = new FishCodexMenu(plugin, t);
                codexMenu.open((Player) event.getWhoClicked());
            }));
        });
    }

    /**
     * Get all the content that is going to be displayed in the codex
     *
     * @return The content to display in the menu
     */
    @Override
    public List<Tier> getContent() {
        return new ArrayList<>(this.plugin.getTierManager().getTiers().values())
                .stream()
                .sorted(Comparator.comparingDouble(Tier::getChance).reversed())
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
    public @Nullable ItemStack getStack(@NonNull Tier value) {
        return value.getTierDisplay().create(value.getPlaceholders());
    }

    @ConfigSerializable
    public static class Config extends CodexGuiConfig {
        public Config() {
            this.title = "Fishing Codex | Tiers";
            this.rows = 3;
            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList(
                    "0-8", "18-26", "9", "17"
            )));
        }
    }

}
