package dev.oribuin.fishing.gui.type.bipaginated;

import dev.triumphteam.gui.builder.gui.BaseChestGuiBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static dev.oribuin.fishing.gui.PluginMenu.slotToRows;

public class BiPaginatedBuilder extends BaseChestGuiBuilder<BiPaginatedGui, BiPaginatedBuilder> {

    private BiPageRow pageRow = new BiPageRow(1);

    @NotNull
    @Contract("_ -> this")
    public BiPaginatedBuilder pageRow(final int pageRow) {
        this.pageRow = new BiPageRow(pageRow);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public BiPaginatedBuilder pageRow(final BiPageRow pageRow) {
        this.pageRow = pageRow;
        return this;
    }

    @NotNull
    public BiPaginatedBuilder pageRow(int start, int end) {
        this.pageRow = new BiPageRow(slotToRows(start), start, end);
        return this;
    }

    /**
     * Creates a new {@link BiPaginatedGui}
     *
     * @return A new {@link BiPaginatedGui}
     */
    @NotNull
    @Override
    @Contract(" -> new")
    public BiPaginatedGui create() {
        final BiPaginatedGui gui = new BiPaginatedGui(createContainer(), this.pageRow, getModifiers());

        final Consumer<BiPaginatedGui> consumer = getConsumer();
        if (consumer != null) consumer.accept(gui);

        return gui;
    }

}
