package dev.oribuin.fishing.item.component;

import dev.oribuin.fishing.item.ConstructComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@SuppressWarnings({ "UnstableApiUsage", "FieldMayBeFinal" })
public final class EdibleConstructType implements ConstructComponent<FoodProperties> {

    private Integer nutrition = null;
    private Float saturation = null;
    private Boolean alwaysConsumable = null;

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @Nullable FoodProperties establish() {
        // If no value was provided, create null food property
        if (this.nutrition == null && this.saturation == null && this.alwaysConsumable == null) {
            return null;
        }

        // Create defined food property
        return FoodProperties.food()
                .nutrition(this.nutrition != null ? this.nutrition : 0)
                .saturation(this.saturation != null ? this.saturation : 0)
                .canAlwaysEat(this.alwaysConsumable != null ? this.alwaysConsumable : false)
                .build();
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        FoodProperties properties = this.establish();
        if (properties == null) return;

        stack.setData(DataComponentTypes.FOOD, properties);
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(DataComponentTypes.FOOD);
    }

}