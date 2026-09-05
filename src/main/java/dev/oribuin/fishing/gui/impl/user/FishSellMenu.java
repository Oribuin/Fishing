package dev.oribuin.fishing.gui.impl.user;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.impl.FishSellEvent;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.impl.Settings;
import dev.oribuin.fishing.config.item.ConstructComponent;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.gui.GuiConfig;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.manager.TierManager;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.economy.CurrencyRegistry;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.ProcessedFish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static dev.oribuin.fishing.storage.util.KeyRegistry.STAT_ROD_SOLD;
import static org.bukkit.event.inventory.InventoryCloseEvent.Reason.PLUGIN;

public class FishSellMenu extends PluginMenu<Gui, FishSellMenu.Config> {

    /**
     * Creates a new menu for the plugin to use
     *
     * @param plugin The plugin instance
     */
    public FishSellMenu(FishingPlugin plugin, Player player) {
        super(plugin, FishSellMenu.Config.class);
        this.gui = this.createMenu().get();

        Fisher fisher = plugin.getDataManager().get(player.getUniqueId());
        Placeholders placeholders = Placeholders.builder()
                .add("player", player.getName())
                .addAll(fisher.getPlaceholders())
                .build();

        this.setDummyIcons(placeholders);

        // Add the strongest rod in the inventory
        ItemStack strongest = plugin.getAugmentManager().getStrongestRod(player.getInventory());
        if (this.config.getRodSlot() != -1 && strongest != null) {
            this.gui.setItem(this.config.getRodSlot(), new GuiItem(strongest, CANCELLED));
        }

        // region Place the gui items into the menu 
        this.config.getMainMenu().place(this.gui, placeholders, event -> {
            FishMainMenu mainMenu = new FishMainMenu(plugin, (Player) event.getWhoClicked());
            mainMenu.open(player);
            CANCELLED.execute(event);
        });

        this.config.getSellFish().place(this.gui, event -> {
            CANCELLED.execute(event);

            Inventory inventory = this.gui.getInventory();

            List<ProcessedFish> target = new ArrayList<>();
            for (int slot : this.config.getSellSlots()) {
                ItemStack stack = inventory.getItem(slot);
                if (stack == null || stack.getType().isAir()) continue;

                Fish fish = this.plugin.getTierManager().getFish(stack);
                if (fish == null) continue;

                Tier tier = fish.getTierInstance();
                if (tier.getSellMoney() <= 0) continue;

                target.add(new ProcessedFish(
                        fish,
                        tier,
                        stack.getAmount(),
                        stack
                ));
            }

            if (target.isEmpty()) {
                PluginMessages.get().getNoSoldFish().send(player);
                event.getWhoClicked().closeInventory(PLUGIN);
                return;
            }

            Map<String, Augment> augments = plugin.getAugmentManager().getAugments(strongest);

            FishSellEvent sellEvent = new FishSellEvent(
                    (Player) event.getWhoClicked(),
                    strongest,
                    augments,
                    target
            );

            sellEvent.callEvent();
            augments.values().forEach(x -> x.handleEvent(sellEvent));
            if (sellEvent.isCancelled()) {
                event.getWhoClicked().closeInventory(PLUGIN);
                return;
            }

            double money = sellEvent.getMoney();
            int totalFish = target.stream().mapToInt(ProcessedFish::amount).sum();
            event.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            if (money <= 0 || totalFish <= 0) {
                PluginMessages.get().getNoSoldFish().send(player);
                return;
            }

            // make sure that shit is GONE
            TierManager tierManager = this.plugin.getTierManager();
            target.forEach(fish -> fish.stack().setAmount(0));
            config.getSellSlots().forEach(integer -> {
                ItemStack item = gui.getInventory().getItem(integer);
                if (tierManager.isFish(item)) gui.getInventory().setItem(0, null);
            });

            event.getWhoClicked().closeInventory(PLUGIN);


            // Increase fishing rod statistics
            if (Settings.get().isRodStatistics() && strongest != null) {
                this.plugin.getRodManager().incrementStatistic(
                        strongest,
                        STAT_ROD_SOLD,
                        totalFish
                );
            }
            PluginMessages.get().getSoldFish().send(player, "total", totalFish, "money", money);
            CurrencyRegistry.VAULT.give(player, money);
        });
        // endregion
    }

    /**
     * Creates the menu for the plugin
     *
     * @return the resulting menu
     */
    @Override
    public Supplier<Gui> createMenu() {
        return () -> Gui.gui()
                .title(Component.text(this.config.getTitle()))
                .rows(this.config.getRows())
                .apply(x -> {

                    // region Stop the user from clicking non sell slots
                    x.setDefaultTopClickAction(event -> {
                        if (!this.config.getSellSlots().contains(event.getSlot())) {
                            CANCELLED.execute(event);
                        }
                    });
                    // endregion

                    // region Only let players click on fish in their inventory
                    x.setPlayerInventoryAction(event -> {
                        ItemStack stack = event.getCurrentItem();
                        if (stack == null || stack.getType().isAir()) {
                            CANCELLED.execute(event);
                            return;
                        }

                        Fish fish = this.plugin.getTierManager().getFish(stack);
                        if (fish == null) {
                            CANCELLED.execute(event);
                            return;
                        }

                        Tier tier = fish.getTierInstance();
                        if (tier.getSellMoney() <= 0) CANCELLED.execute(event);
                    });
                    // endregion 

                    // region Give any non fish items back to the player
                    x.setCloseGuiAction(event -> {
                        Inventory inventory = event.getInventory();
                        for (int slot : this.config.getSellSlots()) {
                            ItemStack stack = inventory.getItem(slot);
                            if (stack == null || stack.getType().isAir()) continue;

                            PlayerInventory playerInventory = event.getPlayer().getInventory();
                            if (playerInventory.firstEmpty() == -1) {
                                event.getPlayer().getWorld().dropItem(
                                        event.getPlayer().getLocation(),
                                        stack,
                                        item -> {
                                            item.setCanMobPickup(false);
                                            item.setOwner(event.getPlayer().getUniqueId());
                                        }
                                );
                                continue;
                            }

                            playerInventory.addItem(stack);
                        }
                    });
                    // endregion
                })
                .create();
    }

    @ConfigSerializable
    @SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
    public static class Config extends GuiConfig {

        private int rodSlot = 44;
        private List<Integer> sellSlots = FishUtils.parseList("9-35");

        private MenuItem sellFish = ItemConstruct.of(Material.EMERALD)
                .setName("<white>[<#94bc80><bold>Sell Fish</bold><white>]")
                .setLore(
                        "<gray>Sell all the fish that you have",
                        "<gray>placed inside the menu for money"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .asMenuItem(40);

        private MenuItem mainMenu = ItemConstruct.of(Material.ARROW)
                .setName("<white>[<#94bc80><bold>Main Menu</bold><white>]")
                .setLore("<gray>Click to go back to the main menu")
                .asMenuItem(36);


        public Config() {
            this.title = "Fishing | Selling Station";
            this.rows = 5;
            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList("0-8", "36-44")));
        }

        public int getRodSlot() {
            return rodSlot;
        }

        public List<Integer> getSellSlots() {
            return sellSlots;
        }

        public MenuItem getSellFish() {
            return sellFish;
        }

        public MenuItem getMainMenu() {
            return mainMenu;
        }
    }

}
