package dev.oribuin.fishing.config.item;

import dev.oribuin.fishing.config.item.component.AttributeItemType;
import dev.oribuin.fishing.config.item.component.DyeItemType;
import dev.oribuin.fishing.config.item.component.EdibleItemType;
import dev.oribuin.fishing.config.item.component.EnchantItemType;
import dev.oribuin.fishing.config.item.component.EquippableItemType;
import dev.oribuin.fishing.config.item.component.GliderItemType;
import dev.oribuin.fishing.config.item.component.GlowingItemType;
import dev.oribuin.fishing.config.item.component.ModelItemType;
import dev.oribuin.fishing.config.item.component.TextureItemType;
import dev.oribuin.fishing.config.item.component.ToolItemType;
import dev.oribuin.fishing.config.item.component.TooltipItemType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record ConstructType<T extends ConstructComponent<?>>(String identifier, Supplier<T> supplier) {

    private static final Map<String, ConstructType<?>> REGISTRY = new HashMap<>();
    public static ConstructType<AttributeItemType> ATTRIBUTE = create("attribute", AttributeItemType::new);
    public static ConstructType<DyeItemType> DYED = create("dyed", DyeItemType::new);
    public static ConstructType<EdibleItemType> EDIBLE = create("edible", EdibleItemType::new);
    public static ConstructType<EnchantItemType> ENCHANT = create("enchanted", EnchantItemType::new);
    public static ConstructType<EquippableItemType> EQUIPPABLE = create("equippable", EquippableItemType::new);
    public static ConstructType<GliderItemType> GLIDER = create("glider", GliderItemType::new);
    public static ConstructType<GlowingItemType> GLOWING = create("glowing", GlowingItemType::new);
    public static ConstructType<ModelItemType> MODEL = create("model", ModelItemType::new);
    public static ConstructType<TextureItemType> TEXTURE = create("texture", TextureItemType::new);
    public static ConstructType<ToolItemType> TOOL = create("tool", ToolItemType::new);
    public static ConstructType<TooltipItemType> TOOLTIP = create("tooltip", TooltipItemType::new);

    /**
     * Create a new construct type within the plugin to register
     *
     * @param supplier The supplier to register
     * @param <T>      The construct type
     *
     * @return The resulting construct
     */
    public static <T extends ConstructComponent<?>> ConstructType<T> create(String identifier, Supplier<T> supplier) {
        ConstructType<T> constructType = new ConstructType<>(identifier, supplier);
        REGISTRY.put(identifier, constructType);
        return constructType;
    }

    /**
     * Create an empty set of construct types
     *
     * @return The construct types
     */
    public static Map<ConstructType<?>, ConstructComponent<?>> getEmpty() {
        return REGISTRY.values().stream().collect(Collectors.toMap(
                x -> x,
                x -> x.supplier().get()
        ));
    }

}
