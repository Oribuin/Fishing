package dev.oribuin.fishing.config.item.component;

import dev.oribuin.fishing.config.item.ConstructComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@SuppressWarnings({ "UnstableApiUsage", "FieldMayBeFinal" })
public final class DyedConstructType implements ConstructComponent<DyedItemColor> {

    private Integer rgb;

    public DyedConstructType() {
        this.rgb = null;
    }

    public DyedConstructType(Integer rgb) {
        this.rgb = rgb;
    }

    public DyedConstructType(java.awt.Color color) {
        this(color.getRGB());
    }

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @Nullable DyedItemColor establish() {
        if (this.rgb == null) return null;

        return DyedItemColor.dyedItemColor(Color.fromRGB(this.rgb));
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        DyedItemColor profile = this.establish();
        if (profile != null) {
            stack.setData(DataComponentTypes.DYED_COLOR, profile);
        }
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(DataComponentTypes.DYED_COLOR);
    }


}