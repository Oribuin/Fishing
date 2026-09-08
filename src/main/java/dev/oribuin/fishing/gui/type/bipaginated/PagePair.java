package dev.oribuin.fishing.gui.type.bipaginated;

import dev.triumphteam.gui.guis.GuiItem;

public class PagePair {

    private final PairDirection direction;
    private GuiItem primary;
    private GuiItem secondary;

    public PagePair(PairDirection direction, GuiItem primary, GuiItem secondary) {
        this.direction = direction;
        this.primary = primary;
        this.secondary = secondary;
    }

    public PairDirection getDirection() {
        return direction;
    }

    public GuiItem getPrimary() {
        return primary;
    }

    public void setPrimary(GuiItem primary) {
        this.primary = primary;
    }

    public GuiItem getSecondary() {
        return secondary;
    }

    public void setSecondary(GuiItem secondary) {
        this.secondary = secondary;
    }
}
