package xyz.oribuin.fishing.api.gui;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.util.FishUtils;
import xyz.oribuin.fishing.util.ItemConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MenuIcon implements Configurable {

    private final String path;
    private List<Integer> slots;
    private ItemConstruct item;
    private StringPlaceholders placeholders;
    private Consumer<InventoryClickEvent> consumer;
    private boolean tickable;

    public MenuIcon(String path) {
        this.path = path;
        this.slots = new ArrayList<>();
        this.item = ItemConstruct.EMPTY;
        this.placeholders = StringPlaceholders.empty();
        this.consumer = event -> {};
        this.tickable = false;
    }

    /**
     * Construct a new MenuIcon with the given path
     *
     * @param path The path of the icon
     *
     * @return The new MenuIcon
     */
    public static MenuIcon construct(String path) {
        return new MenuIcon(path);
    }

    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.item = ItemConstruct.deserialize(config.getConfigurationSection(this.path + ".item"));
        this.tickable = config.getBoolean(this.path + ".tickable", false);

        int slot = config.getInt(this.path + ".slot", -1);
        if (slot > 0) this.slots = List.of(slot);

        List<String> slots = config.getStringList(this.path + ".slots");
        if (!slots.isEmpty()) this.slots = this.parseSlotList(slots);
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set(this.path + ".slots", this.slots);
        config.set(this.path + ".tickable", this.tickable);
        config.set(this.path + ".slots", this.slots);

        // serialize the item
        CommentedConfigurationSection itemSection = config.getConfigurationSection(this.path + ".item");
        if (itemSection == null) itemSection = config.createSection(this.path + ".item");

        this.item.serialize(itemSection);
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

    public String path() {
        return path;
    }

    public List<Integer> slots() {
        return slots;
    }

    public MenuIcon slots(List<Integer> slots) {
        this.slots = slots;
        return this;
    }

    public ItemConstruct item() {
        return item;
    }

    public MenuIcon item(ItemConstruct item) {
        this.item = item;
        return this;
    }

    public StringPlaceholders placeholders() {
        return placeholders;
    }

    public MenuIcon placeholders(StringPlaceholders placeholders) {
        this.placeholders = placeholders;
        return this;
    }

    public boolean tickable() {
        return tickable;
    }

    public MenuIcon tickable(boolean tickable) {
        this.tickable = tickable;
        return this;
    }

    public Consumer<InventoryClickEvent> consumer() {
        return consumer;
    }

    public MenuIcon consumer(Consumer<InventoryClickEvent> consumer) {
        this.consumer = consumer;
        return this;
    }

}
