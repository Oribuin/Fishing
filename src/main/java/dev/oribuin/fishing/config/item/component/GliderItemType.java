package dev.oribuin.fishing.config.item.component;

import dev.oribuin.fishing.config.item.ConstructComponent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import static io.papermc.paper.datacomponent.DataComponentTypes.GLIDER;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal", "UnstableApiUsage" })
public final class GliderItemType extends ConstructComponent<Boolean> {

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public Boolean establish() {
        return this.enabled;
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        if (this.enabled) stack.setData(GLIDER);
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(GLIDER);
    }

}