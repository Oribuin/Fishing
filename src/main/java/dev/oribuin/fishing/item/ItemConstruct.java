package dev.oribuin.fishing.item;

import dev.oribuin.fishing.item.component.EdibleConstructType;
import dev.oribuin.fishing.item.component.EnchantConstructType;
import dev.oribuin.fishing.item.component.ModelConstructType;
import dev.oribuin.fishing.item.component.TextureConstructType;
import dev.oribuin.fishing.item.component.TooltipConstructType;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
@SuppressWarnings({ "unused", "FieldMayBeFinal" })
public class ItemConstruct {

    private Material type;
    private Integer amount;
    private String name;
    private List<String> lore;
    private EnchantConstructType enchantments;
    private TextureConstructType texture;
    private EdibleConstructType edible;
    private TooltipConstructType tooltip;
    private ModelConstructType model;
    private Boolean glowing;

    public ItemConstruct() {
        this(Material.STONE);
    }

    public ItemConstruct(Material type) {
        this.type = type;
        this.amount = 1;
        this.name = null;
        this.lore = new ArrayList<>();
        this.enchantments = null;
        this.texture = null;
        this.edible = null;
        this.tooltip = null;
        this.model = null;
        this.glowing = null;
    }

    public ItemStack build() {
        return this.build(Placeholders.empty());
    }

    @SuppressWarnings({ "UnstableApiUsage" })
    public ItemStack build(Placeholders placeholders) {
        ItemStack stack = new ItemStack(this.type != null ? this.type : Material.STONE, this.amount);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack; // Probably air

        if (this.name != null) stack.setData(DataComponentTypes.CUSTOM_NAME, FishUtils.kyorify(this.name, placeholders));
        if (this.lore != null) {
            List<Component> lines = new ArrayList<>();
            for (String line : this.lore) {
                Component text = FishUtils.kyorify(line, placeholders);
                String content = FishUtils.MINIMESSAGE.serialize(text);
                String[] newLine = content.split("(<newline>|<br>)");
                for (String s : newLine) lines.add(FishUtils.kyorify(s));
            }

            stack.setData(DataComponentTypes.LORE, ItemLore.lore(lines));
        }

        if (this.amount != null) stack.setAmount(this.amount);
        if (this.glowing != null) stack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, this.glowing);
        if (this.tooltip != null) this.tooltip.apply(stack);
        if (this.edible != null) this.edible.apply(stack);
        if (this.texture != null) this.texture.apply(stack);
        if (this.enchantments != null) this.enchantments.apply(stack);
        if (this.model != null) this.model.apply(stack);
        return stack;
    }

    public Material getType() {
        return type;
    }

    public ItemConstruct setType(Material type) {
        this.type = type;
        return this;
    }

    public Integer getAmount() {
        return amount;
    }

    public ItemConstruct setAmount(Integer amount) {
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

    public ItemConstruct setLore(String... lore) {
        this.lore = List.of(lore);
        return this;
    }


    public ItemConstruct setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public EnchantConstructType getEnchantments() {
        return enchantments;
    }

    public ItemConstruct setEnchantments(EnchantConstructType enchantments) {
        this.enchantments = enchantments;
        return this;
    }
    
    public TextureConstructType getTexture() {
        return texture;
    }

    public ItemConstruct setTexture(TextureConstructType texture) {
        this.texture = texture;
        return this;
    }

    public EdibleConstructType getEdible() {
        return edible;
    }

    public ItemConstruct setEdible(EdibleConstructType edible) {
        this.edible = edible;
        return this;
    }

    public TooltipConstructType getTooltip() {
        return tooltip;
    }

    public ItemConstruct setTooltip(TooltipConstructType tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public ModelConstructType getModel() {
        return model;
    }

    public ItemConstruct setModel(ModelConstructType model) {
        this.model = model;
        return this;
    }

    public Boolean getGlowing() {
        return glowing;
    }

    public ItemConstruct setGlowing(Boolean glowing) {
        this.glowing = glowing;
        return this;
    }
}
