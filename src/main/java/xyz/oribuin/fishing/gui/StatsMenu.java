package xyz.oribuin.fishing.gui;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.gui.GuiItem;
import xyz.oribuin.fishing.api.gui.PluginMenu;
import xyz.oribuin.fishing.manager.base.DataManager;
import xyz.oribuin.fishing.storage.Fisher;
import xyz.oribuin.fishing.util.ItemConstruct;

public class StatsMenu extends PluginMenu {

    public StatsMenu(FishingPlugin plugin) {
        super(plugin, "stats");
    }

    /**
     * Open the GUI for the specified player
     *
     * @param player The player to open the GUI for
     */
    @Override
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
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        super.saveSettings(config);

        // Default OPTIONS :DDD
        CommentedConfigurationSection items = this.pullSection(config, "items");
        CommentedConfigurationSection item = this.pullSection(items, "1.stats");

        GuiItem statsItem = new GuiItem();
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
