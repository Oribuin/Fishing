package dev.oribuin.fishing.config.item;

import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.registry.RegistryAccess;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static io.papermc.paper.datacomponent.DataComponentTypes.*;


/**
 * ComponentStack is a class that represents a stack of items with additional properties.
 * Utilizes Paper Component API to create custom items.
 */
@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal", "UnstableApiUsage" })
public class ItemConstruct implements Cloneable {

    private static final RegistryAccess registry = RegistryAccess.registryAccess();

    private Material material;
    private int amount;
    private String name;
    private List<String> lore;
    private Integer maxStackSize;
    private Integer customModelData;
    private Map<String, ConstructComponent<?>> properties;
    private transient Consumer<ItemStack> function;

    public ItemConstruct() {
        this.material = Material.STONE;
        this.amount = 1;
        this.name = null;
        this.lore = null;
        this.maxStackSize = null;
        this.customModelData = null;
        this.properties = new HashMap<>();
        this.function = null;
    }

    /**
     * Create a new {@link ItemConstruct} object with the specified material.
     *
     * @param material The material of the item
     *
     * @return The created ComponentStack object
     */
    @NotNull
    public static ItemConstruct of(@NotNull Material material) {
        return new ItemConstruct().setMaterial(material);
    }

    /**
     * Create a new {@link ItemStack} with the properties of this ComponentStack object.
     **
     * @return The created ItemStack
     */
    @NotNull
    public ItemStack create() {
        return this.create(null, Placeholders.empty());
    }

    /**
     * Create a new {@link ItemStack} with the properties of this ComponentStack object.
     *
     * @param placeholders The placeholders to replace in the item's name and lore
     *
     * @return The created ItemStack
     */
    @NotNull
    public ItemStack create(Placeholders placeholders) {
        return this.create(null, placeholders);
    }

    /**
     * Create a new {@link ItemStack} with the properties of this ComponentStack object.
     *
     * @param consumer Additional functionality for the gadget
     *
     * @return The created ItemStack
     */
    public @NotNull ItemStack createCustom(Consumer<ItemStack> consumer) {
        return createCustom(null, Placeholders.empty(), consumer);
    }

    /**
     * Create a new {@link ItemStack} with the properties of this ComponentStack object.
     *
     * @param consumer     Additional functionality for the gadget
     * @param placeholders The placeholders to replace in the item's name and lore
     *
     * @return The created ItemStack
     */
    public @NotNull ItemStack createCustom(Placeholders placeholders, Consumer<ItemStack> consumer) {
        return createCustom(null, placeholders, consumer);
    }

    /**
     * Create a new {@link ItemStack} with the properties of this ComponentStack object.
     *
     * @param base         The base ItemStack to modify with the properties of this ComponentStack
     * @param consumer     Additional functionality for the gadget
     * @param placeholders The placeholders to replace in the item's name and lore
     *
     * @return The created ItemStack
     */
    public @NotNull ItemStack createCustom(@Nullable ItemStack base, Placeholders placeholders, Consumer<ItemStack> consumer) {
        ItemStack stack = create(base, placeholders);
        consumer.accept(stack);
        return stack;
    }

    /**
     * Create a new {@link ItemStack} with the properties of this ComponentStack object.
     *
     * @param base         The base ItemStack to modify with the properties of this ComponentStack
     * @param placeholders The placeholders to replace in the item's name and lore
     *
     * @return The created ItemStack
     */
    @NotNull
    public ItemStack create(@Nullable ItemStack base, Placeholders placeholders) {
        ItemStack item = base != null ? base.clone() : new ItemStack(this.material);

        if (this.name != null) {
            Component customName = parse(this.name, placeholders);
            item.setData(ITEM_NAME, customName);
            item.setData(CUSTOM_NAME, customName);
        }
        
        if (this.lore != null) {
            List<Component> lines = new ArrayList<>();
            for (String line : this.lore) {
                Component text = FishUtils.kyorify(line, placeholders);
                String content = MiniMessage.miniMessage().serialize(text);
                String[] newLine = content.split("(<newline>|<br>)");
                for (String s : newLine) lines.add(FishUtils.kyorify(s));
            }

            item.setData(DataComponentTypes.LORE, ItemLore.lore(lines));
        }
        if (this.maxStackSize != null) item.setData(MAX_STACK_SIZE, maxStackSize);
        if (this.customModelData != null && this.customModelData > 0) item.setData(CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFloat(this.customModelData));
        if (this.function != null) this.function.accept(item);

        this.properties.values().forEach(x -> x.apply(item));
        return item.asQuantity(Math.max(1, amount));
    }


