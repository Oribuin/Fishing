package dev.oribuin.fishing.gui.type.bipaginated;

public record BiPageRow(int row, int start, int end) {

    public BiPageRow(int row) {
        this(row, (row * 9) - 1, (row * 9) + 8);
    }


}
