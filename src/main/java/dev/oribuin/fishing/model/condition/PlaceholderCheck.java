package dev.oribuin.fishing.model.condition;


import dev.oribuin.fishing.config.Configurable;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Create a new instance of the placeholder checking class, used to check if a placeholder based condition is met or not
 */
public class PlaceholderCheck implements Configurable {

    private CheckType type = CheckType.EQUALS;
    private String input;
    private String output;
    private boolean inverted;
    private boolean required;

    /**
     * Create a new instance of the placeholder checking class, used to check if a placeholder based condition is met or not
     *
     * @param input    The input string to check
     * @param output   The output string to check
     * @param inverted Whether the check should be inverted
     * @param required Whether the check is required to pass to continue
     */
    private PlaceholderCheck(String input, String output, boolean inverted, boolean required) {
        this.input = input;
        this.output = output;
        this.inverted = inverted;
        this.required = required;
    }

    /**
     * Create a new instance of the placeholder checking class, used to check if a placeholder based condition is met or not
     *
     * @param input    The input string to check
     * @param output   The output string to check
     * @param inverted Whether the check should be inverted
     *
     * @return The new instance of the placeholder checking class
     */
    public static PlaceholderCheck create(String input, String output, boolean inverted, boolean required) {
        return new PlaceholderCheck(input, output, inverted, required);
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
        return new PlaceholderCheck(input, output, false, true);
    }

    /**
     * Create a new instance of the placeholder checking class, used to check if a placeholder based condition is met or not
     *
     * @param input The input string to check
     *
     * @return The new instance of the placeholder checking class
     */
    public static PlaceholderCheck create(String input) {
        return new PlaceholderCheck(input, null, false, true);
    }

    /**
     * Create a new instance of the placeholder checking class, used to check if a placeholder based condition is met or not
     *
     * @param config The {@link CommentedConfigurationSection} to load the settings from
     *
     * @return The new instance of the placeholder checking class
     */
    public static PlaceholderCheck create(CommentedConfigurationSection config) {
        PlaceholderCheck check = new PlaceholderCheck(null, null, false, true);
        check.loadSettings(config);
        return check;
    }

    /**
     * Initialize a {@link CommentedConfigurationSection} from a configuration file to establish the settings
     * for the configurable class, will be automatically called when the configuration file is loaded using {@link #reload()}
     * <p>
     * If your class inherits from another configurable class, make sure to call super.loadSettings(config)
     * to save the settings from the parent class
     * <p>
     * A class must be initialized before settings are loaded, If you wish to have a configurable data class style, its best to create a
     * static method that will create a new instance and call this method on the new instance
     * <p>
     * The {@link CommentedConfigurationSection} should never be null, when creating a new section,
     * use {@link #pullSection(CommentedConfigurationSection, String)} to establish new section if it doesn't exist
     *
     * @param config The {@link CommentedConfigurationSection} to load the settings from, this cannot be null.
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.input = config.getString("input", null);
        this.output = config.getString("output", null);
        this.inverted = config.getBoolean("inverted", false);
        this.required = config.getBoolean("required", true);
        this.type = FishUtils.getEnum(CheckType.class, config.getString("type"), CheckType.EQUALS);
    }

    /**
     * Serialize the settings of the configurable class into a {@link CommentedConfigurationSection} to be saved later
     * <p>
     * This functionality will not update the configuration file, it will only save the settings into the section to be saved later.
     * <p>
     * The function {@link #reload()} will save the settings on first load, please override this method if you wish to save the settings regularly
     * New sections should be created using {@link #pullSection(CommentedConfigurationSection, String)}
     *
     * @param config The {@link CommentedConfigurationSection} to save the settings to, this cannot be null.
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("input", this.input);
        config.set("output", this.output);
        config.set("inverted", this.inverted);
        config.set("required", this.required);
        config.set("type", this.type.name().toLowerCase());
    }

    /**
     * Check if the input and output strings are equal or not
     *
     * @param player       The player to check
     * @param placeholders The placeholders to apply to the input and output strings
     *
     * @return Results in true if the condition is met
     */
    public boolean attempt(Player player, StringPlaceholders placeholders) {
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
    public boolean equals(Player player, StringPlaceholders placeholders) {
        if (input == null || output == null) return false;

        String input = PlaceholderAPIHook.applyPlaceholders(player, placeholders.apply(this.input));
        String output = PlaceholderAPIHook.applyPlaceholders(player, placeholders.apply(this.output));

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
    public boolean contains(Player player, StringPlaceholders placeholders) {
        if (input == null || output == null) return false;

        String input = PlaceholderAPIHook.applyPlaceholders(player, placeholders.apply(this.input));
        String output = PlaceholderAPIHook.applyPlaceholders(player, placeholders.apply(this.output));

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
    public boolean startsWith(Player player, StringPlaceholders placeholders) {
        if (input == null || output == null) return false;

        String input = PlaceholderAPIHook.applyPlaceholders(player, placeholders.apply(this.input));
        String output = PlaceholderAPIHook.applyPlaceholders(player, placeholders.apply(this.output));

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
    public boolean endsWith(Player player, StringPlaceholders placeholders) {
        if (input == null || output == null) return false;

        String input = PlaceholderAPIHook.applyPlaceholders(player, placeholders.apply(this.input));
        String output = PlaceholderAPIHook.applyPlaceholders(player, placeholders.apply(this.output));

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
    public boolean matches(Player player, StringPlaceholders placeholders) {
        if (input == null || output == null) return false;

        String input = PlaceholderAPIHook.applyPlaceholders(player, placeholders.apply(this.input));
        String output = PlaceholderAPIHook.applyPlaceholders(player, placeholders.apply(this.output));

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
