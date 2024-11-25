package xyz.oribuin.fishing.util.nms;

import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

@SuppressWarnings({ "unused", "deprecation" })
public final class SkullUtils {

    private static Method method_SkullMeta_setProfile;

    private SkullUtils() {

    }

    /**
     * Applies a base64 encoded texture to an item's SkullMeta
     *
     * @param skullMeta The ItemMeta for the Skull
     * @param texture   The texture to apply to the skull
     */
    public static void setSkullTexture(SkullMeta skullMeta, String texture) {
        if (texture == null || texture.isEmpty()) return;

        // TODO: Add head database support
        //        if (texture != null && texture.startsWith("hdb:") && Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
        //            texture = new HeadDatabaseAPI().getBase64(texture.substring(4));
        //        }

        setTexturesPaper(skullMeta, texture);
    }

    /**
     * Set the texture using the paper api for 1.18+
     *
     * @param meta    The skull meta
     * @param texture The texture
     */
    private static void setTexturesPaper(SkullMeta meta, String texture) {
        if (texture == null || texture.isEmpty())
            return;

        com.destroystokyo.paper.profile.PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "");
        PlayerTextures textures = profile.getTextures();

        String decodedTextureJson = new String(Base64.getDecoder().decode(texture));
        String decodedTextureUrl = decodedTextureJson.substring(28, decodedTextureJson.length() - 4);

        try {
            textures.setSkin(new URL(decodedTextureUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        profile.setTextures(textures);
        meta.setPlayerProfile(profile);
    }

}