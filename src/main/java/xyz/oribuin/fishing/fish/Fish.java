package xyz.oribuin.fishing.fish;

import dev.rosewood.rosegarden.utils.HexUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.fish.condition.Time;
import xyz.oribuin.fishing.fish.condition.Weather;
import xyz.oribuin.fishing.manager.TierManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fish {

    private final String name; // The name of the fish
    private final String tier; // The tier of the fish
    private List<Biome> biomes; // The biomes the fish can be caught in
    private Weather weather; // The weather the fish can be caught in
    private Time time; // The time the fish can be caught
    private String displayName; // The display name of the fish
    private List<String> description; // The description of the fish
    private int modelData; // The model data of the fish
    private ItemStack itemStack; // The item stack of the fish

    // Static Variables
    public static NamespacedKey TYPE_KEY = new NamespacedKey(FishingPlugin.get(), "type");
    public static NamespacedKey TIER_KEY = new NamespacedKey(FishingPlugin.get(), "tier");

    /**
     * Create a new name of fish with a name and quality
     *
     * @param name The name of the fish
     * @param tier The quality of the fish
     */
    public Fish(String name, String tier) {
        Objects.requireNonNull(name, "Fish name cannot be null.");
        Objects.requireNonNull(tier, "Fish tier cannot be null.");

        this.name = name;
        this.tier = tier;
        this.biomes = new ArrayList<>();
        this.time = Time.ALL_DAY;
        this.weather = null; // No Weather Requirement
        this.displayName = StringUtils.capitalize(name.toLowerCase().replace("_", " "));
        this.description = new ArrayList<>();
        this.modelData = -1;
    }

    /**
     * Check if a player can catch the fish
     *
     * @param player The player to check
     * @param rod    The fishing rod the player is using
     * @param hook   The fishhook the player is using
     *
     * @return True if the player can catch the fish
     */
    public boolean canCatch(Player player, ItemStack rod, FishHook hook) {
        // TODO: Respect augments on the fishing rod
        // TODO: Check for additional conditions within the fish

        World world = player.getWorld();
        Location hookPosition = hook.getLocation().clone();

        // Check if the time is correct
        if (!this.time.isTime(world)) return false;

        // Check if the weather is correct
        if (this.weather != null && Weather.test(hookPosition) != this.weather) return false;

        // Check if the biome is correct
        return this.biomes.isEmpty() || this.biomes.contains(world.getBiome(hookPosition));
    }

    /**
     * Create and obtain the itemstack of the fish, We only want to cache the itemstack if it's been used
     *
     * @return The item stack of the fish
     */
    public ItemStack getItemStack() {
        if (this.itemStack != null)
            return this.itemStack.clone();

        // Get the tier of the fish
        Tier fishTier = FishingPlugin.get().getManager(TierManager.class).get(this.tier);
        if (fishTier == null) return null;

        // Add all the information to the item stack
        ItemStack itemStack = fishTier.getBaseDisplay().clone();
        itemStack.editMeta(itemMeta -> {
            itemMeta.displayName(Component.text(HexUtils.colorify(this.displayName)));
            itemMeta.setCustomModelData(this.modelData);
            // TODO: Set the lore of the item

            // fish data :-)
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            container.set(TYPE_KEY, PersistentDataType.STRING, this.name);
            container.set(TIER_KEY, PersistentDataType.STRING, this.tier);
        });

        return itemStack.clone(); // Clone the item stack to prevent any changes
    }

    /**
     * Obtain the tier of the fish based on the tier name
     *
     * @return The tier of the fish
     */
    public Tier tier() {
        return FishingPlugin.get().getManager(TierManager.class).get(this.tier);
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public String getName() {
        return name;
    }

    public String getTier() {
        return tier;
    }

    public List<Biome> getBiomes() {
        return biomes;
    }

    public void setBiomes(List<Biome> biomes) {
        this.biomes = biomes;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
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

    public int getModelData() {
        return modelData;
    }

    public void setModelData(int modelData) {
        this.modelData = modelData;
    }

}
