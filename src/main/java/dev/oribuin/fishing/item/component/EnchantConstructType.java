package dev.oribuin.fishing.item.component;

import dev.oribuin.fishing.item.ConstructComponent;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
@SuppressWarnings({ "UnstableApiUsage", "FieldMayBeFinal" })
public final class EnchantConstructType implements ConstructComponent<ItemEnchantments> {

    private final Map<Enchantment, Integer> enchantments = null;
    private transient Boolean stored = false;

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @NotNull ItemEnchantments establish() {
        return ItemEnchantments.itemEnchantments(
                this.enchantments != null // intellij knows no better, configurate makes this not null
                        ? this.enchantments
                        : new HashMap<>()
        );
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        if (this.enchantments == null || this.enchantments.isEmpty()) return;

        ItemEnchantments itemEnchantments = this.establish();
        DataComponentType.Valued<ItemEnchantments> type = this.stored == null || !this.stored
                ? DataComponentTypes.ENCHANTMENTS
                : DataComponentTypes.STORED_ENCHANTMENTS;
        stack.setData(type, itemEnchantments);
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(DataComponentTypes.ENCHANTMENTS);
    }

    public EnchantConstructType asStored() {
        this.stored = true;
        return this;
    }

}
