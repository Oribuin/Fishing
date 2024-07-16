package xyz.oribuin.fishing.util;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.manager.LocaleManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class FishUtils {

    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public FishUtils() {
        throw new IllegalStateException("VouchersUtil is a utility class and cannot be instantiated.");
    }

    /**
     * Get a bukkit color from a hex code
     *
     * @param hex The hex code
     * @return The bukkit color
     */
    public static Color fromHex(String hex) {
        if (hex == null)
            return Color.BLACK;

        java.awt.Color awtColor;
        try {
            awtColor = java.awt.Color.decode(hex);
        } catch (NumberFormatException e) {
            return Color.BLACK;
        }

        return Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player The player to format the string for
     * @param text   The string to format
     * @return The formatted string
     */
    public static String format(Player player, String text) {
        return format(player, text, StringPlaceholders.empty());
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player       The player to format the string for
     * @param text         The text to format
     * @param placeholders The placeholders to replace
     * @return The formatted string
     */
    public static String format(Player player, String text, StringPlaceholders placeholders) {
        if (text == null)
            return null;

        return HexUtils.colorify(PlaceholderAPIHook.applyPlaceholders(player, placeholders.apply(text)));
    }

    /**
     * Deserialize an ItemStack from a CommentedConfigurationSection with placeholders
     *
     * @param section      The section to deserialize from
     * @param sender       The CommandSender to apply placeholders from
     * @param key          The key to deserialize from
     * @param placeholders The placeholders to apply
     * @return The deserialized ItemStack
     */
    @Nullable
    public static ItemStack deserialize(
            @NotNull CommentedConfigurationSection section,
            @Nullable CommandSender sender,
            @NotNull String key,
            @NotNull StringPlaceholders placeholders
    ) {
        LocaleManager locale = FishingPlugin.get().getManager(LocaleManager.class);
        Material material = Material.getMaterial(locale.format(sender, section.getString(key + ".material"), placeholders), false);
        if (material == null) return null;

        // Load enchantments
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        ConfigurationSection enchantmentSection = section.getConfigurationSection(key + ".enchantments");
        if (enchantmentSection != null) {
            for (String enchantmentKey : enchantmentSection.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentKey.toLowerCase()));
                if (enchantment == null) continue;

                enchantments.put(enchantment, enchantmentSection.getInt(enchantmentKey, 1));
            }
        }

        // Load potion item flags
        ItemFlag[] flags = section.getStringList(key + ".flags").stream()
                .map(ItemFlag::valueOf)
                .toArray(ItemFlag[]::new);

        // Load offline player texture
        String owner = section.getString(key + ".owner");
        OfflinePlayer offlinePlayer = null;
        if (owner != null) {
            if (owner.equalsIgnoreCase("self") && sender instanceof Player player) {
                offlinePlayer = player;
            } else {
                offlinePlayer = NMSUtil.isPaper()
                        ? Bukkit.getOfflinePlayerIfCached(owner)
                        : Bukkit.getOfflinePlayer(owner);
            }
        }

        return new ItemBuilder(material)
                .name(locale.format(sender, section.getString(key + ".name"), placeholders))
                .amount(Math.min(1, section.getInt(key + ".amount", 1)))
                .lore(locale.format(sender, section.getStringList(key + ".lore"), placeholders))
                .flags(flags)
                .glow(section.getBoolean(key + ".glow", false))
                .unbreakable(section.getBoolean(key + ".unbreakable", false))
                .model(toInt(locale.format(sender, section.getString(key + ".model-data", "0"), placeholders)))
                .enchant(enchantments)
                .texture(locale.format(sender, section.getString(key + ".texture"), placeholders))
                .color(fromHex(locale.format(sender, section.getString(key + ".potion-color"), placeholders)))
                .owner(offlinePlayer)
                .build();
    }

    /**
     * Deserialize an ItemStack from a CommentedConfigurationSection
     *
     * @param section The section to deserialize from
     * @param key     The key to deserialize from
     * @return The deserialized ItemStack
     */
    @Nullable
    public static ItemStack deserialize(@NotNull CommentedConfigurationSection section, @NotNull String key) {
        return deserialize(section, null, key, StringPlaceholders.empty());
    }

    /**
     * Deserialize an ItemStack from a CommentedConfigurationSection with placeholders
     *
     * @param section The section to deserialize from
     * @param sender  The CommandSender to apply placeholders from
     * @param key     The key to deserialize from
     * @return The deserialized ItemStack
     */
    @Nullable
    public static ItemStack deserialize(@NotNull CommentedConfigurationSection section, @Nullable CommandSender sender, @NotNull String key) {
        return deserialize(section, sender, key, StringPlaceholders.empty());
    }

    /**
     * Parse an integer from an object safely
     *
     * @param object The object
     * @return The integer
     */
    private static int toInt(String object) {
        try {
            return Integer.parseInt(object);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Convert a string to a duration
     *
     * @param input The input string
     * @return The duration
     */
    public static Duration getTime(String input) {
        if (input == null || input.isEmpty()) return Duration.ZERO;

        long seconds = 0;
        long minutes = 0;
        long hours = 0;
        long days = 0;

        String[] split = input.split(" ");
        for (String s : split) {
            if (s.endsWith("s")) {
                seconds += toInt(s.replace("s", ""));
            } else if (s.endsWith("m")) {
                minutes += toInt(s.replace("m", ""));
            } else if (s.endsWith("h")) {
                hours += toInt(s.replace("h", ""));
            } else if (s.endsWith("d")) {
                days += toInt(s.replace("d", ""));
            }
        }

        return Duration.ofSeconds(seconds).plusMinutes(minutes).plusHours(hours).plusDays(days);

    }

    /**
     * Format a time in milliseconds into a string
     *
     * @param time Time in milliseconds
     * @return Formatted time
     */
    public static String formatTime(long time) {
        long totalSeconds = time / 1000;
        if (totalSeconds <= 0) return "";

        long days = (int) Math.floor(totalSeconds / 86400.0);
        totalSeconds %= 86400;

        long hours = (int) Math.floor(totalSeconds / 3600.0);
        totalSeconds %= 3600;

        long minutes = (int) Math.floor(totalSeconds / 60.0);
        long seconds = (totalSeconds % 60);

        StringBuilder builder = new StringBuilder();
        if (days > 0) builder.append(days).append("d, ");
        if (hours > 0) builder.append(hours).append("h, ");
        if (minutes > 0) builder.append(minutes).append("m, ");
        if (seconds > 0) builder.append(seconds).append("s");
        return builder.toString();
    }


    /**
     * Create a file in a folder from the plugin's resources
     *
     * @param rosePlugin The plugin
     * @param folders    The folders
     * @return The file
     */
    @NotNull
    public static File createFile(@NotNull RosePlugin rosePlugin, @NotNull String... folders) {
        File file = new File(rosePlugin.getDataFolder(), String.join("/", folders)); // Create the file
        if (file.exists())
            return file;

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        String path = String.join("/", folders);
        try (InputStream stream = rosePlugin.getResource(path)) {
            if (stream == null) {
                file.createNewFile();
                return file;
            }

            Files.copy(stream, Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    @NotNull
    public static File createFile(@NotNull RosePlugin rosePlugin, @NotNull File file) {
        return createFile(rosePlugin, file.getPath());
    }

    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name) {
        if (name == null)
            return null;

        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        return null;
    }


    /**
     * Get an enum from a string value
     *
     * @param enumClass The enum class
     * @param name      The name of the enum
     * @param <T>       The enum name
     * @return The enum
     */
    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name, T def) {
        if (name == null)
            return def;

        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        return def;
    }

    /**
     * Convert a list of strings to a list of enums
     * @param enumClass The enum class
     * @param content The content to convert
     * @return The list of enums
     * @param <T> The enum type
     */
    public static <T extends Enum<T>> List<T> getEnumList(Class<T> enumClass, List<String> content) {
        return content.stream()
                .map(s -> getEnum(enumClass, s))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Parse a list of strings from 1-1 to a stringlist
     *
     * @param list The list to parse
     * @return The parsed list
     */
    @SuppressWarnings("unchecked")
    public static List<Integer> parseList(List<String> list) {
        List<Integer> newList = new ArrayList<>();
        for (String s : list) {
            String[] split = s.split("-");
            if (split.length != 2) {
                continue;
            }

            newList.addAll(getNumberRange(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
        }

        return newList;
    }

    /**
     * Get a range of numbers as a list
     *
     * @param start The start of the range
     * @param end   The end of the range
     * @return A list of numbers
     */
    public static List<Integer> getNumberRange(int start, int end) {
        if (start == end) {
            return List.of(start);
        }

        List<Integer> list = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            list.add(i);
        }

        return list;
    }

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
