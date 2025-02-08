package dev.oribuin.fishing.model.economy;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.economy.impl.EntropyCurrency;
import dev.oribuin.fishing.model.economy.impl.FishExpCurrency;
import dev.oribuin.fishing.model.economy.impl.ItemStackCurrency;
import dev.oribuin.fishing.model.economy.impl.PlayerExpCurrency;
import dev.oribuin.fishing.model.economy.impl.SkillpointCurrency;
import dev.oribuin.fishing.model.economy.impl.VaultCurrency;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * All the registered currency type in the fishing plugin
 *
 * @see Currency
 * @see Cost
 */
public class CurrencyRegistry {

    private static final Map<Class<? extends Currency<?>>, Currency<?>> registered = new HashMap<>();
    public static final Currency<Integer> ENTROPY = register(EntropyCurrency.class);
    public static final Currency<Integer> FISH_EXP = register(FishExpCurrency.class);
    public static final Currency<Integer> PLAYER_EXP = register(PlayerExpCurrency.class);
    public static final Currency<Integer> SKILLPOINTS = register(SkillpointCurrency.class);
    public static final Currency<Double> VAULT = register(VaultCurrency.class);
    public static final Currency<ItemStack> ITEM_STACK = register(ItemStackCurrency.class);

    /**
     * Register a currency type in the fishing plugin
     *
     * @param currency The currency to register
     *
     * @return The currency instance
     */
    @NotNull
    public static <T extends Currency<?>> T register(@NotNull Class<T> currency) {
        try {
            T instance = currency.getDeclaredConstructor().newInstance();
            registered.put(currency, instance);
            return instance;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to register currency: " + currency.getSimpleName() + ". Error: " + e.getMessage());
        }
    }

    /**
     * Register a currency type in the fishing plugin
     *
     * @param currency The currency to register
     *
     * @return The currency instance
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> Currency<T> register(@NotNull Currency<T> currency) {
        registered.put((Class<? extends Currency<?>>) currency.getClass(), currency);
        return currency;
    }

    /**
     * Get a currency instance from the registered currencies
     *
     * @param currency The currency class to get
     *
     * @return The currency instance or null if not found
     */
    public static Currency<?> get(Class<? extends Currency<?>> currency) {
        return registered.get(currency);
    }

    /**
     * Get a currency instance from the currency 'name'
     *
     * @param name The currency name to get
     *
     * @return The currency instance or null if not found
     */
    @Nullable
    public static Currency<?> get(@Nullable String name) {
        if (name == null) return null;

        for (Currency<?> currency : registered.values()) {
            if (currency.name().equalsIgnoreCase(name)) return currency;
        }

        return null;
    }

    /**
     * Get a currency instance from the currency 'name'
     *
     * @param name            The currency name to get
     * @param defaultCurrency The currency to return if the currency is not found
     *
     * @return The currency instance or the default currency if not found
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> Currency<T> getOrDefault(@Nullable String name, @NotNull Currency<T> defaultCurrency) {
        Currency<T> currency = (Currency<T>) get(name);
        return currency == null ? defaultCurrency : currency;
    }

}
