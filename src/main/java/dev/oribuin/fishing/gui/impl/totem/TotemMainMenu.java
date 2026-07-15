package dev.oribuin.fishing.gui.impl.totem;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.item.ConstructComponent;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.gui.GuiConfig;
import dev.oribuin.fishing.gui.GuiTickable;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Supplier;

public class TotemMainMenu extends PluginMenu<Gui, TotemMainMenu.Config> implements GuiTickable {

    private final Supplier<Totem> totemSupplier;

    public TotemMainMenu(FishingPlugin plugin, Supplier<Totem> totemSupplier) {
        super(plugin, Config.class);
        this.gui = this.createMenu().get();
        this.totemSupplier = totemSupplier;

        Totem totem = this.totemSupplier.get();
        System.out.println("MAIN MENU - TOTEM DISPLAY ID: " + totem.getDisplayId());
        Fisher fisher = plugin.getDataManager().get(totem.getOwner());
        Placeholders placeholders = Placeholders.builder()
                .addAll(fisher.getPlaceholders())
                .addAll(totem.getPlaceholders())
                .build();

        this.setDummyIcons(placeholders);
        this.config.getTotemName().place(this.gui, placeholders);
        this.config.getTotemPrivacy().place(this.gui, placeholders);
        this.config.getTotemStats().place(this.gui, placeholders, event -> {
            // TODO: Totem Stats menu
        });

        this.config.getTotemUpgrades().place(this.gui, placeholders, event -> {
            TotemUpgradeMenu upgradeMenu = new TotemUpgradeMenu(plugin, this.totemSupplier);
            upgradeMenu.open((Player) event.getWhoClicked());
        });

        this.tick();
    }

    /**
     * Open the menu for the player synchronously and mark the menu as being viewed
     *
     * @param player The player opening the menu
     */
    @Override
    public void open(Player player) {
        Totem totem = this.totemSupplier.get();
        boolean isOwner = totem.getOwner().equals(player.getUniqueId());
        boolean canAccess = switch (totem.getPrivacy()) {
            case PUBLIC -> true;
            case FRIENDS_ONLY -> isOwner || totem.getUsers().contains(player.getUniqueId());
            default -> isOwner;
        };

        if (!canAccess) {
            PluginMessages.get().getTotem().getCannotAccess().send(player);
            return;
        }

        super.open(player);
    }

