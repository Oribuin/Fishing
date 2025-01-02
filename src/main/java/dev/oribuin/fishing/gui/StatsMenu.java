package dev.oribuin.fishing.gui;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.gui.GuiItem;
import dev.oribuin.fishing.api.gui.PluginMenu;
import dev.oribuin.fishing.manager.base.DataManager;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.util.ItemConstruct;

public class StatsMenu extends PluginMenu {

    public StatsMenu(FishingPlugin plugin) {
        super(plugin, "stats");
    }

    /**
     * Open the GUI for the specified player
     *
     * @param player The player to open the GUI for
     */
    public void open(Player player) {
        Fisher fisher = this.plugin.getManager(DataManager.class).get(player.getUniqueId());
        if (fisher == null) {
            player.sendMessage("§cAn error occurred while loading your data.");
            return;
        }

        Gui gui = this.createRegular();

        this.placeExtras(gui); // Place the extra items

        // Place the stats item in the GUI
        this.placeItem(gui, "stats", fisher.placeholders(), event -> {
            player.sendMessage("§aYou clicked the stats item!");
        });

        gui.update();
        gui.open(player);
    }

    /**
     * Serialize the settings of the configurable class into a {@link CommentedConfigurationSection} to be saved later
     * <p>
     * This functionality will not update the configuration file, it will only save the settings into the section to be saved later.
     * <p>
     * The function {@link #reload()} will save the settings on first load, please override this method if you wish to save the settings regularly
     * New sections should be created using {@link #pullSection(CommentedConfigurationSection, String)}
     *
     * @param config The {@link CommentedConfigurationSection} to save the settings to, this cannot be null.
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        super.saveSettings(config);

        // Default OPTIONS :DDD
        CommentedConfigurationSection items = this.pullSection(config, "items");
        CommentedConfigurationSection item = this.pullSection(items, "1");

        GuiItem statsItem = new GuiItem("stats");
        ItemConstruct construct = ItemConstruct.of(Material.PLAYER_HEAD)
                .name("&f&lUser Stats")
                .lore(
                        " &7| &fYour Stats:",
                        " &7| &fLevel: &b%level%",
                        " &7| &fExp: &b%experience%/%required_exp%",
                        " &7| &fPoints: &b%skill_points%"
                );

        statsItem.item(construct);
        statsItem.saveSettings(item); // Save the settings for the stats item
    }

}
