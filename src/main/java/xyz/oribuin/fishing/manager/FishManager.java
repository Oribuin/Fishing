package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.block.Biome;
import xyz.oribuin.fishing.fish.Fish;
import xyz.oribuin.fishing.fish.Tier;
import xyz.oribuin.fishing.fish.condition.Time;
import xyz.oribuin.fishing.fish.condition.Weather;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.HashMap;
import java.util.Map;

public class FishManager extends Manager {

    private final Map<String, Fish> fishTypes = new HashMap<>();

    public FishManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.rosePlugin.getManager(TierManager.class)
                .getQualityTypes()
                .forEach((s, tier) -> {
                    CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(FishUtils.createFile(this.rosePlugin, tier.getTierFile()));
                    CommentedConfigurationSection section = config.getConfigurationSection("fish");

                    // Make sure the section is not null
                    if (section == null) {
                        this.rosePlugin.getLogger().warning("Failed to load fish in tier: " + tier.getName());
                        return;
                    }

                    // Load all the fish from the config
                    section.getKeys(false).forEach(key -> {
                        Fish fish = this.load(config, tier, key);
                        if (fish != null) this.fishTypes.put(key, fish);
                    });
                });
    }


    /**
     * Load a fish from the tier config file
     *
     * @param config The config file
     * @param tier   The tier of the fish
     * @param key    The key of the fish
     *
     * @return The fish
     */
    public Fish load(CommentedFileConfiguration config, Tier tier, String key) {
        String path = ("fish." + key + ".").toLowerCase();
        String name = config.getString(path + "name");

        // Make sure the name is not null
        if (name == null) {
            this.rosePlugin.getLogger().warning("Failed to load fish with key: " + key + " in tier: " + tier.getName());
            return null;
        }

        // Load additional values from the config
        Fish fish = new Fish(name, tier.getName());

        // Catch Conditions
        fish.setDisplayName(config.getString(path + "display-name", name));
        fish.setDescription(config.getStringList(path + "description"));
        fish.setModelData(config.getInt(path + "model-data", -1));

        // Catch Conditions
        fish.setBiomes(FishUtils.getEnumList(Biome.class, config.getStringList(path + "biomes")));
        fish.setWeather(FishUtils.getEnum(Weather.class, config.getString(path + "weather")));
        fish.setTime(FishUtils.getEnum(Time.class, config.getString(path + "time")));
        return fish;
    }

    @Override
    public void disable() {

    }

    public Map<String, Fish> getFishTypes() {
        return fishTypes;
    }

}