    /**
     * Get the paper registry access object
     *
     * @return The registry access object
     */
    public static RegistryAccess getRegistry() {
        return registry;
    }

    /**
     * Parse a string with placeholders
     *
     * @param text         The text to parse
     * @param placeholders The placeholders to replace in the text
     *
     * @return The parsed text
     */
    private static Component parse(String text, Placeholders placeholders) {
        return FishUtils.kyorify(text, placeholders);
    }

    /**
     * Get the data of a specific type from an ItemStack
     *
     * @param stack The ItemStack to get the data from
     * @param type  The type of data to get
     *
     * @return The data if it exists
     */
    public static Optional<Boolean> getData(ItemStack stack, DataComponentType.NonValued type) {
        return Optional.of(stack.hasData(type));
    }

    /**
     * Get the data of a specific type from an ItemStack
     *
     * @param stack The ItemStack to get the data from
     * @param type  The type of data to get
     * @param <T>   The type of data
     *
     * @return The data if it exists
     */
    private static <T> @NotNull Optional<T> getData(ItemStack stack, DataComponentType.@NotNull Valued<T> type) {
        return Optional.ofNullable(stack.getData(type));
    }

    /**
     * Set a property within the item construct
     *
     * @param type     The type tos et
     * @param consumer The consumer for the function
     * @param <T>      The construct type to set
     *
     * @return The resulting item construct
     */
    @SuppressWarnings("unchecked")
    public <T extends ConstructComponent<?>> ItemConstruct setProperty(ConstructType<T> type, Consumer<T> consumer) {
        T property = (T) this.properties.getOrDefault(type.identifier(), type.supplier().get());
        if (property != null) {
            consumer.accept(property);
            this.properties.put(type.identifier(), property);
        }

        return this;
    }

    public MenuItem asMenuItem(Integer... slots) {
        return new MenuItem(this, slots);
    }

    @Override
    public ItemConstruct clone() {
        try {
            ItemConstruct clone = (ItemConstruct) super.clone();
            clone.setMaterial(this.material);
            clone.setAmount(this.amount);
            clone.setName(this.name);
            clone.setLore(this.lore);
            clone.setMaxStackSize(this.maxStackSize);
            clone.setCustomModelData(this.customModelData);
            clone.setProperties(this.properties);
            clone.setFunction(this.function);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public Material getMaterial() {
        return material;
    }

    public ItemConstruct setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public ItemConstruct setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public String getName() {
        return name;
    }

    public ItemConstruct setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getLore() {
        return lore;
    }

    public ItemConstruct setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemConstruct setLore(String... lore) {
        this.lore = List.of(lore);
        return this;
    }

    public Integer getMaxStackSize() {
        return maxStackSize;
    }

    public ItemConstruct setMaxStackSize(Integer maxStackSize) {
        this.maxStackSize = maxStackSize;
        return this;
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    public ItemConstruct setCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public Map<String, ConstructComponent<?>> getProperties() {
        return properties;
    }

    public ItemConstruct setProperties(Map<String, ConstructComponent<?>> properties) {
        this.properties = properties;
        return this;
    }

    public Consumer<ItemStack> getFunction() {
        return function;
    }

    public ItemConstruct setFunction(Consumer<ItemStack> function) {
        this.function = function;
        return this;
    }
}
