package dev.oribuin.fishing.config.item.component;

import dev.oribuin.fishing.config.item.ConstructComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import static io.papermc.paper.datacomponent.DataComponentTypes.ITEM_MODEL;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal", "UnstableApiUsage" })
public final class ModelItemType extends ConstructComponent<NamespacedKey> {

    private String value;

    public ModelItemType() {
        this(null);
    }

    public ModelItemType(String value) {
        this.value = value;
    }

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @Nullable NamespacedKey establish() {
        if (this.value == null) return null;

        return NamespacedKey.fromString(value);
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        NamespacedKey established = this.establish();
        if (established == null || !this.enabled) return;

        stack.setData(ITEM_MODEL, established);
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(ITEM_MODEL);
    }

    public String getValue() {
        return value;
    }

    public ModelItemType setValue(String value) {
        this.value = value;
        return this;
    }
}