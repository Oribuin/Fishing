package dev.oribuin.fishing.config.item.component;

import dev.oribuin.fishing.config.item.ConstructComponent;
import dev.oribuin.fishing.config.item.ItemConstruct;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static io.papermc.paper.datacomponent.DataComponentTypes.TOOLTIP_DISPLAY;
import static io.papermc.paper.datacomponent.DataComponentTypes.TOOLTIP_STYLE;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal", "UnstableApiUsage" })
public class TooltipItemType extends ConstructComponent<TooltipDisplay> {

    private static final Registry<DataComponentType> REGISTRY = ItemConstruct.getRegistry().getRegistry(RegistryKey.DATA_COMPONENT_TYPE);

    private boolean visible;
    private List<String> hiddenComponents;
    private String style;

    /**
     * Create a new tooltip display stack
     */
    public TooltipItemType() {
        this.visible = true;
        this.hiddenComponents = new ArrayList<>();
        this.style = null;
    }

    /**
     * Create a new tooltip display stack
     *
     * @param visible          Whether the tooltip should be visible
     * @param hiddenComponents The hidden components
     */
    public TooltipItemType(boolean visible, List<String> hiddenComponents, String style) {
        this.visible = visible;
        this.hiddenComponents = hiddenComponents;
        this.style = style;
    }

    /**
     * Create a new tooltip display stack
     *
     * @param display An existing tooltip display
     */
    public static TooltipItemType from(ItemStack stack) {
        if (stack == null) return null;

        TooltipDisplay display = stack.getData(TOOLTIP_DISPLAY);
        Key style = stack.getData(TOOLTIP_STYLE);
        List<String> components = new ArrayList<>();
        if (display != null) {
            components = display.hiddenComponents()
                    .stream()
                    .map(DataComponentType::getKey)
                    .map(NamespacedKey::asString)
                    .toList();
        }

        return new TooltipItemType(
                display != null && display.hideTooltip(),
                components,
                style != null ? style.asString() : null
        );

    }

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @NonNull TooltipDisplay establish() {
        Set<DataComponentType> hiddenTypes = this.hiddenComponents.stream()
                .map(NamespacedKey::fromString)
                .filter(Objects::nonNull)
                .map(REGISTRY::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return TooltipDisplay.tooltipDisplay()
                .hideTooltip(!this.visible)
                .hiddenComponents(hiddenTypes)
                .build();
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        if (!this.enabled) return;
        
        if (!this.visible || !this.hiddenComponents.isEmpty()) {
            stack.setData(TOOLTIP_DISPLAY, this.establish());
        }

        if (this.style != null) {
            NamespacedKey namespacedKey = NamespacedKey.fromString(this.style);
            if (namespacedKey != null) stack.setData(TOOLTIP_STYLE, namespacedKey);
        }
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(TOOLTIP_DISPLAY);
        stack.unsetData(TOOLTIP_STYLE);
    }

    public boolean isVisible() {
        return visible;
    }

    public TooltipItemType setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public List<String> getHiddenComponents() {
        return hiddenComponents;
    }

    public TooltipItemType setHiddenComponents(List<String> hiddenComponents) {
        this.hiddenComponents = hiddenComponents;
        return this;
    }
}
