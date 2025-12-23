package dev.oribuin.fishing.item.component;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.hook.plugin.HeadDbProvider;
import dev.oribuin.fishing.item.ConstructComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.DigestException;
import java.sql.SQLSyntaxErrorException;
import java.util.Base64;
import java.util.UUID;

@ConfigSerializable
@SuppressWarnings({ "UnstableApiUsage", "FieldMayBeFinal" })
public final class TextureConstructType implements ConstructComponent<ResolvableProfile> {

    private String texture;
    
    public TextureConstructType() {
        this.texture = null;
    }
    
    public TextureConstructType(String texture) {
        this.texture = texture;
    }

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @Nullable ResolvableProfile establish() {
        if (this.texture == null) return null;

        String[] type = this.texture.split("-");
        if (type.length == 1) return null;
        // todo: serialize player-<name>
        return switch (type[0].toLowerCase()) {
            case "base64" -> this.fromBase64(type[1]);
            case "hdb" -> this.fromHdb(type[1]);
            default -> null;
        };
    }

    /**
     * Apply an {@link ConstructComponent} to an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void apply(@NotNull ItemStack stack) {
        ResolvableProfile profile = this.establish();
        if (profile != null) {
            stack.setData(DataComponentTypes.PROFILE, profile);
        }
    }

    /**
     * Clear an {@link ConstructComponent} from an ItemStack
     *
     * @param stack The ItemStack to apply to
     */
    @Override
    public void clear(@NotNull ItemStack stack) {
        stack.unsetData(DataComponentTypes.PROFILE);
    }

    /**
     * Create a {@link ResolvableProfile} from a base64 texture
     *
     * @param base64 The base64 texture link
     *
     * @return The {@link ResolvableProfile} if available, empty otherwise
     */
    private ResolvableProfile fromBase64(String provided) {
        try {
            PlayerProfile playerProfile = Bukkit.createProfile(UUID.nameUUIDFromBytes(provided.getBytes()), "");
            PlayerTextures playerTextures = playerProfile.getTextures();

            String decodedTextureJson = new String(Base64.getDecoder().decode(provided));
            String decodedTextureUrl = decodedTextureJson.substring(28, decodedTextureJson.length() - 4);

            playerTextures.setSkin(new URL(decodedTextureUrl));
            playerProfile.setTextures(playerTextures);

            return ResolvableProfile.resolvableProfile(playerProfile);
        } catch (MalformedURLException | NullPointerException | IllegalArgumentException ex) {
            FishingPlugin.get().getLogger().warning("Failed to establish resolvable profile from [" + provided + "] due to: " + ex.getMessage());
            return ResolvableProfile.resolvableProfile().build();
        }
    }


    /**
     * Create a {@link ResolvableProfile} from a head database id
     *
     * @param headId The head database id
     *
     * @return The {@link ResolvableProfile} if available, empty otherwise
     */
    private ResolvableProfile fromHdb(String headId) {
        String texture = HeadDbProvider.getApi().getBase64(headId);
        if (texture != null) return this.fromBase64(headId);
        return null;
    }

}