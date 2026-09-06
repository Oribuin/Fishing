package dev.oribuin.fishing.command.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.command.FishCommand;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.gui.BiPaginatedGui;
import dev.oribuin.fishing.gui.paired.PagePair;
import dev.oribuin.fishing.gui.paired.PairDirection;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DebugCommand implements FishCommand {

    private final Table<UUID, Material, Boolean> toggledMaterials = HashBasedTable.create();
    private final FishingPlugin plugin;

    private static final List<Material> AVAILABLE = Arrays.stream(Material.values())
            .filter(x -> x.isItem() && !x.isAir())
            .toList();

    public static class ToggledMaterial {
        private final Material material;
        private boolean status;

        public ToggledMaterial(Material material) {
            this.material = material;
            this.status = true;
        }

        public Material getMaterial() {
            return material;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }

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
                    .setProperty(ConstructType.TOOLTIP, x -> x.setVisible(false))
                    .create());

            gui.setItem(FishUtils.parseList("0-8", "27-35"), border);

            gui.setItem(0, new GuiItem(ItemConstruct.of(Material.BARRIER)
                    .setName("<red>Reset Toggles")
                    .create(), event -> {
                this.toggledMaterials.row(event.getWhoClicked().getUniqueId()).clear();
                event.getWhoClicked().sendMessage(FishUtils.kyorify("<red>Reset Toggle Status"));
                gui.close(event.getWhoClicked());
            }));

            UUID uuid = player.getUniqueId();
            AtomicBoolean previousStatus = new AtomicBoolean(false);
            AVAILABLE.forEach(material -> {
                Boolean status = this.toggledMaterials.get(uuid, material);
                if (status == null) status = !previousStatus.get();
                previousStatus.set(status);
                this.toggledMaterials.put(uuid, material, status);

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
            if (status == null) status = true;

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
