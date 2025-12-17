package dev.oribuin.fishing.item;

import dev.oribuin.fishing.item.component.EdibleConstructType;
import dev.oribuin.fishing.item.component.EnchantConstructType;
import dev.oribuin.fishing.item.component.ModelConstructType;
import dev.oribuin.fishing.item.component.TextureConstructType;
import dev.oribuin.fishing.item.component.TooltipConstructType;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings({ "UnstableApiUsage", "FieldMayBeFinal" })

public class ConstructComponentRegistry {

    private static final Map<DataComponentType, Supplier<ConstructComponent<?>>> COMPONENTS = new HashMap<>();

    public static EdibleConstructType EDIBLE = register(DataComponentTypes.FOOD, EdibleConstructType::new);
    public static EnchantConstructType ENCHANT = register(DataComponentTypes.ENCHANTMENTS, EnchantConstructType::new);
    public static ModelConstructType MODEL = register(DataComponentTypes.ITEM_MODEL, ModelConstructType::new);
    public static TextureConstructType TEXTURE = register(DataComponentTypes.PROFILE, TextureConstructType::new);
    public static TooltipConstructType TOOLTIP = register(DataComponentTypes.TOOLTIP_DISPLAY, TooltipConstructType::new);

    /**
     * Register a construct component type into the plugin
     *
     * @param value    The component type
     * @param supplier The supplier for the builder
     * @param <T>      The construct component class
     *
     * @return The registered construct type
     */
    @SuppressWarnings("unchecked")
    public static <T> T register(DataComponentType value, Supplier<ConstructComponent<?>> supplier) {
        ConstructComponent<?> constructComponent = supplier.get();
        COMPONENTS.putIfAbsent(value, supplier);
        return (T) constructComponent;
    }

}
