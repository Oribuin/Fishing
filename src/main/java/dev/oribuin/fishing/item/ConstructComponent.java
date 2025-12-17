package dev.oribuin.fishing.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConstructComponent<T> {

    /**
     * Create a new item component type from the plugin
     * 
     * @return item component type
     */
    @Nullable
    T establish();

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    void apply(@NotNull ItemStack stack);

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    void clear(@NotNull ItemStack stack);

}
