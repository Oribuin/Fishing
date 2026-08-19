package dev.oribuin.fishing.model.loot;

import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.util.Placeholders;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Create a new fish loot item
 *
 * @param identifier The identifier for the item
 * @param construct  The item to give the player
 * @param additional Any additional functionality to apply to the item
 */
public record FishLoot(String identifier, Supplier<ItemConstruct> construct, Supplier<Placeholders> placeholders, Consumer<ItemStack> additional) {

    /**
     * Create a new fish loot item
     *
     * @param identifier The identifier for the item
     * @param construct  The item to give the player
     */
    public FishLoot(String identifier, Supplier<ItemConstruct> construct) {
        // Empty
        this(identifier, construct, Placeholders::empty, stack -> {
            // Empty
        });
    }

    /**
     * Create a new fish loot item
     *
     * @param identifier   The identifier for the item
     * @param construct    The item to give the player
     * @param placeholders The placeholders to give the player
     */
    public FishLoot(String identifier, Supplier<ItemConstruct> construct, Supplier<Placeholders> placeholders) {
        // Empty
        this(identifier, construct, placeholders, stack -> {
            // Empty
        });
    }

    /**
     * Create a new fish loot item
     *
     * @param placeholders The placeholders to apply
     *
     * @return The created itemstack
     */
    @NotNull
    public ItemStack create(@NotNull Placeholders placeholders) {

        return this.construct.get().createCustom(Placeholders.builder()
                        .addAll(this.placeholders.get())
                        .addAll(placeholders)
                        .build()
                , this.additional);
    }

    /**
     * Create a new fish loot item
     *
     * @return The created itemstack
     */
    @NotNull
    public ItemStack create() {
        return this.construct.get().createCustom(this.placeholders.get(), this.additional);
    }

}
