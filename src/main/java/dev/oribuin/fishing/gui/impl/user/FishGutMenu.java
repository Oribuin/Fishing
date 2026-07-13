package dev.oribuin.fishing.gui.impl.user;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.config.item.component.TooltipConstructType;
import dev.oribuin.fishing.gui.GuiConfig;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.model.economy.CurrencyRegistry;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.function.Supplier;

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

        // region Place the gui items into the menu 
        this.config.getMainMenu().place(this.gui, placeholders, event -> {
            FishMainMenu mainMenu = new FishMainMenu(plugin, (Player) event.getWhoClicked());
            mainMenu.open(player);
            CANCELLED.execute(event);
        });

        this.config.getGutFish().place(this.gui, event -> {
            CANCELLED.execute(event);

            Inventory inventory = this.gui.getInventory();

            int entropy = 0;
            int totalFish = 0;
            for (ItemStack stack : inventory.getStorageContents()) {
                if (stack == null || stack.getType().isAir()) continue;

                Fish fish = this.plugin.getTierManager().getFish(stack);
                if (fish == null) continue;

                Tier tier = fish.getTierInstance();
                if (tier.getSellMoney() <= 0) continue;

                entropy += (tier.getGutEntropy() * stack.getAmount());
                totalFish += stack.getAmount();
                stack.setAmount(0);
            }

            event.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            if (entropy <= 0 || totalFish <= 0) {
                PluginMessages.get().getNoGuttedFish().send(player);
                return;
            }

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

        private List<Integer> sellSlots = FishUtils.parseList("9-35");

        private MenuItem gutFish = new ItemConstruct(Material.NETHERITE_SWORD)
                .setName("<white>[<#94bc80>Gut Fish<white>]")
                .setLore(
                        "<gray>Gut all the fish that you have",
                        "<gray>placed inside the menu for entropy"
                )
                .setGlowing(true)
                .setTooltip(TooltipConstructType.of(true, List.of(
                        DataComponentTypes.ATTRIBUTE_MODIFIERS
                )))
                .asMenuItem(40);

        private MenuItem mainMenu = new ItemConstruct(Material.ARROW)
                .setName("<white>[<#94bc80>Main Menu<white>]")
                .setLore("<gray>Click to go back to the main menu")
                .asMenuItem(36);


        public Config() {
            this.title = "Fishing | Gutting Station";
            this.rows = 5;
            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList("0-8", "36-44")));
        }

        public List<Integer> getSellSlots() {
            return sellSlots;
        }

        public MenuItem getGutFish() {
            return gutFish;
        }

        public MenuItem getMainMenu() {
            return mainMenu;
        }
    }

}
