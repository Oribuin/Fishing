package dev.oribuin.fishing.config.item.component;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.item.ConstructComponent;
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

import static io.papermc.paper.datacomponent.DataComponentTypes.ATTRIBUTE_MODIFIERS;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal", "UnstableApiUsage" })
public class AttributeItemType extends ConstructComponent<ItemAttributeModifiers> {

    private final Map<Attribute, AttributeType> values;

    public AttributeItemType() {
        this(null);
    }

    public AttributeItemType(Map<Attribute, AttributeType> values) {
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
        ItemAttributeModifiers established = this.establish();
        if (established == null || !this.enabled) return;

        stack.setData(ATTRIBUTE_MODIFIERS, established);
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(ATTRIBUTE_MODIFIERS);
    }

    public Map<Attribute, AttributeType> getValues() {
        return values;
    }

    @ConfigSerializable
    @SuppressWarnings({ "FieldMayBeFinal" })
    public static class AttributeType {
        private AttributeModifier.Operation operation;
        private double amount;
        private EquipmentSlotGroup slot;

        public AttributeType() {
        }

        public AttributeType(AttributeModifier.Operation operation, double amount, EquipmentSlotGroup slot) {
            this.operation = operation;
            this.amount = amount;
            this.slot = slot;
        }

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
