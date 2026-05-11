package dev.oribuin.fishing.gui.impl.user;

import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.manager.MenuManager;
import dev.oribuin.fishing.model.economy.CurrencyRegistry;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
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

@ConfigSerializable
public class FishSellMenu extends PluginMenu<Gui> {

    private final List<Integer> sellSlots;

    public FishSellMenu() {
        super("fish_sell_menu");

        this.title = "Selling Station";
        this.rows = 5;
        this.items.put("sell-fish", new MenuItem(SELL_FISH, 40));
        this.items.put("main-menu", new MenuItem(MAIN_MENU, 36));
        this.extraItems.put("border", new MenuItem(BORDER, FishUtils.parseList("0-8", "36-44")));
        this.sellSlots = FishUtils.parseList("9-35");
    }

    /**
     * Open the menu for the player synchronously and mark the menu as being viewed
     *
     * @param player The player opening the menu
     */
    @Override
    public void open(Player player) {
        Fisher fisher = this.plugin.getDataManager().get(player.getUniqueId());
        if (fisher == null) return;

        this.gui = this.createMenu().get();
        Placeholders placeholders = Placeholders.builder()
                .add("player", player.getName())
                .addAll(fisher.getPlaceholders())
                .build();

        this.extraItems.forEach((key, value) -> value.place(this.gui, placeholders, CANCELLED));
        this.placeItem("main-menu", placeholders, event -> {
            CANCELLED.execute(event);
            MenuManager.get(FishMainMenu.class).open((Player) event.getWhoClicked());
        });

        this.placeItem("sell-fish", placeholders, event -> {
            CANCELLED.execute(event);

            Inventory inventory = this.gui.getInventory();

            double money = 0;
            int totalFish = 0;
            for (ItemStack stack : inventory.getStorageContents()) {
                if (stack == null || stack.getType().isAir()) continue;

                Fish fish = this.plugin.getTierManager().getFish(stack);
                if (fish == null) continue;

                Tier tier = fish.getTierInstance();
                if (tier.getSellMoney() <= 0) continue;

                money += (tier.getSellMoney() * stack.getAmount());
                totalFish += stack.getAmount();
                stack.setAmount(0);
            }

            event.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            if (money <= 0 || totalFish <= 0) {
                PluginMessages.get().getNoSoldFish().send(player);
                return;
            }

            PluginMessages.get().getSoldFish().send(player, "total", totalFish, "money", money);
            CurrencyRegistry.VAULT.give(player, money);
        });

        super.open(player);
    }

    /**
     * Creates the menu for the plugin
     *
     * @return the resulting menu
     */
    @Override
    public Supplier<Gui> createMenu() {
        return () -> Gui.gui()
                .title(Component.text(this.title))
                .rows(this.rows)
                .apply(x -> {

                    // region Stop the user from clicking non sell slots
                    x.setDefaultTopClickAction(event -> {
                        if (!this.sellSlots.contains(event.getSlot())) {
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
                        for (int slot : this.sellSlots) {
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

    // region Items
    private static final ItemConstruct BORDER = new ItemConstruct(Material.BLACK_STAINED_GLASS_PANE)
            .setTooltip(false);

    private static final ItemConstruct SELL_FISH = new ItemConstruct(Material.EMERALD)
            .setName("<white>[<#94bc80>Sell Fish<white>]")
            .setLore(
                    "<gray>Sell all the fish that you have",
                    "<gray>placed inside the menu for money"
            )
            .setGlowing(true);

    private static final ItemConstruct MAIN_MENU = new ItemConstruct(Material.ARROW)
            .setName("<white>[<#94bc80>Main Menu<white>]")
            .setLore("<gray>Click to go back to the main menu");

    // endregion
}
