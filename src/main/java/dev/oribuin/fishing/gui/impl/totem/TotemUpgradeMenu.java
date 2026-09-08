package dev.oribuin.fishing.gui.impl.totem;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.TextMessage;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.impl.PluginMessages.TotemMessages;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.gui.GuiConfig;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.gui.type.bipaginated.BiPaginatedGui;
import dev.oribuin.fishing.gui.type.bipaginated.PagePair;
import dev.oribuin.fishing.gui.type.bipaginated.PairDirection;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.DefaultUpgrade;
import dev.oribuin.fishing.model.totem.upgrade.Toggleable;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.model.totem.upgrade.impl.TUpgradeCooldown;
import dev.oribuin.fishing.model.totem.upgrade.impl.TUpgradeDuration;
import dev.oribuin.fishing.model.totem.upgrade.impl.TUpgradeRadius;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.oribuin.fishing.config.item.ConstructType.TEXTURE;
import static dev.oribuin.fishing.config.item.ConstructType.TOOLTIP;
import static dev.oribuin.fishing.config.item.component.TooltipItemType.HIDDEN;

@ConfigSerializable
public class TotemUpgradeMenu extends PluginMenu<BiPaginatedGui, TotemUpgradeMenu.Config> {

    private final BiConsumer<TotemUpgrade, InventoryClickEvent> upgradeAction;

    private final Supplier<Totem> totemSupplier;

    public TotemUpgradeMenu(FishingPlugin plugin, Supplier<Totem> totemSupplier) {
        super(plugin, TotemUpgradeMenu.Config.class);
        this.gui = this.createMenu().get();
        this.totemSupplier = totemSupplier;
        this.upgradeAction = (upgrade, event) -> {
            Player player = (Player) event.getWhoClicked();
            Totem totem = this.totemSupplier.get();
            upgrade.increaseLevel(player, totem);
        };

        Totem totem = this.totemSupplier.get();
        Fisher fisher = plugin.getDataManager().get(totem.getOwner());
        Placeholders placeholders = Placeholders.builder()
                .addAll(fisher.getPlaceholders())
                .addAll(totem.getPlaceholders())
                .build();

        this.setDummyIcons(placeholders);
        this.config.getPreviousPage().place(this.gui, placeholders, event -> gui.previous());
        this.config.getNextPage().place(this.gui, placeholders, event -> gui.next());
        this.placeUpgrades();
    }

    public void placeUpgrades() {
        Totem totem = this.totemSupplier.get();

        // region Add the default upgrades to the totem
        TUpgradeCooldown upgradeCooldown = totem.getUpgrade(TUpgradeCooldown.class);
        if (upgradeCooldown != null) {
            this.config.getCooldownUpgrade().place(this.gui, upgradeCooldown.getPlaceholders(totem), event -> {
                this.upgradeAction.accept(upgradeCooldown, event);
                this.placeUpgrades();
            });
        }
        TUpgradeDuration upgradeDuration = totem.getUpgrade(TUpgradeDuration.class);
        if (upgradeDuration != null) {
            this.config.getDurationUpgrade().place(this.gui, upgradeDuration.getPlaceholders(totem), event -> {
                this.upgradeAction.accept(upgradeDuration, event);
                this.placeUpgrades();
            });
        }

        TUpgradeRadius upgradeRadius = totem.getUpgrade(TUpgradeRadius.class);
        if (upgradeRadius != null) {
            this.config.getRadiusUpgrade().place(this.gui, upgradeRadius.getPlaceholders(totem), event -> {
                this.upgradeAction.accept(upgradeRadius, event);
                this.placeUpgrades();
            });
        }
        // endregion

        // region Add the other upgrades as paginated
        this.gui.clearPageItems();
        Comparator<TotemUpgrade> upgradeComparator = Comparator.comparing(TotemUpgrade::getLevel)
                .thenComparing(TotemUpgrade::getName);

        totem.getUpgrades().values()
                .stream()
                .filter(x -> x.isEnabled() && !(x instanceof DefaultUpgrade)) // Is enabled & not cooldown, radius, duration
                .filter(TotemUpgrade::isEnabled)
                .sorted(upgradeComparator)
                .forEach(upgrade -> this.gui.addItem(this.getUpgradePair(upgrade)));
        // endregion

        this.gui.update();
    }

