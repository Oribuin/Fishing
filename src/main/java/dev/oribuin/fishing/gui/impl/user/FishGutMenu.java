package dev.oribuin.fishing.gui.impl.user;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.impl.FishGutEvent;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.item.ConstructComponent;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.gui.GuiConfig;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.economy.CurrencyRegistry;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.GuttedFish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.bukkit.event.inventory.InventoryCloseEvent.Reason.PLUGIN;

@SuppressWarnings("UnstableApiUsage")
public class FishGutMenu extends PluginMenu<Gui, FishGutMenu.Config> {

    /**
     * Creates a new menu for the plugin to use
     *
     * @param plugin The plugin instance
     */
    public FishGutMenu(FishingPlugin plugin, Player player) {
        super(plugin, FishGutMenu.Config.class);
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

        this.config.getGutFish().place(this.gui, event -> {
            CANCELLED.execute(event);

            Inventory inventory = this.gui.getInventory();

            List<GuttedFish> target = new ArrayList<>();
            for (ItemStack stack : inventory.getStorageContents()) {
                if (stack == null || stack.getType().isAir()) continue;

                Fish fish = this.plugin.getTierManager().getFish(stack);
                if (fish == null) continue;

                Tier tier = fish.getTierInstance();
                if (tier.getGutEntropy() <= 0) continue;

                target.add(new GuttedFish(
                        fish,
                        tier,
                        stack.getAmount(),
                        stack
                ));
            }

            if (target.isEmpty()) {
                PluginMessages.get().getNoGuttedFish().send(player);
                event.getWhoClicked().closeInventory(PLUGIN);
                return;
            }

            Map<Augment, Integer> augments = plugin.getAugmentManager().getAugments(strongest);

            FishGutEvent gutEvent = new FishGutEvent(
                    (Player) event.getWhoClicked(),
                    augments,
                    target
            );
            
            gutEvent.callEvent();
            augments.keySet().forEach(x -> x.handleEvent(gutEvent));
            if (gutEvent.isCancelled()) {
                event.getWhoClicked().closeInventory(PLUGIN);
                return;
            }
            
            int entropy = gutEvent.getEntropy();
            int totalFish = target.stream().mapToInt(GuttedFish::amount).sum();
            
            // make sure that shit is GONE
            target.forEach(fish -> fish.stack().setAmount(0));
            config.getGuttingSlots().forEach(integer -> gui.getInventory().setItem(0, null));
            event.getWhoClicked().closeInventory(PLUGIN);

            PluginMessages.get().getGuttedFish().send(player, "total", totalFish, "entropy", entropy);
            CurrencyRegistry.ENTROPY.give(player, entropy);
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
                        if (!this.config.getGuttingSlots().contains(event.getSlot())) {
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
                        for (int slot : this.config.getGuttingSlots()) {
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
        private List<Integer> guttingSlots = FishUtils.parseList("9-35");

        private MenuItem gutFish = ItemConstruct.of(Material.PAPER)
                .setName("<white>[<#94bc80><bold>Gut Fish</bold><white>]")
                .setLore(
                        "<gray>Gut all the fish that you have",
                        "<gray>placed inside the menu for entropy"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .setProperty(ConstructType.MODEL, x -> x.setValue("minecraft:netherite_sword"))
                .asMenuItem(40);

        private MenuItem mainMenu = ItemConstruct.of(Material.ARROW)
                .setName("<white>[<#94bc80><bold>Main Menu</bold><white>]")
                .setLore("<gray>Click to go back to the main menu")
                .asMenuItem(36);


        public Config() {
            this.title = "Fishing | Gutting Station";
            this.rows = 5;
            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList("0-8", "36-44")));
        }

        public int getRodSlot() {
            return rodSlot;
        }

        public List<Integer> getGuttingSlots() {
            return guttingSlots;
        }

        public MenuItem getGutFish() {
            return gutFish;
        }

        public MenuItem getMainMenu() {
            return mainMenu;
        }
    }

}
