package dev.oribuin.fishing.config.item.component;

import dev.oribuin.fishing.config.item.ConstructComponent;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import static io.papermc.paper.datacomponent.DataComponentTypes.DYED_COLOR;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal", "UnstableApiUsage" })
public class DyeItemType extends ConstructComponent<DyedItemColor> {

    private Color color;

    /**
     * Create a new ColoredStack object from a Color object
     */
    public DyeItemType() {
        this.color = Color.WHITE;
    }

    /**
     * Create a new ColoredStack object from a Color object
     *
     * @param color The Color object to create the ColoredStack from
     */
    public DyeItemType(Color color) {
        this.color = color;
    }

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @Nullable DyedItemColor establish() {
        return DyedItemColor.dyedItemColor(this.color);
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        DyedItemColor established = this.establish();
        if (established == null || !this.enabled) return;

        stack.setData(DYED_COLOR, established);
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(DYED_COLOR);
    }

    /**
     * Create a new ColoredStack object from an ItemStack object
     *
     * @param stack The DyedItemColor object to create the ColoredStack from
     *
     * @return The ColoredStack object
     */
    public static DyeItemType from(ItemStack stack) {
        if (stack == null) return null;
        
        DyedItemColor itemColor = stack.getData(DYED_COLOR);
        return itemColor != null ? new DyeItemType(itemColor.color()) : null;
    }

}
