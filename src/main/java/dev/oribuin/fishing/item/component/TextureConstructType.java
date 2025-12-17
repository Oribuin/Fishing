package dev.oribuin.fishing.item.component;

import com.destroystokyo.paper.profile.PlayerProfile;
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
import java.util.Base64;
import java.util.UUID;

@ConfigSerializable
@SuppressWarnings({ "UnstableApiUsage", "FieldMayBeFinal" })
public final class TextureConstructType implements ConstructComponent<ResolvableProfile> {

    private String texture = null;

    /**
     * Create a new item component type from the plugin
     *
     * @return item component type
     */
    @Override
    public @Nullable ResolvableProfile establish() {
        String[] type = this.texture.split("-");
        if (type.length == 1) return null;
        // todo: serialize player-<name>
        switch (type[0].toLowerCase()) {
            case "base64" -> this.fromBase64(type[1]);
            case "hdb" -> this.fromHdb(type[1]);
        }
        return this.fromBase64(this.texture);
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
            stack.setData(DataComponentTypes.PROFILE, this.establish());
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
    private ResolvableProfile fromBase64(String base64) {
        String texture = base64.substring(7); // remove base64-, should probably do this through split

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
     * Create a {@link ResolvableProfile} from a head database id
     *
     * @param headId The head database id
     *
     * @return The {@link ResolvableProfile} if available, empty otherwise
     */
    private ResolvableProfile fromHdb(String headId) {
        String id = headId.substring(4); // remove hdb-, should probably do this through split
        String texture = HeadDbProvider.getApi().getBase64(id);
        if (texture != null) return this.fromBase64(headId);
        return null;
    }
    
}