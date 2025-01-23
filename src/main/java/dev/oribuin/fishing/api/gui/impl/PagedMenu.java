package dev.oribuin.fishing.api.gui.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.oribuin.fishing.api.gui.BaseGui;
import dev.oribuin.fishing.api.gui.MenuIcon;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class PagedMenu extends BaseGui {

    private final Map<Integer, MenuIcon> currentPage;
    private final List<MenuIcon> pageIcons;
    private final List<Integer> pageSlots;
    private int pageNum;

    /**
     * Create a new gui instance with the specified plugin and id
     *
     * @param plugin The plugin instance
     * @param id     The id of the gui
     */
    public PagedMenu(RosePlugin plugin, String id) {
        super(plugin, id);

        this.currentPage = new LinkedHashMap<>();
        this.pageIcons = new ArrayList<>();
        this.pageSlots = new ArrayList<>();
        this.pageNum = 1;
    }

    /**
     * Update the inventory with the static icons
     */
    @Override
    public void update() {
        super.update();
    }


    /**
     * Get the current page number
     *
     * @return The current page number
     */
    public int pageSize() {
        return this.pageIcons.size();
    }
}
