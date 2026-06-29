package dev.oribuin.fishing.config.item.component;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.item.ConstructComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

@ConfigSerializable
@SuppressWarnings({ "UnstableApiUsage", "FieldMayBeFinal" })
public class AttributeConstructType implements ConstructComponent<ItemAttributeModifiers> {

    private final Map<Attribute, AttributeType> values;

    public AttributeConstructType() {
        this(null);
    }

    public AttributeConstructType(Map<Attribute, AttributeType> values) {
        this.values = values;
    }

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @Nullable ItemAttributeModifiers establish() {
        if (this.values == null || this.values.isEmpty()) return null;
        NamespacedKey namespacedKey = NamespacedKey.fromString("item_construct", FishingPlugin.get());
        if (namespacedKey == null) return null;

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
        for (Map.Entry<Attribute, AttributeType> entry : this.values.entrySet()) {
            builder.addModifier(entry.getKey(), new AttributeModifier(
                    namespacedKey,
                    entry.getValue().getAmount(),
                    entry.getValue().getOperation(),
                    entry.getValue().getSlot()
            ), entry.getValue().getSlot());
        }

        return builder.build();
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        ItemAttributeModifiers modifiers = this.establish();
        if (modifiers != null) {
            stack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, modifiers);
        }
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
    }

    @ConfigSerializable
    @SuppressWarnings({ "FieldMayBeFinal" })
    public static class AttributeType {
        private AttributeModifier.Operation operation;
        private double amount;
        private EquipmentSlotGroup slot;

        public AttributeModifier.Operation getOperation() {
            return operation;
        }

        public double getAmount() {
            return amount;
        }

        public EquipmentSlotGroup getSlot() {
            return slot;
        }
    }

}
