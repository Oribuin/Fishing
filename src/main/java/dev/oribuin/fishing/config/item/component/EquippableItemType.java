package dev.oribuin.fishing.config.item.component;

import dev.oribuin.fishing.config.item.ConstructComponent;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import static io.papermc.paper.datacomponent.DataComponentTypes.EQUIPPABLE;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal", "UnstableApiUsage" })
public class EquippableItemType extends ConstructComponent<Equippable> {

    private EquipmentSlot slot; // The slot the item can be equipped in
    private boolean swappable; // Whether the item can be swapped with another item in the same slot
    private boolean damageable; // Whether the item can take damage
    private boolean dispensable; // Whether the item can be dispensed

    /**
     * Create a new equippable stack with default properties for the item
     */
    public EquippableItemType() {
        this.slot = EquipmentSlot.HAND;
        this.swappable = true;
        this.damageable = true;
        this.dispensable = true;
    }

    /**
     * Create a new equippable stack with the specified properties for the item
     *
     * @param slot        The slot the item can be equipped in
     * @param swappable   Whether the item can be swapped with another item in the same slot
     * @param damageable  Whether the item can take damage
     * @param dispensable Whether the item can be dispensed
     */
    public EquippableItemType(EquipmentSlot slot, boolean swappable, boolean damageable, boolean dispensable) {
        this.slot = slot;
        this.swappable = swappable;
        this.damageable = damageable;
        this.dispensable = dispensable;
    }

    /**
     * Create a new equippable stack from an equippable component
     *
     * @param component The equippable component
     *
     * @return The equippable stack
     */
    public static EquippableItemType fromComponent(Equippable component) {
        if (component == null) return null;

        return new EquippableItemType(
                component.slot(),
                component.swappable(),
                component.damageOnHurt(),
                component.dispensable()
        );
    }


    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @Nullable Equippable establish() {
        return Equippable.equippable(this.slot)
                .swappable(this.swappable)
                .damageOnHurt(this.damageable)
                .dispensable(this.dispensable)
                .build();
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        Equippable established = this.establish();
        if (established == null || !this.enabled) return;

        stack.setData(EQUIPPABLE, established);
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(EQUIPPABLE);
    }

}
