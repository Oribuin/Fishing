package dev.oribuin.fishing.config.item.component;

import dev.oribuin.fishing.config.item.ConstructComponent;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import static io.papermc.paper.datacomponent.DataComponentTypes.FOOD;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal", "UnstableApiUsage" })
public class EdibleItemType extends ConstructComponent<FoodProperties> {

    private int nutrition;
    private int saturation;
    private boolean requireHunger;

    /**
     * Create a new EdibleStack object with default properties
     */
    public EdibleItemType() {
        this.nutrition = 10;
        this.saturation = 10;
        this.requireHunger = false;
    }

    /**
     * Create a new EdibleStack object with the given properties
     *
     * @param nutrition     The nutrition value of the item
     * @param saturation    The saturation value of the item
     * @param requireHunger Whether the item requires hunger to be consumed
     */
    public EdibleItemType(int nutrition, int saturation, boolean requireHunger) {
        this.nutrition = nutrition;
        this.saturation = saturation;
        this.requireHunger = requireHunger;
    }

    /**
     * Create a new EdibleConstructType object from an ItemStack object
     *
     * @param stack The EdibleConstructType object to create the ColoredStack from
     *
     * @return The EdibleConstructType object
     */
    public static EdibleItemType from(ItemStack stack) {
        if (stack == null) return null;

        FoodProperties properties = stack.getData(FOOD);
        return properties != null ? new EdibleItemType(
                properties.nutrition(), 
                (int) properties.saturation(), 
                !properties.canAlwaysEat()
        ) : null;
    }

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @Nullable FoodProperties establish() {
        return FoodProperties.food()
                .nutrition(this.nutrition)
                .saturation(this.saturation)
                .canAlwaysEat(!this.requireHunger)
                .build();
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        FoodProperties established = this.establish();
        if (established == null || !this.enabled) return;

        stack.setData(FOOD, established);
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(FOOD);
    }

}
