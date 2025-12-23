package dev.oribuin.fishing.model.fish;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.model.condition.CatchCondition;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class Fish {

    @Comment("The name of the fish (Typically the id)")
    private transient String name; // loaded from key in Tier because i got lazy
    @Comment("The display name of the fish")
    private String displayName;
    @Comment("The lore description of the fish")
    private List<String> description;
    @Comment("The list of conditions for the fish")
    private List<CatchCondition> conditions;
    @Comment("The itemstack design for the fish")
    private ItemConstruct construct;
    private transient ItemStack itemStack; // used so we're not serializing the same item over and over and over again
    private transient String tier;

    public Fish() {
        this.name = "unknown-cod";
        this.description = List.of("Example fish style");
        this.conditions = new ArrayList<>();
        this.construct = null; // inherits Tier construct
    }

    /**
     * Create a new name of fish with a name and quality
     *
     * @param name        The name of the fish
     * @param description Description about the fish
     * @param conditions  The conditions required to be met before catching the fish
     */
    public Fish(
            @NotNull String name,
            @NotNull List<String> description,
            @NotNull List<CatchCondition> conditions,
            @NotNull ItemConstruct construct
    ) {
        this.name = name;
        this.displayName = StringUtils.capitalize(name.toLowerCase().replace("_", " "));
        this.description = description;
        this.conditions = conditions;
        this.construct = construct;
    }

    /**
     * Create and obtain the itemstack of the fish, We only want to cache the itemstack if it's been used
     *
     * @return The item stack of the fish
     */
    public ItemStack buildItem() {
        if (this.itemStack != null)
            return this.itemStack.clone();

        // Get the tier of the fish
        Tier fishTier = FishingPlugin.get().getTierManager().get(this.tier);
        if (fishTier == null) return null;

        // Add all the information to the item stack
        Placeholders.Builder placeholders = Placeholders.builder();
        placeholders.addAll(this.placeholders());
        placeholders.addAll(this.getTierInstance().placeholders()); // TODO: Tier Placeholders

        ItemConstruct tierConstruct = fishTier.getItem(); // todo: merge Fish#getConstruct() -> Tier#getConstruct()
        //        if (this.construct.getName() != null)  tierConstruct.set
        ItemStack itemStack = fishTier.getItem().build(placeholders.build());
        itemStack.editMeta(itemMeta -> {
            // fish data :-)
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            container.set(KeyRegistry.FISH_TYPE, PersistentDataType.STRING, this.name);
            container.set(KeyRegistry.FISH_TYPE, PersistentDataType.STRING, this.tier);
        });

        this.itemStack = itemStack;
        return this.itemStack.clone(); // Clone the item stack to prevent any changes
    }

    @NotNull
    public Tier getTierInstance() {
        return FishingPlugin.get().getTierManager().get(this.tier);
    }

    public Placeholders placeholders() {
        Placeholders.Builder builder = Placeholders.builder()
                .add("id", this.name)
                .add("name", this.displayName)
                .add("tier", this.tier)
                .add("description", FishUtils.kyorify(String.join("<br>", this.description)));

        // Add all the placeholders from the conditions
        this.conditions.forEach(condition -> builder.addAll(condition.placeholders()));

        return builder.build();
    }

    @Override
    public String toString() {
        return "Fish{" +
               "name='" + name + '\'' +
               ", displayName='" + displayName + '\'' +
               ", description=" + description +
               ", conditions=" + conditions +
               ", construct=" + construct +
               ", itemStack=" + itemStack +
               ", tier='" + tier + '\'' +
               '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<CatchCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<CatchCondition> conditions) {
        this.conditions = conditions;
    }

    public ItemConstruct getConstruct() {
        return construct;
    }

    public void setConstruct(ItemConstruct construct) {
        this.construct = construct;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }
}
