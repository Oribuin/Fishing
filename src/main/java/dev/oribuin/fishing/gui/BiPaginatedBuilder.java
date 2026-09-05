package dev.oribuin.fishing.gui;

import dev.triumphteam.gui.builder.gui.BaseChestGuiBuilder;
import dev.triumphteam.gui.builder.gui.PaginatedBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BiPaginatedBuilder extends BaseChestGuiBuilder<BiPaginatedGui, BiPaginatedBuilder> {
    
    private int pageRow = 1;

    @NotNull
    @Contract("_ -> this")
    public BiPaginatedBuilder pageRow(final int pageRow) {
        this.pageRow = pageRow;
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
        final BiPaginatedGui gui = new BiPaginatedGui(createContainer(), pageRow, getModifiers());

        final Consumer<BiPaginatedGui> consumer = getConsumer();
        if (consumer != null) consumer.accept(gui);

        return gui;
    }

}