    private PagePair getUpgradePair(@NotNull TotemUpgrade upgrade) {
        Consumer<InventoryClickEvent> toggleAction = event -> {
            Totem totem = this.totemSupplier.get();
            if (totem == null || !(upgrade instanceof Toggleable toggleable)) return;

            // get the totem stand
            ArmorStand display = totem.getDisplay();
            if (display == null) return;

            // Get the pair target slot
            Integer targetSlot = gui.getPrimarySlot(event.getSlot());
            if (targetSlot == null) return;

            toggleable.setActivated(!toggleable.isActivated());

            TotemMessages messages = PluginMessages.get().getTotem();
            TextMessage message = toggleable.isActivated() ? messages.getUpgradeToggleOn() : messages.getUpgradeToggleOff();

            totem.writeContainer(display.getPersistentDataContainer());
            message.send(event.getWhoClicked(), "upgrade", upgrade.getName());
            this.placeUpgrades();
        };

        Consumer<InventoryClickEvent> upgradeAction = event -> {
            Totem totem = this.totemSupplier.get();
            if (upgrade.increaseLevel((Player) event.getWhoClicked(), totem)) {
                this.placeUpgrades();
            }
        };

        Placeholders placeholders = upgrade.getPlaceholders(this.totemSupplier.get());
        ItemStack upgradeItem = upgrade.getIcon().create(placeholders);
        GuiItem enabled = new GuiItem(this.config.getUpgradeActive().create(placeholders), toggleAction::accept);
        GuiItem disabled = new GuiItem(this.config.getUpgradeInactive().create(placeholders), toggleAction::accept);

        boolean currentStatus = !(upgrade instanceof Toggleable toggleable) || toggleable.isActivated();
        return new PagePair(PairDirection.VERTICAL_DOWN,
                new GuiItem(upgradeItem, upgradeAction::accept),
                currentStatus ? enabled : disabled);

    }

    /**
     * Creates the menu for the plugin
     *
     * @return the resulting menu
     */
    @Override
    public Supplier<BiPaginatedGui> createMenu() {
        return () -> BiPaginatedGui.builder()
                .title(Component.text(this.config.getTitle()))
                .rows(this.config.getRows())
                .pageRow(28, 34)
                .disableAllInteractions()
                .create();
    }

    @ConfigSerializable
    @SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
    public static class Config extends GuiConfig {

        private MenuItem levelUpTotem = ItemConstruct.of(Material.EXPERIENCE_BOTTLE)
                .setName("<white>[<#94bc80><bold>Level Up Totem</bold><white>]")
                .setLore("<gray>add info & cost n stuff :)")
                .asMenuItem(11);


        private MenuItem durationUpgrade = ItemConstruct.of(Material.PAPER)
                .setName("<#93bc80><bold>Duration</bold> <gray>- <white>(<level><gray>/<white><max_level>)")
                .setLore(
                        "<gray>Increases the active time for a totem.",
                        "",
                        "<#93bc80>Current Stats:",
                        " <#93bc80>➡ <white>Level: <#93bc80><level>",
                        " <#93bc80>➡ <white>Duration: <#93bc80><total>",
                        " <#93bc80>➡ <white>Level Up Cost: <#93bc80><cost>",
                        " <#93bc80>➡ <white>Max Level : <#93bc80><max_level>",
                        "",
                        "<#93bc80>⏩ <white>Left Click to Upgrade"
                )
                .setCustomModelData(210002)
                .asMenuItem(14);

        private MenuItem radiusUpgrade = ItemConstruct.of(Material.PAPER)
                .setName("<#93bc80><bold>Radius</bold> <gray>- <white>(<level><gray>/<white><max_level>)")
                .setLore(
                        "<gray>Increases the effect range of each block",
                        "",
                        "<#93bc80>Current Stats:",
                        " <#93bc80>➡ <white>Level: <#93bc80><level>",
                        " <#93bc80>➡ <white>Radius: <#93bc80><total>",
                        " <#93bc80>➡ <white>Level Up Cost: <#93bc80><cost>",
                        " <#93bc80>➡ <white>Max Level : <#93bc80><max_level>",
                        "",
                        "<#93bc80>⏩ <white>Left Click to Upgrade"
                )
                .setCustomModelData(210003)
                .asMenuItem(15);

