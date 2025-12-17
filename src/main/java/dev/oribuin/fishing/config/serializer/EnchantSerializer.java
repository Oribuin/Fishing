package dev.oribuin.fishing.config.serializer;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class EnchantSerializer extends ScalarSerializer<Enchantment> {

    private static final RegistryAccess REGISTRY = RegistryAccess.registryAccess();
    private static final EnchantSerializer INSTANCE = new EnchantSerializer();

    private EnchantSerializer() {
        super(Enchantment.class);
    }

    @Override
    public Enchantment deserialize(Type type, Object obj) throws SerializationException {
        String name = obj.toString();
        Registry<@NotNull Enchantment> enchants = REGISTRY.getRegistry(RegistryKey.ENCHANTMENT);
        NamespacedKey key = NamespacedKey.fromString(name);
        if (key == null) return null;

        return enchants.get(key);
    }

    @Override
    protected Object serialize(Enchantment item, Predicate<Class<?>> typeSupported) {
        return item.key().asString();
    }

    public static EnchantSerializer getInstance() {
        return INSTANCE;
    }

}
