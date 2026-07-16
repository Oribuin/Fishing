package dev.oribuin.fishing.gui;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.manager.MenuManager;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.scheduler.task.ScheduledTask;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public abstract class PluginMenu<T extends BaseGui, Z extends GuiConfig> {

    public static final ItemConstruct BORDER = ItemConstruct.of(Material.BLACK_STAINED_GLASS_PANE)
            .setProperty(ConstructType.TOOLTIP, x -> x.setVisible(false));

    protected final FishingPlugin plugin;
    protected final Class<Z> configClass;
    protected transient boolean viewed;
    protected transient ScheduledTask task;
    protected T gui;
    protected Z config;

    protected final transient GuiAction<InventoryClickEvent> EMPTY = event -> {};
    protected final transient GuiAction<InventoryClickEvent> CANCELLED = event -> {
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
    };

    /**
     * Creates a new menu for the plugin to use
     *
     * @param plugin      The plugin instance
     * @param configClass The config for the menu
     */
    public PluginMenu(FishingPlugin plugin, Class<Z> configClass) {
        this.plugin = plugin;
        this.configClass = configClass;
        this.viewed = false;
        this.config = MenuManager.getLoader().get(configClass);
    }

    /**
     * Open the menu for the player synchronously and mark the menu as being viewed
     *
     * @param player The player opening the menu
     */
    public void open(Player player) {
        if (this.gui == null) return;

        this.viewed = true;
        PluginScheduler.get().runTask(() -> this.gui.open(player));

        // If the gui is tickable, tick the gui
        if (this instanceof GuiTickable tickable) {
            long delay = tickable.getTickDelay().toSeconds();

            this.task = PluginScheduler.get().runTaskTimerAsync(() -> {
                // Gui doesn't exist, don't tick & cancel
                if (this.gui == null) {
                    if (this.task != null) task.cancel();
                    return;
                }

                // GUI is viewed but no longer has viewers, cancel
                if (this.viewed && this.gui.getInventory().getViewers().isEmpty()) {
                    if (this.task != null) task.cancel();
                    return;
                }

                tickable.tick();
            }, delay, delay, TimeUnit.SECONDS);
        }
    }

    /**
     * Creates the menu for the plugin
     *
     * @return the resulting menu
     */
    public abstract Supplier<T> createMenu();

    /**
     * Set the dummy items as defined in the config
     *
     * @param placeholders The placeholders
     */
    public void setDummyIcons(Placeholders placeholders) {
        this.config.getDummyItems().forEach(icon -> icon.place(this.gui, placeholders, EMPTY));
    }

    /**
     * Set the dummy items as defined in the config
     */
    public void setDummyIcons() {
        this.setDummyIcons(Placeholders.empty());
    }

}
