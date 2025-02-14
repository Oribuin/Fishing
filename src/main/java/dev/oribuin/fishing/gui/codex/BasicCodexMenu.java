package dev.oribuin.fishing.gui.codex;

import dev.oribuin.fishing.api.gui.PluginMenu;
import dev.triumphteam.gui.guis.PaginatedGui;
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

}
