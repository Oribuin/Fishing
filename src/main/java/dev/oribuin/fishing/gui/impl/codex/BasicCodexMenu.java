package dev.oribuin.fishing.gui.impl.codex;

import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Predicate;

public abstract class BasicCodexMenu<T> extends PluginMenu<PaginatedGui> {

    /**
     * Creates a new plugin menu instance, with the specified name
     *
     * @param name The name of the menu, will be also function as the menu path
     */
    public BasicCodexMenu(String name) {
        super(name);
    }

    /**
     * Get all the content that is going to be displayed in the codex
     *
     * @param player    The player to get the content for
     * @param condition The condition required to display the content
     *
     * @return The content to display in the menu
     */
    public abstract List<T> getContent(Player player, Predicate<T> condition);

    protected final ItemConstruct PAGE_FORWARD = new ItemConstruct(Material.ARROW)
            .setName("<white>[<#94bc80>Next Page<white>]")
            .setLore("<gray>Click here to go to the next page");
    
    protected final ItemConstruct CODEX_MAIN_MENU = new ItemConstruct(Material.KNOWLEDGE_BOOK)
            .setName("<white>[<#94bc80>Codex Menu<white>]")
            .setLore(
                    "<gray>Click here to go to the index page", 
                    "<gray>with all the different types of information", 
                    "<gray>available in the codex"
            );

    protected final ItemConstruct PAGE_BACKWARD = new ItemConstruct(Material.ARROW)
            .setName("<white>[<#94bc80>Previous Page<white>]")
            .setLore("<gray>Click here to go to the previous page");
    
}
