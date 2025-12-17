package dev.oribuin.fishing.config.serializer;

import dev.oribuin.fishing.util.FishUtils;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class ComponentSerializer extends ScalarSerializer<Component> {

    private static final ComponentSerializer INSTANCE = new ComponentSerializer();

    private ComponentSerializer() {
        super(Component.class);
    }

    @Override
    public Component deserialize(Type type, Object obj) throws SerializationException {
        return FishUtils.LEGACY_SERIALIZER.deserialize(obj.toString());
    }

    @Override
    protected Object serialize(Component item, Predicate<Class<?>> typeSupported) {
        return FishUtils.MINIMESSAGE.serialize(item);
    }

    public static ComponentSerializer getInstance() {
        return INSTANCE;
    }

}
