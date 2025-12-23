package dev.oribuin.fishing.item.component;

import dev.oribuin.fishing.item.ConstructComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

@ConfigSerializable
@SuppressWarnings({ "UnstableApiUsage", "FieldMayBeFinal" })
public final class EnchantConstructType implements ConstructComponent<ItemEnchantments> {

    private final Map<Enchantment, Integer> equipped = null;
    private final Map<Enchantment, Integer> stored = null;

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @NotNull ItemEnchantments establish() {
        return ItemEnchantments.itemEnchantments().build();
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        if (this.equipped != null && !this.equipped.isEmpty()) {
            stack.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments(this.equipped));
        }

        if (this.stored != null && !this.stored.isEmpty()) {
            stack.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments(this.equipped));
        }
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(DataComponentTypes.ENCHANTMENTS);
        stack.unsetData(DataComponentTypes.STORED_ENCHANTMENTS);
    }

}
