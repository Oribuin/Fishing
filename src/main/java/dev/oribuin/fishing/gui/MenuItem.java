package dev.oribuin.fishing.gui;

import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Arrays;
import java.util.List;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })

public class MenuItem {

    private boolean enabled;
    private List<Integer> slots;
    private ItemConstruct item;

    public MenuItem() {
        this.enabled = true;
        this.slots = List.of(0);
        this.item = new ItemConstruct(Material.STONE);
    }

    public MenuItem(ItemConstruct construct, Integer... slots) {
        this.enabled = true;
        this.slots = Arrays.asList(slots);
        this.item = construct;
    }

    public MenuItem(ItemConstruct construct, List<Integer> slots) {
        this.enabled = true;
        this.slots = slots;
        this.item = construct;
    }

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

    public MenuItem setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public MenuItem setSlots(List<Integer> slots) {
        this.slots = slots;
        return this;
    }

    public ItemConstruct getItem() {
        return item;
    }

    public MenuItem setItem(ItemConstruct item) {
        this.item = item;
        return this;
    }
}
