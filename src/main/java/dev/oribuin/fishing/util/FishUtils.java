package dev.oribuin.fishing.util;

import dev.oribuin.fishing.FishingPlugin;
import io.papermc.paper.registry.RegistryAccess;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class FishUtils {

    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    public static RegistryAccess REGISTRY = RegistryAccess.registryAccess();
    public static LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    public static MiniMessage MINIMESSAGE = MiniMessage.miniMessage();

    public FishUtils() {
        throw new IllegalStateException("FishUtil is a utility class and cannot be instantiated.");
    }

    /**
     * Convert a string to a component
     *
     * @param text The text to convert
     *
     * @return The component
     */
    public static Component kyorify(String text) {
        return MINIMESSAGE.deserialize(text)
                .decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Convert a string into a component with placeholders applied
     *
     * @param text         The text to convert
     * @param placeholders The placeholders to apply
     *
     * @return The component
     */
    public static Component kyorify(String text, Placeholders placeholders) {
        return placeholders.apply(text);
    }

    /**
     * Create a namespaced key from a string value, defaulting to the minecraft namespace
     *
     * @param value The value
     *
     * @return The namespaced key
     */
    public static NamespacedKey key(String value) {
        if (value == null) return null;
        return value.contains(":") ? NamespacedKey.fromString(value) : NamespacedKey.minecraft(value);
    }

    /**
     * Evaluate a math expression and return the result
     *
     * @param expression The expression to evaluate
     *
     * @return The result of the expression
     */
    public static double evaluate(String expression) {
        return new ExpressionBuilder(expression).build().evaluate();
    }

    /**
     * Get a bukkit color from a hex code
     *
     * @param hex The hex code
     *
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
     * Parse an integer from an object safely
     *
     * @param object The object
     *
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
     *
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
     *
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

        // remove the last comma
        if (builder.toString().endsWith(", ")) {
            builder.setLength(builder.length() - 2);
        }

        return builder.toString();
    }


    /**
     * Create a file in a folder from the plugin's resources
     *
     * @param javaPlugin The plugin
     * @param folders    The folders
     *
     * @return The file
     */
    @NotNull
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File createFile(@NotNull JavaPlugin javaPlugin, @NotNull String... folders) {
        File file = new File(javaPlugin.getDataFolder(), String.join("/", folders)); // Create the file
        if (file.exists())
            return file;

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        String path = String.join("/", folders);
        try (InputStream stream = javaPlugin.getResource(path)) {
            if (stream == null) {
                file.createNewFile();
                return file;
            }

            Files.copy(stream, Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            FishingPlugin.get().getLogger().severe("Failed to create file: " + file.getPath() + " - " + e.getMessage());
        }

        return file;
    }

    @NotNull
    public static File createFile(@NotNull JavaPlugin javaPlugin, @NotNull File file) {
        return createFile(javaPlugin, file.getPath());
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
     * Convert a string such as "1-2" to a pair of integers
     *
     * @param height The height string
     *
     * @return The pair of integers
     */
    public static Pair<Integer, Integer> getHeight(String height) {
        if (height == null) return null;

        try {
            String[] split = height.split("-");
            if (split.length == 2) {
                return Pair.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }
        } catch (NumberFormatException e) {
            FishingPlugin.get().getLogger().warning("Failed to parse height: " + height);
        }

        return null;
    }


    /**
     * Get an enum from a string value
     *
     * @param enumClass The enum class
     * @param name      The name of the enum
     * @param <T>       The enum name
     *
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
     *
     * @param enumClass The enum class
     * @param content   The content to convert
     * @param <T>       The enum type
     *
     * @return The list of enums
     */
    public static <T extends Enum<T>> List<T> getEnumList(Class<T> enumClass, List<String> content) {
        return content.stream()
                .map(s -> getEnum(enumClass, s))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Niceify an enum value to a string
     *
     * @param enumValue The enum value
     * @param <T>       The enum type
     *
     * @return The niceified string
     */
    public static <T extends Enum<T>> String niceify(T enumValue) {
        String noUnderscores = enumValue.name().toLowerCase().replace("_", " ");
        return StringUtils.capitalize(noUnderscores);
    }

    public static <T extends Enum<T>> String niceify(T enumValue, String def) {
        if (enumValue == null) return def;

        String noUnderscores = enumValue.name().toLowerCase().replace("_", " ");
        return StringUtils.capitalize(noUnderscores);
    }

    /**
     * Format every word in a string to be capitalized
     *
     * @param str The string to format
     *
     * @return The formatted string
     */
    public static String capitalizeFully(String str) {
        String[] split = str.toLowerCase().split(" ");
        StringBuilder builder = new StringBuilder();

        for (String s : split) {
            builder.append(StringUtils.capitalize(s)).append(" ");
        }

        return builder.toString().trim();
    }

    /**
     * Parse a list of strings from 1-1 to a stringlist
     *
     * @param list The list to parse
     *
     * @return The parsed list
     */
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
     *
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

    /**
     * Generate the default fish names from the resources
     *
     * @return The list of fish names
     */
    public static List<String> generateFishNames() {
        List<String> fishNames = new ArrayList<>();

        InputStream stream = FishingPlugin.get().getResource("generated/fish.txt");
        if (stream == null) {
            FishingPlugin.get().getLogger().severe("Failed to generate default fish: InputStream is null.");
            return fishNames;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fishNames.add(line);
            }
        } catch (Exception e) {
            FishingPlugin.get().getLogger().severe("Failed to generate default fish: " + e.getMessage());
        }

        return fishNames;
    }
}
