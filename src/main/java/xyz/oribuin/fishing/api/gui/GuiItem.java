package xyz.oribuin.fishing.api.gui;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.util.ItemConstruct;

import java.util.ArrayList;
import java.util.List;

public class GuiItem implements Configurable {

    protected boolean enabled = true;
    protected String name = "item";
    protected List<Integer> slot = List.of(0);
    protected ItemConstruct item = ItemConstruct.EMPTY;

    /**
     * Place the item in the specified slot in the GUI
     *
     * @param gui      The GUI to place the item in
     * @param function The function to run when the item is clicked
     */
    public void place(BaseGui gui, GuiAction<InventoryClickEvent> function) {
        this.place(gui, StringPlaceholders.empty(), function);
    }

    /**
     * Place the item in the specified slot in the GUI
     *
     * @param gui          The GUI to place the item in
     * @param placeholders The placeholders to apply to the item
     * @param function     The function to run when the item is clicked
     */
    public void place(BaseGui gui, StringPlaceholders placeholders, GuiAction<InventoryClickEvent> function) {
        if (gui == null) return;
        if (!this.enabled) return;

        int guiSize = gui.getRows() * 9;
        this.slot.forEach(x -> {
            if (x < 0 || x >= guiSize) return;

            ItemStack item = this.item.build(placeholders);
            if (item == null) return;

            gui.setItem(slot, new dev.triumphteam.gui.guis.GuiItem(item, function));
        });
    }

    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.enabled = config.getBoolean("enabled", true);
        this.name = config.getString("name");

        int slot = config.getInt("slot", -1);
        if (slot > 0) this.slot = List.of(slot);

        List<String> slots = config.getStringList("slots");
        if (!slots.isEmpty()) this.slot = this.parseSlotList(slots);

        CommentedConfigurationSection item = config.getConfigurationSection("item");
        if (item != null) {
            this.item = ItemConstruct.deserialize(item);
        }

        System.out.println("Loaded item: " + this.name);
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("enabled", this.enabled);
        config.set("name", this.name);

        if (this.slot.size() == 1) config.set("slot", this.slot.get(0));
        else config.set("slots", this.slot.stream().map(String::valueOf).toList());

        CommentedConfigurationSection item = config.getConfigurationSection("item");
        if (item == null) item = config.createSection("item");

        this.item.serialize(item);
    }

    /**
     * Parse the slots from the configuration file to a list of integers
     *
     * @param lines The lines to parse
     *
     * @return The list of integers
     */
    private List<Integer> parseSlotList(List<String> lines) {
        List<Integer> slots = new ArrayList<>();
        for (String line : lines) {
            slots.addAll(this.parseSlots(line));
        }
        return slots;
    }

    /**
     * Parse the slots from the configuration file to a list of integers
     *
     * @param line The line to parse
     *
     * @return The list of integers
     */
    private List<Integer> parseSlots(String line) {
        try {
            String[] split = line.split("-");
            if (split.length == 2) {
                int start = Integer.parseInt(split[0]);
                int end = Integer.parseInt(split[1]);

                List<Integer> results = new ArrayList<>();
                for (int i = start; i <= end; i++) {
                    results.add(i);
                }

                return results;
            }

            return List.of(Integer.parseInt(line));
        } catch (NumberFormatException e) {
            return List.of(0);
        }
    }

    public boolean enabled() {
        return enabled;
    }

    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public List<Integer> slot() {
        return slot;
    }

    public void slot(List<Integer> slot) {
        this.slot = slot;
    }

    public ItemConstruct item() {
        return item;
    }

    public void item(ItemConstruct item) {
        this.item = item;
    }

}
