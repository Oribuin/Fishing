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

@SuppressWarnings("UnstableApiUsage")
public class FishMainMenu extends PluginMenu<Gui, FishMainMenu.Config> {

    /**
     * Creates a new menu for the plugin to use
     */
    public FishMainMenu(FishingPlugin plugin, Player player) {
        super(plugin, Config.class);
        this.gui = this.createMenu().get();

        Fisher fisher = plugin.getDataManager().get(player.getUniqueId());
        Placeholders placeholders = Placeholders.builder()
                .add("player", player.getName())
                .addAll(fisher.getPlaceholders())
                .build();

        this.setDummyIcons(placeholders);

        // region Place the gui items into the menu 
        this.config.getUserStats().place(this.gui, placeholders); // TODO: Open stats menu
        this.config.getGuttingStation().place(this.gui, placeholders, event -> {
            Player clicked = (Player) event.getWhoClicked();
            FishGutMenu sellMenu = new FishGutMenu(plugin, clicked);
            sellMenu.open(clicked);
        });

        this.config.getSellingStation().place(this.gui, placeholders, event -> {
            Player clicked = (Player) event.getWhoClicked();
            FishSellMenu sellMenu = new FishSellMenu(plugin, clicked);
            sellMenu.open(clicked);
        });

        this.config.getCodexMenu().place(this.gui, placeholders, event -> {
            // TODO: Codex menu;
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
                .disableAllInteractions()
                .create();
    }

    @ConfigSerializable
    @SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
    public static class Config extends GuiConfig {

        private final MenuItem userStats = ItemConstruct.of(Material.PLAYER_HEAD)
                .setName("<white>[<#94bc80><bold>Your Stats</bold><white>]")
                .setLore(
                        "<gray>Click here to view your current",
                        "<gray>fishing statistics",
                        "",
                        "<#94bc80>Information:",
                        " <#94bc80>- <white>Entropy: <#94bc80><entropy>",
                        " <#94bc80>- <white>Level: <#94bc80><level>",
                        " <#94bc80>- <white>Experience: <#94bc80><experience><gray>/<#94bc80><required_exp>",
                        " <#94bc80>- <white>Skill Points: <#94bc80><skill_points>"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .setProperty(ConstructType.TEXTURE, x -> x.setValue("player-<player>"))
                .asMenuItem(4);

        private MenuItem guttingStation = ItemConstruct.of(Material.PAPER)
                .setName("<white>[<#94bc80><bold>Gut Fish</bold><white>]")
                .setLore(
                        "<gray>Gut all the fish that you have",
                        "<gray>placed inside the menu for entropy"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .setProperty(ConstructType.MODEL, x -> x.setValue("minecraft:netherite_sword"))
                .asMenuItem(19);

        private MenuItem sellingStation = ItemConstruct.of(Material.EMERALD)
                .setName("<white>[<#94bc80><bold>Selling Station</bold><white>]")
                .setLore(
                        "<gray>Sell your fish to exchange them",
                        "<gray>for money"
                )
                .asMenuItem(20);

        private MenuItem codexMenu = ItemConstruct.of(Material.KNOWLEDGE_BOOK)
                .setName("<white>[<#94bc80><bold>The Codex</bold><white>]")
                .setLore(
                        "<gray>Click to view information about",
                        "<gray>varies things within the plugin"
                )
                .asMenuItem(21);


        public Config() {
            this.title = "Fishing | Main Menu";
            this.rows = 5;
            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList("0-8", "36-44")));
        }

        public MenuItem getUserStats() {
            return userStats;
        }

        public MenuItem getGuttingStation() {
            return guttingStation;
        }

        public MenuItem getSellingStation() {
            return sellingStation;
        }

        public MenuItem getCodexMenu() {
            return codexMenu;
        }
    }
}
