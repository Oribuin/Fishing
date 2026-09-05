package dev.oribuin.fishing.gui.paired;

import java.util.function.Function;

/**
 * Establish the directional gap between two paged items
 *
 * @see dev.oribuin.fishing.gui.BiPaginatedGui For where this is applicable
 * @see PagePair For creating a pair of page items
 */
public enum PairDirection {
    VERTICAL_UP(integer -> Math.max(0, integer - 9)),
    VERTICAL_DOWN(integer -> Math.min(54, integer + 9));

    private final Function<Integer, Integer> pairSlotSupplier;

    PairDirection(Function<Integer, Integer> pairSlotSupplier) {
        this.pairSlotSupplier = pairSlotSupplier;
    }

    public int getOtherSlot(int current) {
        return this.pairSlotSupplier.apply(current);
    }
}
