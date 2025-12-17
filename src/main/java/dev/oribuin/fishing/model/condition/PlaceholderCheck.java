package dev.oribuin.fishing.model.condition;

import dev.oribuin.fishing.hook.plugin.PAPIProvider;
import dev.oribuin.fishing.util.Placeholders;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Create a new instance of the placeholder checking class, used to check if a placeholder based condition is met or not
 */
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class PlaceholderCheck {

    private CheckType type;
    private String input;
    private String output;
    private boolean inverted;
    private boolean required;

    /**
     * Create a new instance of the placeholder checking class, used to check if a placeholder based condition is met or not
     *
     * @param type     The type of method being used to check
     * @param input    The input string to check
     * @param output   The output string to check
     * @param inverted Whether the check should be inverted
     * @param required Whether the check is required to pass to continue
     */
    private PlaceholderCheck(CheckType type, String input, String output, boolean inverted, boolean required) {
        this.type = type;
        this.input = input;
        this.output = output;
        this.inverted = inverted;
        this.required = required;
    }

    /**
     * Create a new instance of the placeholder checking class, used to check if a placeholder based condition is met or not
     *
     * @param type     The type of method being used to check
     * @param input    The input string to check
     * @param output   The output string to check
     * @param inverted Whether the check should be inverted
     * @param required Whether the check is required to pass for the condition
     *
     * @return The new instance of the placeholder checking class
     */
    public static PlaceholderCheck create(CheckType type, String input, String output, boolean inverted, boolean required) {
        return new PlaceholderCheck(type, input, output, inverted, required);
    }

    /**
     * Create a new instance of the placeholder checking class, used to check if a placeholder based condition is met or not
     *
     * @param input    The input string to check
     * @param output   The output string to check
     * @param inverted Whether the check should be inverted
     * @param required Whether the check is required to pass for the condition
     *
     * @return The new instance of the placeholder checking class
     */
    public static PlaceholderCheck create(String input, String output, boolean inverted, boolean required) {
        return create(CheckType.EQUALS, input, output, inverted, required);
    }

    /**
     * Create a new instance of the placeholder checking class, used to check if a placeholder based condition is met or not
     *
     * @param input  The input string to check
     * @param output The output string to check
     *
     * @return The new instance of the placeholder checking class
     */
    public static PlaceholderCheck create(String input, String output) {
        return create(CheckType.EQUALS, input, output, false, true);
    }

    /**
     * Create a new instance of the placeholder checking class, used to check if a placeholder based condition is met or not
     *
     * @param input The input string to check
     *
     * @return The new instance of the placeholder checking class
     */
    public static PlaceholderCheck create(String input) {
        return create(CheckType.EQUALS, input, null, false, true);
    }

    /**
     * Check if the input and output strings are equal or not
     *
     * @param player       The player to check
     * @param placeholders The placeholders to apply to the input and output strings
     *
     * @return Results in true if the condition is met
     */
    public boolean attempt(Player player, Placeholders placeholders) {
        return switch (this.type) {
            case EQUALS -> this.equals(player, placeholders);
            case CONTAINS -> this.contains(player, placeholders);
            case STARTS_WITH -> this.startsWith(player, placeholders);
            case ENDS_WITH -> this.endsWith(player, placeholders);
            case MATCHES -> this.matches(player, placeholders);
        };
    }

    /**
     * Check if the input and output strings are equal or not
     *
     * @param player       The player to check
     * @param placeholders The placeholders to apply to the input and output strings
     *
     * @return Results in true if the condition is met
     */
    public boolean equals(Player player, Placeholders placeholders) {
        if (input == null || output == null) return false;

        String input = PAPIProvider.apply(player, placeholders.applyString(this.input));
        String output = PAPIProvider.apply(player, placeholders.applyString(this.output));

        return input.equals(output) != this.inverted;
    }

    /**
     * Check if the input string contains the output string or not
     *
     * @param player       The player to check
     * @param placeholders The placeholders to apply to the input and output strings
     *
     * @return Results in true if the condition is met
     */
    public boolean contains(Player player, Placeholders placeholders) {
        if (input == null || output == null) return false;

        String input = PAPIProvider.apply(player, placeholders.applyString(this.input));
        String output = PAPIProvider.apply(player, placeholders.applyString(this.output));

        return input.contains(output) != this.inverted;
    }

    /**
     * Check if the input string starts with the output string or not
     *
     * @param player       The player to check
     * @param placeholders The placeholders to apply to the input and output strings
     *
     * @return Results in true if the condition is met
     */
    public boolean startsWith(Player player, Placeholders placeholders) {
        if (input == null || output == null) return false;

        String input = PAPIProvider.apply(player, placeholders.applyString(this.input));
        String output = PAPIProvider.apply(player, placeholders.applyString(this.output));

        return input.startsWith(output) != this.inverted;
    }

    /**
     * Check if the input string ends with the output string or not
     *
     * @param player       The player to check
     * @param placeholders The placeholders to apply to the input and output strings
     *
     * @return Results in true if the condition is met
     */
    public boolean endsWith(Player player, Placeholders placeholders) {
        if (input == null || output == null) return false;

        String input = PAPIProvider.apply(player, placeholders.applyString(this.input));
        String output = PAPIProvider.apply(player, placeholders.applyString(this.output));

        return input.endsWith(output) != this.inverted;
    }

    /**
     * Check if the input string matches the output string or not
     *
     * @param player       The player to check
     * @param placeholders The placeholders to apply to the input and output strings
     *
     * @return Results in true if the condition is met
     */
    public boolean matches(Player player, Placeholders placeholders) {
        if (input == null || output == null) return false;

        String input = PAPIProvider.apply(player, placeholders.applyString(this.input));
        String output = PAPIProvider.apply(player, placeholders.applyString(this.output));

        return input.matches(output) != this.inverted;
    }

    /**
     * Check the input string based on the type of check
     */
    public enum CheckType {
        EQUALS,
        CONTAINS,
        STARTS_WITH,
        ENDS_WITH,
        MATCHES
    }

    public CheckType type() {
        return type;
    }

    public String input() {
        return input;
    }

    public String output() {
        return output;
    }

    public boolean inverted() {
        return inverted;
    }

    public boolean required() {
        return required;
    }

}
