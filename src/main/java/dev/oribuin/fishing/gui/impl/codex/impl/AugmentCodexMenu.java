package dev.oribuin.fishing.gui.impl.codex.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.impl.codex.BasicCodexMenu;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.util.FishUtils;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AugmentCodexMenu extends BasicCodexMenu<Augment, AugmentCodexMenu.Config> {

    /**
     * Creates a new plugin menu instance, with the specified name
     */
    public AugmentCodexMenu(FishingPlugin plugin) {
        super(plugin, AugmentCodexMenu.Config.class);
        this.gui = this.createMenu().get();
        
        this.getContent().forEach(x -> {
            ItemStack stack = this.getStack(x);
            if (stack != null) this.gui.addItem(new GuiItem(stack));
        });
    }

    /**
     * Get the t value as an itemstack to display
     *
     * @param value The value to show
     *
     * @return The itemstack form
     */
    @Override
    public @Nullable ItemStack getStack(@NonNull Augment value) {
        return value.getDisplayItem().create(value.getPlaceholders());
    }

    /**
     * Get all the content that is going to be displayed in the codex
     *
     * @return The content to display in the menu
     */
    @Override
    public List<Augment> getContent() {
        return new ArrayList<>(this.plugin.getAugmentManager().getAugments().values())
                .stream()
                .sorted(
                        Comparator.comparing(Augment::getName)
                                .thenComparing(Augment::getRequiredLevel))
                .toList();
    }

    @ConfigSerializable
    public static class Config extends CodexGuiConfig {
        public Config() {
            this.title = "Fishing Codex | Augments";
            this.rows = 4;
            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList(
                    "0-9", "17-18", "26-35"
            )));
        }
    }

}
