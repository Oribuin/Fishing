package dev.oribuin.fishing.gui;

import dev.oribuin.fishing.gui.paired.PagePair;
import dev.triumphteam.gui.components.GuiContainer;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BiPaginatedGui extends BaseGui {

    private final List<PagePair> pageItems;
    private final Map<Integer, PagePair> currentPage;
    private int pageRow;
    private int pageNum;
    private int pageSize;
    
    public BiPaginatedGui(@NotNull GuiContainer guiContainer, int pageRow, @NotNull Set<InteractionModifier> interactionModifiers) {
        super(guiContainer, interactionModifiers);
        this.pageItems = new ArrayList<>();
        this.currentPage = new ConcurrentHashMap<>(9);
        this.pageRow = pageRow;
        this.pageNum = 1;
        this.pageSize = 9;
    }
    
    public static BiPaginatedBuilder builder() {
        return new BiPaginatedBuilder();
    }

    public BiPaginatedGui setPageRow(int pageRow) {
        this.pageRow = pageRow;
        return this;
    }

    public BiPaginatedGui setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Add a page pair to the gui
     *
     * @param item The pair to add
     */
    public void addItem(@NotNull PagePair item) {
        this.pageItems.add(item);
    }

    public void addItem(PagePair... items) {
        this.pageItems.addAll(Arrays.asList(items));
    }

    public void update() {
        this.getInventory().clear();
        this.populateGui();
        this.updatePage();
    }

    /**
     * Update a page item on a gui
     *
     * @param slot      The slot to update
     * @param primary   The primary ItemStack to update
     * @param secondary The secondary ItemStack to update
     */
    public void updatePageItem(int slot, @Nullable ItemStack primary, @Nullable ItemStack secondary) {
        if (!this.currentPage.containsKey(slot)) return;

        PagePair pair = this.currentPage.get(slot);
        if (primary != null) pair.getPrimary().setItemStack(primary);
        if (secondary != null) pair.getSecondary().setItemStack(secondary);

        this.currentPage.put(slot, pair);

        // update the items
        this.getInventory().setItem(slot, pair.getPrimary().getItemStack());
        this.getInventory().setItem(pair.getDirection().getOtherSlot(slot), pair.getSecondary().getItemStack());
    }

    /**
     * Update a page item on a gui
     *
     * @param slot      The slot to update
     * @param primary   The primary GuiItem to update
     * @param secondary The secondary GuiItem to update
     */
    public void updatePageItem(int slot, @Nullable GuiItem primary, @Nullable GuiItem secondary) {
        if (!this.currentPage.containsKey(slot)) return;

        PagePair pair = this.currentPage.get(slot);
        int index = this.pageItems.indexOf(this.currentPage.get(slot));
        if (primary != null) pair.setPrimary(primary);
        if (secondary != null) pair.setSecondary(secondary);

        // update the items
        this.getInventory().setItem(slot, pair.getPrimary().getItemStack());
        this.getInventory().setItem(pair.getDirection().getOtherSlot(slot), pair.getSecondary().getItemStack());
        this.currentPage.put(slot, pair);
        this.pageItems.set(index, pair);
    }

    public void removePageItem(@NotNull PagePair item) {
        this.pageItems.remove(item);
        this.updatePage();
    }

    public void removePageItem(@NotNull ItemStack item) {
        Optional<PagePair> guiItem = this.pageItems.stream().filter((it) -> {
            if (it.getPrimary() != null && it.getPrimary().getItemStack().equals(item)) return true;

            return it.getSecondary() != null && it.getSecondary().getItemStack().equals(item);
        }).findFirst();
        guiItem.ifPresent(this::removePageItem);
    }

    public void open(@NotNull HumanEntity player) {
        this.open(player, 1);
    }

    public void open(@NotNull HumanEntity player, int openPage) {
        if (!player.isSleeping()) {
            if (openPage <= this.getPagesNum() || openPage > 0) {
                this.pageNum = openPage;
            }

            this.getInventory().clear();
            this.currentPage.clear();
            this.populateGui();
            this.populatePage();
            player.openInventory(this.getInventory());
        }
    }

    public @NotNull BaseGui updateTitle(@NotNull Component title) {
        this.setUpdating(true);
        List<HumanEntity> viewers = new ArrayList(this.getInventory().getViewers());
        GuiContainer guiContainer = this.guiContainer();
        guiContainer.title(title);
        this.setInventory(guiContainer.createInventory(this));

        for (HumanEntity player : viewers) {
            this.open(player, this.getPageNum());
        }

        this.setUpdating(false);
        return this;
    }

    public void populateGui() {
        for (final Map.Entry<Integer, GuiItem> entry : this.getGuiItems().entrySet()) {
            this.getInventory().setItem(entry.getKey(), entry.getValue().getItemStack());
        }
    }
    
    public @NotNull Map<@NotNull Integer, @NotNull PagePair> getCurrentPageItems() {
        return Collections.unmodifiableMap(this.currentPage);
    }

    public @NotNull List<@NotNull PagePair> getPageItems() {
        return Collections.unmodifiableList(this.pageItems);
    }

    public int getCurrentPageNum() {
        return this.pageNum;
    }

    public int getNextPageNum() {
        return this.pageNum + 1 > this.getPagesNum() ? this.pageNum : this.pageNum + 1;
    }

    public int getPrevPageNum() {
        return this.pageNum - 1 == 0 ? this.pageNum : this.pageNum - 1;
    }

    public boolean next() {
        if (this.pageNum + 1 > this.getPagesNum()) {
            return false;
        } else {
            ++this.pageNum;
            this.updatePage();
            return true;
        }
    }

    public boolean previous() {
        if (this.pageNum - 1 == 0) {
            return false;
        } else {
            --this.pageNum;
            this.updatePage();
            return true;
        }
    }

    public PagePair getPageItem(int slot) {
        return this.currentPage.get(slot);
    }

    private List<PagePair> getPageNum(int givenPage) {
        int page = givenPage - 1;
        List<PagePair> guiPage = new ArrayList();
        int max = page * this.pageSize + this.pageSize;
        if (max > this.pageItems.size()) {
            max = this.pageItems.size();
        }

        for (int i = page * this.pageSize; i < max; ++i) {
            guiPage.add(this.pageItems.get(i));
        }

        return guiPage;
    }

    public int getPagesNum() {
        return (int) Math.ceil((double) this.pageItems.size() / (double) this.pageSize);
    }

    private void populatePage() {
        int slot = this.pageRow * 9;
        Iterator<PagePair> iterator = this.getPageNum(this.pageNum).iterator();

        while (iterator.hasNext() && slot < this.getInventory().getSize()) {
            if (this.getGuiItem(slot) == null && this.getInventory().getItem(slot) == null) {
                PagePair pagePair = iterator.next();
                int secondarySlot = pagePair.getDirection().getOtherSlot(slot);
                this.currentPage.put(slot, pagePair);
                this.getInventory().setItem(slot, pagePair.getPrimary().getItemStack());
                this.getInventory().setItem(secondarySlot, pagePair.getSecondary().getItemStack());
            }
            ++slot;
        }

    }

    Map<Integer, PagePair> getMutableCurrentPageItems() {
        return this.currentPage;
    }

    void clearPage() {
        for (Map.Entry<Integer, PagePair> entry : this.currentPage.entrySet()) {
            int secondarySlot = entry.getValue().getDirection().getOtherSlot(entry.getKey());
            this.getInventory().setItem(entry.getKey(), null);
            this.getInventory().setItem(secondarySlot, null);
        }

    }

    public void clearPageItems(boolean update) {
        this.pageItems.clear();
        if (update) {
            this.update();
        }

    }

    public void clearPageItems() {
        this.clearPageItems(false);
    }

    public int getPageSize() {
        return 9;
    }

    int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void updatePage() {
        this.clearPage();
        this.populatePage();
    }
    
}
