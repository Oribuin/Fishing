package dev.oribuin.fishing.command.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.command.FishCommand;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.gui.type.bipaginated.BiPaginatedGui;
import dev.oribuin.fishing.gui.type.bipaginated.PagePair;
import dev.oribuin.fishing.gui.type.bipaginated.PairDirection;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import dev.oribuin.fishing.util.FishUtils;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static dev.oribuin.fishing.config.item.component.TooltipItemType.HIDDEN;

public class DebugCommand implements FishCommand {

    private final Table<UUID, Material, Boolean> toggledMaterials = HashBasedTable.create();
    private final FishingPlugin plugin;

    private static final List<Material> AVAILABLE = Arrays.stream(Material.values())
            .filter(x -> x.isItem() && !x.isAir())
            .toList();

    /**
     * Create a new command instance with the provided plugin instance.
     *
     * @param plugin The plugin instance.
     */
    public DebugCommand(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Debug all the placeholders a totem has
     *
     * @param player The sender running the command
     */
    @Command("fishing|fish debug bipaginated")
    @Permission("fishing.debug")
    @CommandDescription("Get a bunch of bipaginated items")
    public void bipaginated(Player player) {

        PluginScheduler.get().runTaskAtEntity(player, () -> {
            BiPaginatedGui gui = BiPaginatedGui.builder()
                    .title(FishUtils.kyorify("Material Toggle"))
                    .disableAllInteractions()
                    .rows(4)
                    .create();

            GuiItem border = new GuiItem(ItemConstruct.of(Material.BLACK_STAINED_GLASS_PANE)
                    .setProperty(ConstructType.TOOLTIP, HIDDEN)
                    .create());

            gui.setItem(FishUtils.parseList("0-8", "27-35"), border);

            gui.setItem(0, new GuiItem(ItemConstruct.of(Material.ARROW)
                    .setName("<#93bc80>Previous Page")
                    .setLore("<gray>Click to change the page")
                    .create(), event -> gui.previous()));


            gui.setItem(4, new GuiItem(ItemConstruct.of(Material.BARRIER)
                    .setName("<red>Reset Toggles")
                    .setLore("<gray>Resets the player's current toggles")
                    .create(), event -> {
                this.toggledMaterials.row(event.getWhoClicked().getUniqueId()).clear();
                event.getWhoClicked().sendMessage(FishUtils.kyorify("<red>Reset Toggle Status"));
                gui.close(event.getWhoClicked());
            }));

            gui.setItem(3, new GuiItem(ItemConstruct.of(Material.COMPARATOR)
                    .setName("<red>Invert Toggles")
                    .setLore("<gray>Resets the player's current toggles")
                    .create(), event -> {

                Map<Material, Boolean> toggles = this.toggledMaterials.row(event.getWhoClicked().getUniqueId());
                AVAILABLE.forEach(material -> {
                    boolean current = toggles.getOrDefault(material, false);
                    toggles.put(material, !current);
                });

                event.getWhoClicked().sendMessage(FishUtils.kyorify("<red>Inverted Toggle Status"));
                gui.close(event.getWhoClicked());
            }));

            gui.setItem(8, new GuiItem(ItemConstruct.of(Material.ARROW)
                    .setName("<#93bc80>Next Page")
                    .setLore("<gray>Click to change the page")
                    .create(), event -> gui.next()));

            UUID uuid = player.getUniqueId();
            AVAILABLE.forEach(material -> {
                Boolean status = this.toggledMaterials.get(uuid, material);
                if (status == null) status = false;

                // add the item
                gui.addItem(this.getPair(gui, material, status));
            });

            gui.open(player);
        });
    }

    public PagePair getPair(BiPaginatedGui gui, Material material, boolean currentStatus) {
        Consumer<InventoryClickEvent> action = event -> {
            //                    if (!(event.getInventory().getHolder() instanceof BiPaginatedGui newGui)) return;

            UUID target = event.getWhoClicked().getUniqueId();
            Boolean status = this.toggledMaterials.get(target, material);
            if (status == null) status = currentStatus;

            Integer targetSlot = gui.getPrimarySlot(event.getSlot());
            if (targetSlot == null) return;

            status = !status;
            this.toggledMaterials.put(target, material, status);
            PagePair newPair = this.getPair(gui, material, status);
            gui.updatePageItem(targetSlot, newPair.getPrimary(), newPair.getSecondary());

            event.getWhoClicked().sendMessage(FishUtils.kyorify("<#93bc80>You have toggled the item " + FishUtils.capitalizeFully(material.name()) + " to " + (status ? "<green>Enabled" : "<red>Disabled")));
        };


        GuiItem enabled = new GuiItem(ItemConstruct.of(Material.GREEN_DYE)
                .setName("<green>Enabled")
                .setLore("<gray>Left Click to toggle")
                .create(), action::accept);

        GuiItem disabled = new GuiItem(ItemConstruct.of(Material.RED_DYE)
                .setName("<red>Disabled")
                .setLore("<gray>Left Click to toggle")
                .create(), action::accept);


        return new PagePair(
                PairDirection.VERTICAL_DOWN,
                ItemBuilder.from(material).asGuiItem(),
                currentStatus ? enabled : disabled
        );
    }

    /**
     * Debug all the placeholders a totem has
     *
     * @param player The sender running the command
     */
    @Command("fishing|fish debug placeholders totem")
    @Permission("fishing.debug")
    @CommandDescription("Get the placeholders for a totem in your hand")
    public void debugTotem(Player player) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || stack.getType() == Material.AIR) {
            player.sendMessage("no item in hand :/");
            return;
        }


        if (!meta.getPersistentDataContainer().has(KeyRegistry.TOTEM_ACTIVE.key(), KeyRegistry.TOTEM_ACTIVE)) {
            player.sendMessage("no totem :(");
            return;
        }

        player.sendMessage("Totem Placeholders: ");
        Totem totem = new Totem(player.getLocation(), meta.getPersistentDataContainer());
        totem.getPlaceholders().getAll()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> player.sendMessage(
                        Component.text("<" + entry.getKey() + "> = ").append(entry.getValue()))
                );
    }

    /**
     * Debug all the placeholders a totem has
     *
     * @param player The sender running the command
     */
    @Command("fishing|fish debug placeholders augment")
    @Permission("fishing.debug")
    @CommandDescription("Get the placeholders for an augment in your hand")
    public void debugAugment(Player player) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || stack.getType() == Material.AIR) {
            player.sendMessage("no item in hand :/");
        }

        // TODO: Get augment from itemstack
    }

}
