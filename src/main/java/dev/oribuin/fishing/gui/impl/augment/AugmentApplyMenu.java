package dev.oribuin.fishing.gui.impl.augment;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.item.ConstructComponent;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.gui.GuiConfig;
import dev.oribuin.fishing.gui.GuiTickable;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.gui.impl.codex.impl.AugmentCodexMenu;
import dev.oribuin.fishing.manager.AugmentManager;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static dev.oribuin.fishing.config.item.ConstructType.TOOLTIP;
import static dev.oribuin.fishing.storage.util.KeyRegistry.AUGMENT_TYPE;

public class AugmentApplyMenu extends PluginMenu<Gui, AugmentApplyMenu.Config> implements GuiTickable {

    /**
     * Creates a new menu for the plugin to use
     */
    public AugmentApplyMenu(FishingPlugin plugin, Player player) {
        super(plugin, AugmentApplyMenu.Config.class);
        this.gui = this.createMenu().get();

        Fisher fisher = plugin.getDataManager().get(player.getUniqueId());
        Placeholders placeholders = Placeholders.builder()
                .add("player", player.getName())
                .addAll(fisher.getPlaceholders())
                .build();

        this.setDummyIcons(placeholders);

        this.config.getAugmentInfo().place(this.gui, placeholders, event -> {
            Player who = (Player) event.getWhoClicked();
            new AugmentCodexMenu(this.plugin).open(who);
        });

        this.tick();
        // Additional stuff is added as a tickable method
    }

    /**
     * Creates a tickable task for a {@link PluginMenu}
     */
    @Override
    public void tick() {
        // region Place the gui items into the menu

        ItemStack stackRod = this.gui.getInventory().getItem(this.config.getRodSlot());
        ItemStack stackAugment = this.gui.getInventory().getItem(this.config.getAugmentSlot());

        boolean canApply = this.isRod(stackRod) && this.isAugment(stackAugment);
        if (!canApply) {
            this.config.getMissingPieces().update(this.gui, Placeholders.empty());
            return;
        }

        // todo: consider putting this as a tickable to check

        this.config.getApplyAugment().update(this.gui, event -> {
            AugmentManager manager = plugin.getAugmentManager();
            Player who = (Player) event.getWhoClicked();

            ItemStack rodStack = event.getInventory().getItem(this.config.getRodSlot());
            if (rodStack == null || rodStack.getType() != Material.FISHING_ROD) return;

            ItemStack augmentStack = event.getInventory().getItem(this.config.getAugmentSlot());
            Augment augment = manager.getAugmentStack(augmentStack);
            if (augmentStack == null || augment == null) {
                who.sendMessage("need to place an augment");
                return;
            }

            if (augmentStack.getAmount() != 1) {
                who.sendMessage("You can only apply one augment at a time.");
                return;
            }

            if (!augment.canUse(who)) {
                who.sendMessage("player cannot use this augment");
                return;
            }

            int level = augment.getLevel();
            if (!augment.doesAccept(rodStack, level)) {
                who.sendMessage("level too high for this rod :/");
                return;
            }

            // TODO: Add augment upgrading then re-enable this
//            if (!this.plugin.getRodManager().canAccept(rodStack, augment)) {
//                who.sendMessage("your fishing rod does not have enough slots for this augment");
//                return;
//            }


            this.gui.getInventory().clear(this.config.getAugmentSlot());

            // Get the augment from the argument
            Map<Augment, Integer> augments = new HashMap<>(manager.getAugments(augmentStack));
            augments.put(augment, Math.min(level, augment.getMaxLevel()));
            manager.applyAugments(rodStack, augments);
            who.sendMessage("Successfully applied the augment to the fishing rod.");
        });
        // endregion
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
     * Check if an itemstack is fishing rod
     *
     * @param stack The stack to check
     *
     * @return Whether the stack is a rod or not
     */
    private boolean isRod(@Nullable ItemStack stack) {
        return stack != null && stack.getType() == Material.FISHING_ROD;
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

        return stack.getPersistentDataContainer().has(AUGMENT_TYPE.key(), AUGMENT_TYPE);
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
                        if (event.getSlot() != this.config.getRodSlot() && event.getSlot() != this.config.getAugmentSlot()) {
                            CANCELLED.execute(event);
                        }
                    });
                    // endregion

                    // region Only let players click on fish in their inventory
                    x.setPlayerInventoryAction(event -> {
                        ItemStack stack = event.getCurrentItem();
                        if (stack == null || stack.getType().isAir()) stack = event.getCursor();
                        if (stack.getType().isAir()) {
                            CANCELLED.execute(event);
                            return;
                        }

                        if (!this.isRod(stack) && !this.isAugment(stack)) {
                            CANCELLED.execute(event);

                        }
                    });
                    // endregion 

