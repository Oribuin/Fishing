package dev.oribuin.fishing.gui.impl.totem;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.item.ConstructComponent;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.gui.GuiConfig;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Supplier;

@ConfigSerializable
public class TotemUpgradeMenu extends PluginMenu<PaginatedGui, TotemUpgradeMenu.Config> {

    private final Supplier<Totem> totemSupplier;

    public TotemUpgradeMenu(FishingPlugin plugin, Supplier<Totem> totemSupplier) {
        super(plugin, TotemUpgradeMenu.Config.class);
        this.gui = this.createMenu().get();
        this.totemSupplier = totemSupplier;

        Totem totem = this.totemSupplier.get();
        Fisher fisher = plugin.getDataManager().get(totem.getOwner());
        Placeholders placeholders = Placeholders.builder()
                .addAll(fisher.getPlaceholders())
                .addAll(totem.getPlaceholders())
                .build();

        this.setDummyIcons(placeholders);
        this.config.getPreviousPage().place(this.gui, placeholders, event -> gui.previous());
        this.config.getNextPage().place(this.gui, placeholders, event -> gui.next());
        this.config.getTotemMainMenu().place(this.gui, placeholders, event -> {
            TotemMainMenu mainMenu = new TotemMainMenu(plugin, totemSupplier);
            mainMenu.open((Player) event.getWhoClicked());
        });

        this.placeUpgrades();
    }

    /**
     * Place the dynamic items in the GUI for the totem
     *
     * @param totem  The totem to place the items for
     * @param player The player to place the items for
     */
    private void placeUpgrades() {
        gui.clearPageItems();
        Totem totem = this.totemSupplier.get();
        totem.getUpgrades().forEach((upgradeId, upgrade) -> {
            ItemStack item = UPGRADE_STYLE.create(upgrade.getPlaceholders(totem));
            if (item == null) return;
            // todo: make less ugly
            gui.addItem(new GuiItem(item, x -> {
                if (upgrade.increaseLevel((Player) x.getWhoClicked(), totem)) {
                    this.placeUpgrades();
                }
            }));
        });

        gui.update();
    }

    /**
     * Creates the menu for the plugin
     *
     * @return the resulting menu
     */
    @Override
    public Supplier<PaginatedGui> createMenu() {
        return () -> Gui.paginated()
                .title(Component.text(this.config.getTitle()))
                .rows(this.config.getRows())
                .disableAllInteractions()
                .create();
    }

    @ConfigSerializable
    @SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
    public static class Config extends GuiConfig {
        private MenuItem totemStats = ItemConstruct.of(Material.OAK_HANGING_SIGN)
                .setName("<white>[<#94bc80>Totem Details<white>]")
                .setLore(
                        "<gray>Here are the current upgrades",
                        "<gray>active for this fishing totem",
                        "",
                        "<#94bc80>Statistics:",
                        " <#94bc80>- <white>Active: <#94bc80><active>",
                        " <#94bc80>- <white>Owner: <#94bc80><owner>",
                        " <#94bc80>- <white>Radius: <#94bc80><upgrade_radius_total>",
                        " <#94bc80>- <white>Duration: <#94bc80><upgrade_duration_total>",
                        " <#94bc80>- <white>Cooldown: <#94bc80><upgrade_cooldown_total>"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .asMenuItem(4);

        private MenuItem previousPage = ItemConstruct.of(Material.ARROW)
                .setName("<white>[<#94bc80>Previous Page<white>]")
                .setLore("<gray>Click here to go to the previous page")
                .asMenuItem(3);

        private MenuItem totemMainMenu = ItemConstruct.of(Material.KNOWLEDGE_BOOK)
                .setName("<white>[<#94bc80>Totem Main Menu<white>]")
                .setLore("<gray>Click here to go to the totem menu")
                .asMenuItem(4);

        private MenuItem nextPage = ItemConstruct.of(Material.ARROW)
                .setName("<white>[<#94bc80>Next Page<white>]")
                .setLore("<gray>Click here to go to the next page")
                .asMenuItem(5);

        public Config() {
            this.title = "Fishing Totem | Upgrades";
            this.rows = 3;
            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList("0-8", "18-26", "9", "17")));
        }

        public MenuItem getTotemStats() {
            return totemStats;
        }

        public MenuItem getPreviousPage() {
            return previousPage;
        }

        public MenuItem getTotemMainMenu() {
            return totemMainMenu;
        }

        public MenuItem getNextPage() {
            return nextPage;
        }
    }


    // region Items

    private static final ItemConstruct PAGE_FORWARD = ItemConstruct.of(Material.ARROW)
            .setName("<white>[<#94bc80>Next Page<white>]")
            .setLore("<gray>Click here to go to the next page");

    private static final ItemConstruct PAGE_BACKWARD = ItemConstruct.of(Material.ARROW)
            .setName("<white>[<#94bc80>Previous Page<white>]")
            .setLore("<gray>Click here to go to the previous page");

    private static final ItemConstruct UPGRADE_STYLE = ItemConstruct.of(Material.HEART_OF_THE_SEA) // Upgrades will choose their own item, idgaf
            .setName("<white>[<#94bc80><bold><name><white>]")
            .setLore(
                    "<gray><description>",
                    "",
                    "<#94bc80>Information",
                    " <#94bc80>- <gray>Current: <white><level>",
                    " <#94bc80>- <gray>Max Level: <white><max_level>",
                    ""
            )
            .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
            .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled);
    // endregion


}