    /**
     * Creates a tickable task for a {@link PluginMenu}
     */
    @Override
    public void tick() {
        Totem totem = this.totemSupplier.get();
        if (totem == null) return;

        Fisher fisher = plugin.getDataManager().get(totem.getOwner());
        Placeholders placeholders = Placeholders.builder()
                .addAll(fisher.getPlaceholders())
                .addAll(totem.getPlaceholders())
                .build();

        // Totem is currently active :)
        if (totem.isActive()) this.config.getTotemActive().place(this.gui, placeholders, event -> {
            Player activator = (Player) event.getWhoClicked();
            PluginMessages.get().getTotem().getAlreadyActive().send(activator, placeholders);
        });

        // Totem is on cooldown and is no logner active
        if (!totem.isActive() && totem.onCooldown()) this.config.getTotemCooldown().place(this.gui, placeholders, event -> {
            Player activator = (Player) event.getWhoClicked();
            PluginMessages.get().getTotem().getOnCooldown().send(activator, placeholders);
        });

        // Totem is not on cooldown and not active (this is where you can activate it)
        if (!totem.isActive() && !totem.onCooldown()) {
            this.config.getTotemActivate().place(this.gui, placeholders, event -> {
                Player activator = (Player) event.getWhoClicked();

                boolean isOwner = totem.getOwner().equals(activator.getUniqueId());
                boolean canActivate = switch (totem.getPrivacy()) {
                    case PUBLIC -> true;
                    case FRIENDS_ONLY -> isOwner || totem.getUsers().contains(activator.getUniqueId());
                    default -> isOwner;
                };

                if (!canActivate) {
                    PluginMessages.get().getTotem().getCannotActivate().send(activator);
                    return;
                }

                // Tell the owner that their totem was activated
                Player owner = Bukkit.getPlayer(totem.getOwner());
                if (owner != null && !isOwner) {
                    PluginMessages.get().getTotem().getOtherPlayerActivated().send(owner, "activator", activator.getName());
                }

                totem.activate(activator); // Activate the totem // TODO: Play totem activate animation
                activator.closeInventory(InventoryCloseEvent.Reason.PLUGIN); // Close the player's inventory
            });
        }

        this.gui.update();
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
        // TODO: Totem Privacy

        private MenuItem totemName = ItemConstruct.of(Material.NAME_TAG)
                .setName("<white>[<#94bc80>Totem Name<white>]")
                .setLore(
                        "<gray>Change the display name for this",
                        "<gray>fishing totem"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .asMenuItem(15);

        private MenuItem totemPrivacy = ItemConstruct.of(Material.TRIAL_KEY)
                .setName("<white>[<#94bc80>Totem Privacy<white>]")
                .setLore(
                        "<gray>Change the level of access that others",
                        "<gray>have to this fishing totem",
                        "",
                        " <#94bc80>- <white>Status: <#94bc80><status>"

                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .asMenuItem(16);

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

        private MenuItem totemUpgrades = ItemConstruct.of(Material.PAPER)
                .setName("<white>[<#94bc80>Totem Upgrades<white>]")
                .setLore(
                        "<gray>Click here to view and level",
                        "<gray>up this fishing totem",
                        "",
                        "<#94bc80>Levels:",
                        " <#94bc80>- <white>Radius: <#94bc80><upgrade_radius>",
                        " <#94bc80>- <white>Duration: <#94bc80><upgrade_duration>",
                        " <#94bc80>- <white>Cooldown: <#94bc80><upgrade_cooldown>"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .setProperty(ConstructType.MODEL, x -> x.setValue("minecraft:netherite_upgrade_smithing_template"))
                .asMenuItem(10);

        private MenuItem totemActivate = ItemConstruct.of(Material.LIME_DYE)
                .setName("<white>[<#05e653>Activate Totem<white>]")
                .setLore(
                        "<gray>Click here to active this totem",
                        "",
                        "<#05e653>Details:",
                        " <#05e653>- <white>Radius: <#05e653><upgrade_radius_total>",
                        " <#05e653>- <white>Duration: <#05e653><upgrade_duration_total>",
                        " <#05e653>- <white>Cooldown: <#05e653><upgrade_cooldown_total>"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .asMenuItem(13);

        private MenuItem totemCooldown = ItemConstruct.of(Material.RED_DYE)
                .setName("<white>[<#e60505>On Cooldown<white>]")
                .setLore(
                        "<gray>This totem is currently on cooldown",
                        "",
                        "<#e65f05>- <white>Time Remaining: <#e65f05><upgrade_cooldown>"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .asMenuItem(13);

        private MenuItem totemActive = ItemConstruct.of(Material.ORANGE_DYE)
                .setName("<white>[<#e65f05>Currently Active<white>]")
                .setLore(
                        "<gray>Your totem is currently active",
                        "",
                        "<#e65f05>- <white>Time Remaining: <#e65f05><upgrade_duration_remaining>"
                )
                .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled)
                .asMenuItem(13);

        public Config() {
            this.title = "Fishing Totem | Main Menu";
            this.rows = 3;
            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList("0-8", "18-26", "9", "17")));
        }

        public MenuItem getTotemName() {
            return totemName;
        }

        public MenuItem getTotemPrivacy() {
            return totemPrivacy;
        }

        public MenuItem getTotemStats() {
            return totemStats;
        }

        public MenuItem getTotemUpgrades() {
            return totemUpgrades;
        }

        public MenuItem getTotemActivate() {
            return totemActivate;
        }

        public MenuItem getTotemCooldown() {
            return totemCooldown;
        }

        public MenuItem getTotemActive() {
            return totemActive;
        }
    }
}
