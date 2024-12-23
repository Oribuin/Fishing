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
     * Serialize the settings of the configurable class into a {@link CommentedConfigurationSection} to be saved later
     * <p>
     * This functionality will not update the configuration file, it will only save the settings into the section to be saved later.
     * <p>
     * The function {@link #reload()} will save the settings on first load, please override this method if you wish to save the settings regularly
     * New sections should be created using {@link #pullSection(CommentedConfigurationSection, String)}
     *
     * @param config The {@link CommentedConfigurationSection} to save the settings to, this cannot be null.
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("texture", this.texture);
    }

    /**
     * Initialize a {@link CommentedConfigurationSection} from a configuration file to establish the settings
     * for the configurable class, will be automatically called when the configuration file is loaded using {@link #reload()}
     * <p>
     * If your class inherits from another configurable class, make sure to call super.loadSettings(config)
     * to save the settings from the parent class
     * <p>
     * A class must be initialized before settings are loaded, If you wish to have a configurable data class style, its best to create a
     * static method that will create a new instance and call this method on the new instance
     * <p>
     * The {@link CommentedConfigurationSection} should never be null, when creating a new section,
     * use {@link #pullSection(CommentedConfigurationSection, String)} to establish new section if it doesn't exist
     *
     * @param config The {@link CommentedConfigurationSection} to load the settings from, this cannot be null.
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