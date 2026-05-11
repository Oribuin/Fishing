package dev.oribuin.fishing.config.serializer;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class AttributeSerializer extends ScalarSerializer<Attribute> {

    private static final RegistryAccess REGISTRY = RegistryAccess.registryAccess();
    private static final AttributeSerializer INSTANCE = new AttributeSerializer();

    protected AttributeSerializer() {
        super(Attribute.class);
    }

    @Override
    public Attribute deserialize(Type type, Object obj) throws SerializationException {
        String name = obj.toString();
        Registry<Attribute> attributes = REGISTRY.getRegistry(RegistryKey.ATTRIBUTE);
        NamespacedKey key = NamespacedKey.fromString(name);
        if (key == null) return null;

        return attributes.get(key);
    }

    @Override
    protected Object serialize(Attribute item, Predicate<Class<?>> typeSupported) {
        return item.key().value();
    }

    public static AttributeSerializer getInstance() {
        return INSTANCE;
    }

}