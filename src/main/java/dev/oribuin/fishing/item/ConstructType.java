package dev.oribuin.fishing.item;

import dev.oribuin.fishing.item.component.AttributeConstructType;
import dev.oribuin.fishing.item.component.DyedConstructType;
import dev.oribuin.fishing.item.component.EdibleConstructType;
import dev.oribuin.fishing.item.component.EnchantConstructType;
import dev.oribuin.fishing.item.component.ModelConstructType;
import dev.oribuin.fishing.item.component.TextureConstructType;
import dev.oribuin.fishing.item.component.TooltipConstructType;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public enum ConstructType {
    ATTRIBUTE(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeConstructType::new),
    DYED(DataComponentTypes.DYED_COLOR, DyedConstructType::new),
    EDIBLE(DataComponentTypes.FOOD, EdibleConstructType::new),
    ENCHANT(DataComponentTypes.ENCHANTMENTS, EnchantConstructType::new),
    MODEL(DataComponentTypes.ITEM_MODEL, ModelConstructType::new),
    TEXTURE(DataComponentTypes.PROFILE, TextureConstructType::new),
    TOOLTIP(DataComponentTypes.TOOLTIP_DISPLAY, TooltipConstructType::new),
    ;

    private final DataComponentType type;
    private final Supplier<ConstructComponent<?>> supplier;

    ConstructType(DataComponentType type, Supplier<ConstructComponent<?>> supplier) {
        this.type = type;
        this.supplier = supplier;
    }

    /**
     * Get a null map from all construct types
     *
     * @return The null map
     */
    public static Map<ConstructType, ConstructComponent<?>> getNullMap() {
        Map<ConstructType, ConstructComponent<?>> results = new HashMap<>();
        for (ConstructType component : ConstructType.values()) results.put(component, null);
        return results;
    }

    public void apply(ItemStack stack) {
        ConstructComponent<?> component = this.supplier.get();
        if (component != null) component.apply(stack);
    }

    public static void apply(Collection<ConstructComponent<?>> components, ItemStack stack) {
        for (ConstructComponent<?> component : components) {
            if (component != null) component.apply(stack);
        }
    }

    public void clear(ItemStack stack) {
        ConstructComponent<?> component = this.supplier.get();
        if (component != null) component.clear(stack);
    }

    public static void clear(Collection<ConstructComponent<?>> components, ItemStack stack) {
        for (ConstructComponent<?> component : components) {
            if (component != null) component.apply(stack);
        }
    }

    public DataComponentType getType() {
        return type;
    }

    public Supplier<ConstructComponent<?>> getSupplier() {
        return supplier;
    }

}
