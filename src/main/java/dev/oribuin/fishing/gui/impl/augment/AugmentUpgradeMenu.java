package dev.oribuin.fishing.gui.impl.augment;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.item.ConstructComponent;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.gui.GuiConfig;
import dev.oribuin.fishing.gui.GuiTickable;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.gui.impl.codex.impl.AugmentCodexMenu;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.economy.Cost;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.model.fisher.Fisher;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.time.Duration;
import java.util.function.Supplier;

import static dev.oribuin.fishing.config.item.ConstructType.TOOLTIP;
import static dev.oribuin.fishing.storage.util.KeyRegistry.AUGMENT_TYPE;

public class AugmentUpgradeMenu extends PluginMenu<Gui, AugmentUpgradeMenu.Config> implements GuiTickable {

    /**
     * Creates a new menu for the plugin to use
     */
    public AugmentUpgradeMenu(FishingPlugin plugin, Player player) {
        super(plugin, AugmentUpgradeMenu.Config.class);
        this.gui = this.createMenu().get();

        Fisher fisher = plugin.getDataManager().get(player.getUniqueId());
        Placeholders placeholders = Placeholders.builder()
                .add("player", player.getName())
                .addAll(fisher.getPlaceholders())
                .build();

        this.setDummyIcons(placeholders);

        this.config.getAugmentInfo().place(this.gui, placeholders, event -> {
            Player who = (Player) event.getWhoClicked();
            new AugmentCodexMenu(this.plugin).open(who); // TODO: ?
        });

        this.tick();
    }

    public void update(@NotNull Augment augment) {
        Placeholders placeholders = Placeholders.builder()
                .add("next", Math.min(augment.getLevel() + 1, augment.getMaxLevel()))
                .add("current", augment.getLevel())
                //                .add("previous", augment.getLevel() - Math.min(0, this.increase))
                .add("cost", augment.getUpgradeCost(1).getFormatted())
                .addAll(augment.getPlaceholders())
                .build();

        this.config.getUpgradeLevel().update(this.gui, placeholders, event -> {
            Player who = (Player) event.getWhoClicked();

            // Check whether the augment is already at max level
            if (augment.getLevel() >= augment.getMaxLevel() || augment.getLevel() + 1 > augment.getMaxLevel()) {
                PluginMessages.get().getHitAugmentMaxLevel().send(who);
                return;
            }

            // TODO: Add back the canUse functionality
            //            if (augment.canUse(who)) {
            //                who.sendMessage("Can the player even use the augment to upgrade it?");
            //                return;
            //            }

            // entropy :) maybe make this better
            Cost<Integer> cost = augment.getUpgradeCost(1);
            if (!cost.has(who)) {
                PluginMessages.get().getCannotAfford().send(who, placeholders);
                return;
            }

            cost.take(who);
            // TODO: Add back the canUse functionality
            //            if (augment.canUse(who)) {
            //                who.sendMessage("Can the player even use the augment to upgrade it?");
            //                return;
            //            }

            // TODO: Do a cost check; can the player afford to do it
            PluginMessages.get().getUpgradedAugment().send(who, placeholders);
            augment.setLevel(Math.min(augment.getLevel() + 1, augment.getMaxLevel()));
            this.gui.getInventory().setItem(this.config.getAugmentSlot(), augment.getItemWithLevel());
        });
    }

    /**
     * Creates a tickable task for a {@link PluginMenu}
     */
    @Override
    public void tick() {
        ItemStack stackAugment = this.gui.getInventory().getItem(this.config.getAugmentSlot());

        Augment augment = this.plugin.getAugmentManager().getAugmentStack(stackAugment);
        if (augment == null || augment.getLevel() >= augment.getMaxLevel()) {
            this.config.getMissingPieces().update(this.gui, Placeholders.empty());
            return;
        }

        this.update(augment);
    }

    /**
     * Get the duration delay between each gui refresh
     *
     * @return The gui to refresh
     */
    @Override
    public Duration getTickDelay() {
        return Duration.ofMillis(250);
    }

