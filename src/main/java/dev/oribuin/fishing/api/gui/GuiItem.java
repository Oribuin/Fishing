package dev.oribuin.fishing.api.gui;

import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })

public class GuiItem {

    private boolean enabled;
    private List<Integer> slots = List.of(0);
    private ItemConstruct item;

    /**
     * Place the item in the specified slot in the GUI
     *
     * @param gui      The GUI to place the item in
     * @param function The function to run when the item is clicked
     */
    public void place(BaseGui gui, GuiAction<InventoryClickEvent> function) {
        this.place(gui, Placeholders.empty(), function);
    }

    /**
     * Place the item in the specified slot in the GUI
     *
     * @param gui          The GUI to place the item in
     * @param placeholders The placeholders to apply to the item
     * @param function     The function to run when the item is clicked
     */
    public void place(BaseGui gui, Placeholders placeholders, GuiAction<InventoryClickEvent> function) {
        if (gui == null) return;
        if (!this.enabled) return;

        int guiSize = gui.getRows() * 9;
        this.slots.forEach(x -> {
            if (x < 0 || x >= guiSize) return;

            ItemStack item = this.item.build(placeholders);
            if (item == null) return;

            gui.setItem(x, new dev.triumphteam.gui.guis.GuiItem(item, function));
        });
    }

    public boolean isEnabled() {
        return enabled;
    }

    public GuiItem setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public GuiItem setSlots(List<Integer> slots) {
        this.slots = slots;
        return this;
    }

    public ItemConstruct getItem() {
        return item;
    }

    public GuiItem setItem(ItemConstruct item) {
        this.item = item;
        return this;
    }
}
