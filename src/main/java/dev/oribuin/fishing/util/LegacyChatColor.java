package dev.oribuin.fishing.util;

public enum LegacyChatColor {
    AQUA("b"),
    DARK_GREEN("2"),
    BLACK("0"),
    BLUE("9"),
    BOLD("l"),
    DARK_AQUA("3"),
    DARK_BLUE("1"),
    DARK_GRAY("8"),
    DARK_PURPLE("5"),
    DARK_RED("4"),
    GOLD("6"),
    GRAY("7"),
    GREEN("a"),
    ITALIC("o"),
    LIGHT_PURPLE("d"),
    OBFUSCATED("k"),
    RED("c"),
    RESET("r"),
    STRIKETHROUGH("m"),
    UNDERLINED("n"),
    WHITE("f"),
    YELLOW("e");

    private final String color;

    /**
     * Constructor for the Enum.
     *
     * @param color the legacy colour code
     */
    LegacyChatColor(String color) {
        this.color = color;
    }

    private static final LegacyChatColor[] COLORS = new LegacyChatColor[]{
            AQUA,
            DARK_GREEN,
            BLACK, BLUE,
            DARK_AQUA,
            DARK_BLUE,
            DARK_GRAY,
            DARK_PURPLE,
            DARK_RED,
            GOLD,
            GRAY,
            GREEN,
            LIGHT_PURPLE,
            RED, WHITE,
            YELLOW,
            BOLD
    };

    /**
     * Get an array of all COLORS (non formatting codes)
     *
     * @return the array of all colors
     */
    public static LegacyChatColor[] getColours() {
        return COLORS;
    }

    /**
     * Get an array of all colors + bold (all codes allowed for nicknames)
     *
     * @return the array of all colors + bold
     */
    public static LegacyChatColor[] getNicknameCodes() {
        return COLORS;
    }


    /**
     * Get the legacy colour code.
     *
     * @return the legacy colour code
     */
    public String getCode() {
        return this.color;
    }
}