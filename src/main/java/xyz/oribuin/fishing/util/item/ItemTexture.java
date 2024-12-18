package xyz.oribuin.fishing.util.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.config.Configurable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public final class ItemTexture implements Configurable {

    private String texture;
    /**
     * Define the player head texture of an item
     *
     * @param texture The base64 texture of the player head
     */
    public ItemTexture(String texture) {
        this.texture = texture;
    }

    /**
     * Create a new potion effect from the builder
     *
     * @return The potion effect
     */
    public ResolvableProfile create() {
        try {
            PlayerProfile playerProfile = Bukkit.createProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "");
            PlayerTextures playerTextures = playerProfile.getTextures();

            String decodedTextureJson = new String(Base64.getDecoder().decode(texture));
            String decodedTextureUrl = decodedTextureJson.substring(28, decodedTextureJson.length() - 4);

            playerTextures.setSkin(new URL(decodedTextureUrl));
            playerProfile.setTextures(playerTextures);

            return ResolvableProfile.resolvableProfile(playerProfile);
        } catch (MalformedURLException | NullPointerException ex) {
            return ResolvableProfile.resolvableProfile().build();
        }
    }

    /**
     * Create a new potion effect from the builder
     *
     * @param config The configuration section to load the potion effect from
     *
     * @return The potion effect
     */
    public static ItemTexture of(CommentedConfigurationSection config) {
        return new ItemTexture(config.getString("texture"));
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("texture", this.texture);
    }

    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.texture = config.getString("texture");
    }

    /**
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return String.format("ItemTexture{texture='%s'}", texture);
    }

    public String texture() {
        return texture;
    }

    public void texture(String texture) {
        this.texture = texture;
    }

}