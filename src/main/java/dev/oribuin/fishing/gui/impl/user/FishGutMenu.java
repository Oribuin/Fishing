package dev.oribuin.fishing.gui.impl.user;

import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.manager.MenuManager;
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
public class FishGutMenu extends PluginMenu<Gui> {

    private final List<Integer> gutSlots;

    public FishGutMenu() {
        super("fish_gut_menu");

        this.title = "Gutting Station";
        this.rows = 5;
        this.items.put("gut-fish", new MenuItem(GUT_FISH, 40));
        this.items.put("main-menu", new MenuItem(MAIN_MENU, 36));
        this.extraItems.put("border", new MenuItem(BORDER, FishUtils.parseList("0-8", "36-44")));
        this.gutSlots = FishUtils.parseList("9-35");
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

        this.placeItem("gut-fish", placeholders, event -> {
            CANCELLED.execute(event);

            Inventory inventory = this.gui.getInventory();

            int entropy = 0;
            int totalFish = 0;
            for (ItemStack stack : inventory.getStorageContents()) {
                if (stack == null || stack.getType().isAir()) continue;

                Fish fish = this.plugin.getTierManager().getFish(stack);
                if (fish == null) continue;

                Tier tier = fish.getTierInstance();
                if (tier.getGutEntropy() <= 0) continue;

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
            fisher.setEntropy(fisher.getEntropy() + entropy);
            this.plugin.getDataManager().saveUser(fisher);
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

                    // region Stop the user from clicking non gut slots
                    x.setDefaultTopClickAction(event -> {
                        if (!this.gutSlots.contains(event.getSlot())) {
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
                        if (tier.getGutEntropy() <= 0) CANCELLED.execute(event);
                    });
                    // endregion 

                    // region Give any non fish items back to the player
                    x.setCloseGuiAction(event -> {
                        Inventory inventory = event.getInventory();
                        for (int slot : this.gutSlots) {
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

    private static final ItemConstruct GUT_FISH = new ItemConstruct(Material.NETHERITE_SWORD)
            .setName("<white>[<#94bc80>Gut Fish<white>]")
            .setLore(
                    "<gray>Gut all the fish that you have",
                    "<gray>placed inside the menu for entropy"
            )
            .setGlowing(true);

    private static final ItemConstruct MAIN_MENU = new ItemConstruct(Material.ARROW)
            .setName("<white>[<#94bc80>Main Menu<white>]")
            .setLore("<gray>Click to go back to the main menu");

    // endregion
}