    /**
     * Check whether an itemstack is a fishing augment
     *
     * @param stack The stack to check against
     *
     * @return Whether the stack is an augment or not
     */
    private boolean isAugment(@Nullable ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return false;

        return stack.getPersistentDataContainer().has(AUGMENT_TYPE);
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
                        if (event.getSlot() != this.config.getAugmentSlot()) {
                            CANCELLED.execute(event);
                        }
                    });
                    // endregion

                    // region Only let players click on fish in their inventory
                    x.setPlayerInventoryAction(event -> {
                        ItemStack stack = event.getCurrentItem();
                        if (stack == null || stack.getType().isAir()) stack = event.getCursor();
                        if (stack.getType().isAir() || stack.getAmount() != 1) {
                            CANCELLED.execute(event);
                            return;
                        }

                        if (!this.isAugment(stack)) CANCELLED.execute(event);
                    });
                    // endregion 

                    // region Give any non fish items back to the player
                    x.setCloseGuiAction(event -> {
                        Player who = (Player) event.getPlayer();
                        Inventory inventory = event.getInventory();
                        // give augment and rod back

                        ItemStack stackAugment = inventory.getItem(this.config.getAugmentSlot());

                        if (stackAugment != null && stackAugment.getType() != Material.AIR) {
                            this.giveOrDrop(stackAugment.clone(), who);
                        }

                        inventory.clear(this.config.getAugmentSlot());
                    });
                    // endregion
                })
                .create();
    }

    /**
     * Give the player the rod/augment or drop it if their inventory is full
     *
     * @param stack  The stack to give/drop
     * @param player The player to give it to
     */
    private void giveOrDrop(ItemStack stack, Player player) {
        if (stack == null || stack.getType().isAir()) return;

        if (player.getInventory().firstEmpty() == -1) {
            // folia moment
            PluginScheduler.get().runTaskAtLocation(player.getLocation(), () -> player.getLocation().getWorld().dropItem(
                    player.getLocation(),
                    stack,
                    item -> item.setOwner(player.getUniqueId())
            ));
            return;
        }

        player.getInventory().addItem(stack);
    }

    @ConfigSerializable
    @SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
    public static class Config extends GuiConfig {

        private int augmentSlot = 22;

        private MenuItem augmentInfo = ItemConstruct.of(Material.KNOWLEDGE_BOOK)
                .setName("<white>[<#94bc80><bold>What are Augments?</bold><white>]") // TODO: Augment Upgrading
                .setLore(
                        "<gray>Augments are crafted modifications",
                        "<gray>for your fishing rod, granting them",
                        "<gray>unique effects when catching fish",
                        "",
                        " <#93bc80>Click to see all fishing augments"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .asMenuItem(4);

        private MenuItem upgradeLevel = ItemConstruct.of(Material.PLAYER_HEAD) // TODO: Change
                .setName("<white>[<#94bc80><bold>Upgrade Augment</bold><white>]")
                .setLore(
                        "<gray>Imagine things are listed here like cost",
                        "<gray>Required level blah blah",
                        "",
                        "<white>Level: <#93bc80><current>",
                        "<white>Next Level: <#93bc80><next>",
                        "<white>Cost: <#93bc80><cost>",
                        "",
                        "<#93bc80>Click to upgrade the augment"
                )
                .setProperty(ConstructType.TEXTURE, x -> x.setValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0="))
                .asMenuItem(31);

        private MenuItem missingPieces = ItemConstruct.of(Material.RED_CONCRETE) // TODO: Change
                .setName("<white>[<#94bc80><bold>Can't Upgrade</bold><white>]")
                .setLore(
                        "<gray>Make sure that you have put a fishing rod",
                        "<gray>and a fishing augment in the menu"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .asMenuItem(31);

        public Config() {
            this.title = "Fishing | Upgrade Augment";
            this.rows = 5;

            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList(
                    "1-7",
                    "10-16",
                    "19-21", "23-25",
                    "28-34",
                    "37-43"
            )));

            this.dummyItems.add(new MenuItem(ItemConstruct.of(Material.GREEN_STAINED_GLASS_PANE)
                    .setProperty(TOOLTIP, x -> x.setVisible(false)),
                    0, 8, 9, 17, 18, 26, 27, 35, 36, 44
            ));

            //            this.dummyItems.add(new MenuItem(
            //                    ItemConstruct.of(Material.PLAYER_HEAD)
            //                            .setProperty(TOOLTIP, HIDDEN)
            //                            .setProperty(TEXTURE, x -> x.setValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYzMzlmZjJlNTM0MmJhMThiZGM0OGE5OWNjYTY1ZDEyM2NlNzgxZDg3ODI3MmY5ZDk2NGVhZDNiOGFkMzcwIn19fQ==")),
            //                    10
            //            ));
            //
            //            this.dummyItems.add(new MenuItem(
            //                    ItemConstruct.of(Material.PLAYER_HEAD)
            //                            .setProperty(TOOLTIP, HIDDEN)
            //                            .setProperty(TEXTURE, x -> x.setValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODU1MGI3Zjc0ZTllZDc2MzNhYTI3NGVhMzBjYzNkMmU4N2FiYjM2ZDRkMWY0Y2E2MDhjZDQ0NTkwY2NlMGIifX19")),
            //                    12
            //            ));
        }

        public int getAugmentSlot() {
            return augmentSlot;
        }

        public MenuItem getAugmentInfo() {
            return augmentInfo;
        }

        public MenuItem getMissingPieces() {
            return missingPieces;
        }

        public MenuItem getUpgradeLevel() {
            return upgradeLevel;
        }

        //        public MenuItem getIncreaseLevel() {
        //            return increaseLevel;
        //        }
        //
        //        public MenuItem getDecreaseLevel() {
        //            return decreaseLevel;
        //        }
    }
}