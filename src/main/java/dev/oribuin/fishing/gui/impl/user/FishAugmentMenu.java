package dev.oribuin.fishing.gui.impl.user;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.item.ConstructComponent;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.gui.GuiConfig;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Supplier;

public class FishAugmentMenu extends PluginMenu<Gui, FishAugmentMenu.Config> {

    /**
     * Creates a new menu for the plugin to use
     */
    public FishAugmentMenu(FishingPlugin plugin, Player player) {
        super(plugin, FishAugmentMenu.Config.class);
        this.gui = this.createMenu().get();

        Fisher fisher = plugin.getDataManager().get(player.getUniqueId());
        Placeholders placeholders = Placeholders.builder()
                .add("player", player.getName())
                .addAll(fisher.getPlaceholders())
                .build();

        this.setDummyIcons(placeholders);

        // region Place the gui items into the menu
        
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
                .disableAllInteractions()
                .create();
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

        private MenuItem displayArrow = ItemConstruct.of(Material.PLAYER_HEAD)
                .setProperty(ConstructType.TEXTURE, x -> x.setValue("base64-eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjUyN2ViYWU5ZjE1MzE1NGE3ZWQ0OWM4OGMwMmI1YTlhOWNhN2NiMTYxOGQ5OTE0YTNkOWRmOGNjYjNjODQifX19"))
                .setProperty(ConstructType.TOOLTIP, x -> x.setVisible(false))
                .asMenuItem(22);

        private MenuItem displayRod = ItemConstruct.of(Material.FISHING_ROD)
                .setName("<white>[<#94bc80><bold>Fishing Rod</bold><white>]")
                .setLore(
                        "<gray>Place the fishing rod that",
                        "<gray>you want to apply an augment to",
                        "<gray>in the empty space below"
                )
                .asMenuItem(12);

        private MenuItem displayBook = ItemConstruct.of(Material.BOOK)
                .setName("<white>[<#94bc80><bold>Augment </bold><white>]")
                .setLore(
                        "<gray>Place the augment that",
                        "<gray>you want to spend apply",
                        "<gray>in the empty space below"
                )
                .asMenuItem(15);

        public Config() {
            this.title = "Fishing | Apply Augments";
            this.rows = 5;
            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList("0-8", "36-44")));
            this.dummyItems.add(displayArrow);
            this.dummyItems.add(displayRod);
            this.dummyItems.add(displayBook);
            this.dummyItems.add(
                    ItemConstruct.of(Material.GREEN_STAINED_GLASS_PANE)
                            .setProperty(ConstructType.TOOLTIP, x -> x.setVisible(false))
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
    }
}