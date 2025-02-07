package dev.oribuin.fishing.api.gui;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.config.Configurable;
import dev.oribuin.fishing.model.item.ItemConstruct;

import java.util.ArrayList;
import java.util.List;

public class GuiItem implements Configurable {

    protected boolean enabled = true;
    protected List<Integer> slot = List.of(0);
    protected ItemConstruct item = ItemConstruct.of(Material.STONE).tooltip(false);

    public GuiItem() {
    }

    public GuiItem(ItemConstruct item) {
        this.item = item;
    }

    public GuiItem(int slot, ItemConstruct item) {
        this.slot = List.of(slot);
        this.item = item;
    }

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
     * Initialize a {@link CommentedConfigurationSection} from a configuration file to establish the settings
     * for the configurable class, will be automatically called when the configuration file is loaded using {@link #reload()}
     * <p>
     * If your class inherits from another configurable class, make sure to call super.loadSettings(config)
     * to save the settings from the parent class
     * <p>
     * A class must be initialized before settings are loaded, If you wish to have a configurable data class style, its best to create a
     * static method that will create a new instance and call this method on the new instance
     * <p>
     * The {@link CommentedConfigurationSection} should never be null, when creating a new section,
     * use {@link #pullSection(CommentedConfigurationSection, String)} to establish new section if it doesn't exist
     *
     * @param config The {@link CommentedConfigurationSection} to load the settings from, this cannot be null.
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.enabled = config.getBoolean("enabled", true);

        int slot = config.getInt("slot", -1);
        if (slot > 0) this.slot = List.of(slot);

        List<String> slots = config.getStringList("slots");
        if (!slots.isEmpty()) this.slot = this.parseSlotList(slots);

        this.item = ItemConstruct.deserialize(this.pullSection(config, "display-item"));
    }

    /**
     * Serialize the settings of the configurable class into a {@link CommentedConfigurationSection} to be saved later
     * <p>
     * This functionality will not update the configuration file, it will only save the settings into the section to be saved later.
     * <p>
     * The function {@link #reload()} will save the settings on first load, please override this method if you wish to save the settings regularly
     * New sections should be created using {@link #pullSection(CommentedConfigurationSection, String)}
     *
     * @param config The {@link CommentedConfigurationSection} to save the settings to, this cannot be null.
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("enabled", this.enabled);

        if (this.slot.size() == 1) config.set("slot", this.slot.get(0));
        else config.set("slots", this.slot.stream().map(String::valueOf).toList());

        this.item.saveSettings(this.pullSection(config, "display-item"));
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

    public List<Integer> slot() {
        return slot;
    }

    public void slot(List<Integer> slot) {
        this.slot = slot;
    }

    public void parseSlot(String slot) {
        this.slot = this.parseSlots(slot);
    }

    public void parseSlots(List<String> slots) {
        this.slot = this.parseSlotList(slots);
    }

    public ItemConstruct item() {
        return item;
    }

    public void item(ItemConstruct item) {
        this.item = item;
    }

}
