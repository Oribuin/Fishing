package dev.oribuin.fishing.config.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public abstract class ConstructComponent<T> {

    protected boolean enabled = true;

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Nullable
    public abstract T establish();

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    public abstract void apply(@NotNull ItemStack stack);

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    public abstract void clear(@NotNull ItemStack stack);

    /**
     * Should the value be applied to the itemstack
     *
     * @return The stack to apply
     */
    public final boolean isEnabled() {
        return this.enabled;
    }

    public ConstructComponent<T> setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ConstructComponent<T> setEnabled() {
        this.enabled = true;
        return this;
    }

    public ConstructComponent<T> setDisabled() {
        this.enabled = false;
        return this;
    }


}