                    // region Give any non fish items back to the player
                    x.setCloseGuiAction(event -> {
                        Player who = (Player) event.getPlayer();
                        Inventory inventory = event.getInventory();
                        // give augment and rod back

                        ItemStack stackRod = inventory.getItem(this.config.getRodSlot());
                        ItemStack stackAugment = inventory.getItem(this.config.getAugmentSlot());

                        if (stackRod != null && stackRod.getType() != Material.AIR) this.giveOrDrop(stackRod.clone(), who);
                        if (stackAugment != null && stackAugment.getType() != Material.AIR) this.giveOrDrop(stackAugment.clone(), who);

                        inventory.clear(this.config.getRodSlot());
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

        private int rodSlot = 20;
        private int augmentSlot = 24;

        private MenuItem augmentInfo = ItemConstruct.of(Material.KNOWLEDGE_BOOK)
                .setName("<white>[<#94bc80><bold>What are Augments?</bold><white>]")
                .setLore(
                        "<gray>Augments are crafted modifications",
                        "<gray>for your fishing rod, granting them",
                        "<gray>unique effects when catching fish",
                        "",
                        " <#93bc80>Click to see all fishing augments"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .asMenuItem(4);

        private MenuItem applyAugment = ItemConstruct.of(Material.LIME_CONCRETE) // TODO: Change
                .setName("<white>[<#94bc80><bold>Apply Augments</bold><white>]")
                .setLore(
                        "<gray>Imagine things are listed here like cost",
                        "<gray>Required level blah blah",
                        "",
                        " <#93bc80>Click to apply the augment"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .asMenuItem(22);

        private MenuItem missingPieces = ItemConstruct.of(Material.RED_CONCRETE) // TODO: Change
                .setName("<white>[<#94bc80><bold>No Rod/Augment</bold><white>]")
                .setLore(
                        "<gray>Make sure that you have put a fishing rod",
                        "<gray>and a fishing augment in the menu"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .asMenuItem(22);

        //        private MenuItem displayArrow = ItemConstruct.of(Material.PLAYER_HEAD)
        //                .setProperty(ConstructType.TEXTURE, x -> x.setValue("base64-eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjUyN2ViYWU5ZjE1MzE1NGE3ZWQ0OWM4OGMwMmI1YTlhOWNhN2NiMTYxOGQ5OTE0YTNkOWRmOGNjYjNjODQifX19"))
        //                .setProperty(TOOLTIP, x -> x.setVisible(false))
        //                .asMenuItem(13);

        private MenuItem displayRod = ItemConstruct.of(Material.FISHING_ROD)
                .setName("<white>[<#94bc80><bold>Fishing Rod</bold><white>]")
                .setLore(
                        "<gray>Place the fishing rod that",
                        "<gray>you want to apply an augment to",
                        "<gray>in the empty space below"
                )
                .asMenuItem(11);

        private MenuItem displayBook = ItemConstruct.of(Material.BOOK)
                .setName("<white>[<#94bc80><bold>Augment</bold><white>]")
                .setLore(
                        "<gray>Place the augment that",
                        "<gray>you want to spend apply",
                        "<gray>in the empty space below"
                )
                .asMenuItem(15);

        public Config() {
            this.title = "Fishing | Apply Augments";
            this.rows = 5;
            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList(
                    "0-8",
                    "36-44"
            )));
            this.dummyItems.add(new MenuItem(ItemConstruct.of(Material.GRAY_STAINED_GLASS_PANE)
                    .setProperty(TOOLTIP, x -> x.setVisible(false)),
                    FishUtils.parseList("9-19", "21", "22", "23", "25-35")
            ));

            this.dummyItems.add(displayRod);
            this.dummyItems.add(displayBook);
            this.dummyItems.add(
                    ItemConstruct.of(Material.GREEN_STAINED_GLASS_PANE)
                            .setProperty(TOOLTIP, x -> x.setVisible(false))
                            .asMenuItem(29, 33)
            );
        }

        public int getRodSlot() {
            return rodSlot;
        }

        public int getAugmentSlot() {
            return augmentSlot;
        }

        public MenuItem getAugmentInfo() {
            return augmentInfo;
        }

        public MenuItem getApplyAugment() {
            return applyAugment;
        }

        public MenuItem getMissingPieces() {
            return missingPieces;
        }
    }
}