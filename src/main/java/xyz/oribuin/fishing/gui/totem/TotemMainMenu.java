package xyz.oribuin.fishing.gui.totem;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.gui.RoseMenuWrapper;
import dev.rosewood.rosegarden.gui.fill.type.MenuFill;
import dev.rosewood.rosegarden.gui.item.RoseItem;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Material;

public class TotemMainMenu extends RoseMenuWrapper {

    public static final String ID = "totem/main_menu"; // will / cause problems who knows

    public TotemMainMenu(RosePlugin rosePlugin) {
        super(rosePlugin, ID);
    }

    @Override
    public void create() {
        this.addPage("Totem Menu", 27, page -> {
            page.fill(MenuFill.of(), RoseItem.of(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("&f"));
            StringPlaceholders placeholders = StringPlaceholders.empty(); // need to get totem, i imagine adding totem to constructor will break the whole library

            RoseItem statsItem = RoseItem.of(Material.KNOWLEDGE_BOOK)
                    .setDisplayName(placeholders.apply("&#4f73d6&lTotem Stats"))
                    .setLore(
                            placeholders.apply("&7"),
                            placeholders.apply("&#4f73d6- &7Owner: &f%owner%"),
                            placeholders.apply("&#4f73d6- &7Active: &f%active%"),
                            placeholders.apply("&#4f73d6- &7Radius: &f%radius% blocks"),
                            placeholders.apply("&#4f73d6- &7Duration: &f%duration%"),
                            placeholders.apply("&#4f73d6- &7Cooldown: &f%cooldown%"),
                            placeholders.apply("&7")
                    );

            page.addIcon(4, statsItem);

            //            RoseItem totemActive = RoseItem.of(Material.PLAYER_HEAD)
            //                    .setSkullTexture(RED)
            //                    .setDisplayName(placeholders.apply("&#4f73d6&lActive &7[&f%timer%&7]"))
            //                    .setLore("&7Take advantage of the", "&7totem while it's active!");
            //
            //            RoseItem totemCooldown = RoseItem.of(Material.PLAYER_HEAD)
            //                    .setSkullTexture(ORANGE)
            //                    .setDisplayName(placeholders.apply("&#4f73d6&lCooldown &7[&f%cooldownTimer%&7]"))
            //                    .setLore("&7The totem is on cooldown", "&7wait for it to finish!");
            //
            //            RoseItem totemReady = RoseItem.of(Material.PLAYER_HEAD)
            //                    .setSkullTexture(GREEN)
            //                    .setDisplayName(placeholders.apply("&#4f73d6&lActivate Totem"))
            //                    .setLore("&7Activate the totem to", "&7take advantage of its benefits!");
        });
    }

    private static final String RED = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZkZTNiZmNlMmQ4Y2I3MjRkZTg1NTZlNWVjMjFiN2YxNWY1ODQ2ODRhYjc4NTIxNGFkZDE2NGJlNzYyNGIifX19";
    private static final String ORANGE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM0ODg2ZWYzNjJiMmM4MjNhNmFhNjUyNDFjNWM3ZGU3MWM5NGQ4ZWM1ODIyYzUxZTk2OTc2NjQxZjUzZWEzNSJ9fX0=";
    private static final String GREEN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDI3Y2E0NmY2YTliYjg5YTI0ZmNhZjRjYzBhY2Y1ZTgyODVhNjZkYjc1MjEzNzhlZDI5MDlhZTQ0OTY5N2YifX19";
    //
    //    public TotemMainMenu(FishingPlugin plugin) {
    //        super(plugin, "totem/main_menu");
    //
    //        this.title = "Totem Menu";
    //        this.rows = 3;
    //    }
    //
    //    /**
    //     * Open the GUI for the specified player
    //     *
    //     * @param totem  The totem to open the GUI for
    //     * @param player The player to open the GUI for
    //     */
    //    public void open(Totem totem, Player player) {
    //        PaginatedGui gui = this.createPaginated();
    //
    //        this.placeExtras(gui);
    //        this.placeDynamics(gui, totem, player);
    //
    //        // TODO: Task to update the GUI every second
    //
    //        gui.open(player);
    //    }
    //
    //    /**
    //     * Place the dynamic items in the GUI for the totem
    //     *
    //     * @param gui    The GUI to place the items in
    //     * @param totem  The totem to place the items for
    //     * @param player The player to place the items for
    //     */
    //    private void placeDynamics(PaginatedGui gui, Totem totem, Player player) {
    //        this.placeItem(gui, "totem-stats", totem.placeholders(), x -> {
    //            // Withdraw the totem and give it to the player
    //        });
    //
    //        // The totem is active, display the active totem item
    //        if (totem.active()) {
    //            this.placeItem(gui, "totem-active", totem.placeholders(), x -> {
    //                // Withdraw the totem and give it to the player
    //            });
    //        }
    //
    //        // The totem is not active and is on cooldown, display the cooldown item
    //        if (!totem.active() && totem.onCooldown()) {
    //            this.placeItem(gui, "totem-cooldown", totem.placeholders(), x -> {
    //                // Withdraw the totem and give it to the player
    //            });
    //        }
    //
    //        // The totem is not active and is not on cooldown, display the activate item
    //        if (!totem.active() && !totem.onCooldown()) {
    //            this.placeItem(gui, "totem-activate", totem.placeholders(), x -> {
    //                // Withdraw the totem and give it to the player
    //            });
    //        }
    //
    //        // TODO: Add Totem Upgrades to the GUI
    //
    //
    //        gui.update();
    //    }
    //
    //    /**
    //     * Save the configuration file for the configurable class
    //     * I would recommend always super calling this method to save any settings that could be implemented
    //     *
    //     * @param config The configuration file to save
    //     */
    //    @Override
    //    public void saveSettings(@NotNull CommentedConfigurationSection config) {
    //        super.saveSettings(config);
    //
    //        // Default OPTIONS :DDD
    //        CommentedConfigurationSection items = this.pullSection(config, "items");
    //        CommentedConfigurationSection statSection = this.pullSection(items, "1");
    //
    //        GuiItem statsItem = new GuiItem("totem-stats", 4, ItemConstruct.of(Material.KNOWLEDGE_BOOK)
    //                .name("&#4f73d6&lTotem Stats")
    //                .lore(
    //                        "",
    //                        " &#4f73d6- &7Owner: &f%owner%",
    //                        " &#4f73d6- &7Active: &f%active%",
    //                        " &#4f73d6- &7Radius: &f%radius% blocks",
    //                        " &#4f73d6- &7Duration: &f%duration%",
    //                        " &#4f73d6- &7Cooldown: &f%cooldown%",
    //                        ""
    //                ));
    //
    //        statsItem.saveSettings(statSection); // Save the settings for the stats item
    //
    //        CommentedConfigurationSection activeSection = this.pullSection(items, "2");
    //        GuiItem activeItem = new GuiItem("totem-active", 22, ItemConstruct.of(Material.PLAYER_HEAD)
    //                .name("&#4f73d6&lActive &7[&f%timer%&7]")
    //                .lore("&7Take advantage of the", "&7totem while it's active!")
    //                .texture(GREEN)
    //        );
    //
    //        activeItem.saveSettings(activeSection); // Save the settings for the active item
    //
    //        CommentedConfigurationSection cooldownSection = this.pullSection(items, "3");
    //        GuiItem cooldownItem = new GuiItem("totem-cooldown", 22, ItemConstruct.of(Material.PLAYER_HEAD)
    //                .name("&#4f73d6&lCooldown &7[&f%cooldownTimer%&7]")
    //                .lore("&7The totem is on cooldown", "&7wait for it to finish!")
    //                .texture(ORANGE)
    //        );
    //
    //        cooldownItem.saveSettings(cooldownSection); // Save the settings for the cooldown item
    //
    //        CommentedConfigurationSection activateSection = this.pullSection(items, "4");
    //        GuiItem activateItem = new GuiItem("totem-activate", 22, ItemConstruct.of(Material.PLAYER_HEAD)
    //                .name("&#4f73d6&lActivate Totem")
    //                .lore("&7Activate the totem to", "&7take advantage of its benefits!")
    //                .texture(RED)
    //        );
    //
    //        activateItem.saveSettings(activateSection); // Save the settings for the activate item
    //
    //        // Border Item
    //        CommentedConfigurationSection borderSection = this.pullSection(config, "extra-items");
    //        CommentedConfigurationSection borderItem = this.pullSection(borderSection, "1");
    //
    //        GuiItem border = new GuiItem("border");
    //        border.item(PluginMenu.BORDER);
    //        border.parseSlots(List.of("0-9", "17-26"));
    //        border.saveSettings(borderItem);
    //    }

}
