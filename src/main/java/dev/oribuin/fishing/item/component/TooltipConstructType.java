package dev.oribuin.fishing.item.component;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.item.ConstructComponent;
import dev.oribuin.fishing.util.FishUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ConfigSerializable
@SuppressWarnings({ "UnstableApiUsage", "FieldMayBeFinal" })
public class TooltipConstructType implements ConstructComponent<TooltipDisplay> {

    public static Registry<@NotNull DataComponentType> TYPE_REGISTRY = FishUtils.REGISTRY.getRegistry(RegistryKey.DATA_COMPONENT_TYPE);

    private Boolean visible = null;
    private List<String> hiddenComponents = null;

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @Nullable TooltipDisplay establish() {
        Set<DataComponentType> types = new HashSet<>();
        if (this.hiddenComponents != null && !this.hiddenComponents.isEmpty()) {
            this.hiddenComponents.forEach(s -> {
                DataComponentType type = this.from(s.toLowerCase());
                if (type != null) types.add(type);
                else FishingPlugin.get().getLogger().warning("Failed to match data component type [" + s + "]");
            });
        }

        return TooltipDisplay.tooltipDisplay()
                .hideTooltip(this.visible != null && !this.visible)
                .hiddenComponents(types)
                .build();
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        TooltipDisplay display = this.establish();
        if (display == null) return;
        
        System.out.println("Applying: " + display);
        stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, display);
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(DataComponentTypes.TOOLTIP_DISPLAY);
    }

    /**
     * Match a data component type from a string
     *
     * @param type The data component type name
     *
     * @return The matched {@link DataComponentType} or null
     */
    @Nullable
    private DataComponentType from(@Subst("n/a") @NotNull String type) {
        return TYPE_REGISTRY.get(Key.key(type));
    }

    public Boolean getVisible() {
        return visible;
    }

    public TooltipConstructType setVisible(Boolean visible) {
        this.visible = visible;
        return this;
    }

    public List<String> getHiddenComponents() {
        return hiddenComponents;
    }

    public TooltipConstructType setHiddenComponents(List<String> hiddenComponents) {
        this.hiddenComponents = hiddenComponents;
        return this;
    }
}