        private MenuItem cooldownUpgrade = ItemConstruct.of(Material.PAPER)
                .setName("<#93bc80><bold>Cooldown</bold> <gray>- <white>(<level><gray>/<white><max_level>)")
                .setLore(
                        "<gray>Decreases the cooldown between each totem activation",
                        "",
                        "<#93bc80>Current Stats:",
                        " <#93bc80>➡ <white>Level: <#93bc80><level>",
                        " <#93bc80>➡ <white>Cooldown: <#93bc80><total>",
                        " <#93bc80>➡ <white>Level Up Cost: <#93bc80><cost>",
                        " <#93bc80>➡ <white>Max Level : <#93bc80><max_level>",
                        "",
                        "<#93bc80>⏩ <white>Left Click to Upgrade"
                )
                .setCustomModelData(210001)
                .asMenuItem(16);

        private ItemConstruct upgradeActive = ItemConstruct.of(Material.LIME_DYE)
                .setName("<#05e653><bold>Activated")
                .setLore(
                        "<gray>Toggles whether this upgrade should be active",
                        "",
                        "<white>⏩ <#93bc80>Left Click to Deactivate"
                )
                .setProperty(ConstructType.GLOWING, x -> x.setEnabled(true));

        private ItemConstruct upgradeInactive = ItemConstruct.of(Material.RED_DYE)
                .setName("<#e60505><bold>Disabled")
                .setLore(
                        "<gray>Toggles whether this upgrade should be active",
                        "",
                        "<white>⏩ <#93bc80>Left Click to Deactivate"
                );

        private MenuItem previousPage = ItemConstruct.of(Material.PLAYER_HEAD)
                .setName("<white>[<#94bc80><bold>Previous Page</bold><white>]")
                .setLore("<gray>Click here to change the current page")
                .setProperty(TEXTURE, x -> x.setValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjRiZmVmMTRlODQyMGEyNTZlNDU3YTRhN2M4ODExMmUxNzk0ODVlNTIzNDU3ZTQzODUxNzdiYWQifX19"))
                .asMenuItem(27);

        private MenuItem nextPage = ItemConstruct.of(Material.PLAYER_HEAD)
                .setName("<white>[<#94bc80><bold>Next Page</bold><white>]")
                .setLore("<gray>Click here to change the current page")
                .setProperty(TEXTURE, x -> x.setValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGQ5OTNiOGMxMzU4ODkxOWI5ZjhiNDJkYjA2NWQ1YWRmZTc4YWYxODI4MTViNGU2ZjBmOTFiYTY4M2RlYWM5In19fQ=="))
                .asMenuItem(35);

        public Config() {
            this.title = "Fishing Totem | Upgrades";
            this.rows = 6;
            this.dummyItems.add(new MenuItem(this.border(Material.BLACK_STAINED_GLASS_PANE), FishUtils.parseList("0-53")));
            this.dummyItems.add(new MenuItem(this.border(Material.GREEN_STAINED_GLASS_PANE), FishUtils.parseList("28-34", "37-43")));
            this.dummyItems.add(new MenuItem(
                    ItemConstruct.of(Material.PLAYER_HEAD)
                            .setProperty(TOOLTIP, HIDDEN)
                            .setProperty(TEXTURE, x -> x.setValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYzMzlmZjJlNTM0MmJhMThiZGM0OGE5OWNjYTY1ZDEyM2NlNzgxZDg3ODI3MmY5ZDk2NGVhZDNiOGFkMzcwIn19fQ==")),
                    10
            ));

            this.dummyItems.add(new MenuItem(
                    ItemConstruct.of(Material.PLAYER_HEAD)
                            .setProperty(TOOLTIP, HIDDEN)
                            .setProperty(TEXTURE, x -> x.setValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODU1MGI3Zjc0ZTllZDc2MzNhYTI3NGVhMzBjYzNkMmU4N2FiYjM2ZDRkMWY0Y2E2MDhjZDQ0NTkwY2NlMGIifX19")),
                    12
            ));
        }

        public MenuItem getLevelUpTotem() {
            return levelUpTotem;
        }

        public MenuItem getDurationUpgrade() {
            return durationUpgrade;
        }

        public MenuItem getRadiusUpgrade() {
            return radiusUpgrade;
        }

        public MenuItem getCooldownUpgrade() {
            return cooldownUpgrade;
        }

        public ItemConstruct getUpgradeActive() {
            return upgradeActive;
        }

        public ItemConstruct getUpgradeInactive() {
            return upgradeInactive;
        }

        public MenuItem getPreviousPage() {
            return previousPage;
        }

        public MenuItem getNextPage() {
            return nextPage;
        }

    }

}
