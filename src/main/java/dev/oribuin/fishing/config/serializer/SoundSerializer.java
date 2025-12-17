package dev.oribuin.fishing.config.serializer;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

@SuppressWarnings("removal")
public class SoundSerializer extends ScalarSerializer<Sound> {

    private static final RegistryAccess REGISTRY = RegistryAccess.registryAccess();
    private static final SoundSerializer INSTANCE = new SoundSerializer();

    private SoundSerializer() {
        super(Sound.class);
    }

    @Override
    public Sound deserialize(Type type, Object obj) throws SerializationException {
        String name = obj.toString();
        Registry<Sound> sounds = REGISTRY.getRegistry(RegistryKey.SOUND_EVENT);
        NamespacedKey key = NamespacedKey.fromString(name);
        if (key == null) return null;

        return sounds.get(key);
    }

    @Override
    protected Object serialize(Sound item, Predicate<Class<?>> typeSupported) {
        return item.key().value();
    }

    public static SoundSerializer getInstance() {
        return INSTANCE;
    }

}
