package dev.oribuin.fishing.config.item.component;

import dev.oribuin.fishing.config.item.ConstructComponent;
import dev.oribuin.fishing.config.item.ItemConstruct;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

import static io.papermc.paper.datacomponent.DataComponentTypes.ENCHANTMENTS;
import static io.papermc.paper.datacomponent.DataComponentTypes.STORED_ENCHANTMENTS;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class EnchantItemType extends ConstructComponent<ItemEnchantments> {

    private static final Registry<Enchantment> REGISTRY = ItemConstruct.getRegistry().getRegistry(RegistryKey.ENCHANTMENT);

    private Map<String, Integer> enchantments;
    private boolean stored;

    /**
     * Create a new EnchantedStack object with default properties
     */
    public EnchantItemType() {
        this.enchantments = new HashMap<>();
        this.stored = false;
    }

    /**
     * Create a new EnchantedStack object with enchantments and tooltip
     *
     * @param enchantments The enchantments to apply
     */
    public EnchantItemType(Map<Enchantment, Integer> enchantments) {
        this.enchantments = new HashMap<>();

        enchantments.forEach((enchantment, integer) -> {
            // Add the enchantment to the map
            this.enchantments.put(enchantment.getKey().getNamespace(), integer);
        });
    }

    /**
     * Create a new EnchantedStack object with an enchantment and level
     *
     * @param enchantment The enchantment to apply
     * @param level       The level of the enchantment
     */
    public EnchantItemType(Enchantment enchantment, int level) {
        this.enchantments = new HashMap<>();
        this.enchantments.put(enchantment.getKey().getKey(), level);
    }

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @Nullable ItemEnchantments establish() {
        Map<Enchantment, Integer> result = new HashMap<>();
        this.enchantments.forEach((namespace, integer) -> {
            NamespacedKey namespacedKey = NamespacedKey.fromString(namespace);
            if (namespacedKey == null) return;

            Enchantment enchantment = REGISTRY.get(namespacedKey);
            if (enchantment == null) return;

            result.put(enchantment, integer);
        });

        return ItemEnchantments.itemEnchantments(result);
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        ItemEnchantments established = this.establish();
        if (established == null || !this.enabled) return;

        stack.setData(this.stored ? STORED_ENCHANTMENTS : ENCHANTMENTS, established);
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(this.stored ? STORED_ENCHANTMENTS : ENCHANTMENTS);
    }

    public Map<String, Integer> getEnchantments() {
        return enchantments;
    }

    public EnchantItemType setEnchantments(Map<String, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    public boolean isStored() {
        return stored;
    }

    public EnchantItemType setStored(boolean stored) {
        this.stored = stored;
        return this;
    }
}
    
    

