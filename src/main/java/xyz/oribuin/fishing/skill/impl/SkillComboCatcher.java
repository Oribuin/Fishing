package xyz.oribuin.fishing.skill.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.event.FishCatchEvent;
import xyz.oribuin.fishing.api.event.FishGenerateEvent;
import xyz.oribuin.fishing.skill.Skill;
import xyz.oribuin.fishing.util.FishUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SkillComboCatcher extends Skill {

    private final Map<UUID, ComboData> combos = new HashMap<>();
    private String maxFormula = "%level% / 5 * 2"; // Max combo formula
    private String chanceFormula = "%combo% * 0.1"; // The buff per combo
    private Duration timeout = Duration.ofSeconds(30); // The time until the combo is reset

    /**
     * Skills are player unique upgrades, different to augments because
     * they are universal for the player and will activate regardless of what fishing
     * rod they use. These skills can be bought with fishing levels
     */
    public SkillComboCatcher() {
        super("combo_catcher",
                "Increases the chance of obtaining higher ",
                "rarity first when catching consecutively",
                "Current Combo: %combo%/%maxcombo%",
                "Current Bonus: %bonus%"
        );
    }

    /**
     * The functionality provided when a fish is generated for the player
     *
     * @param event The event that is fired when a fish is generated
     * @param level The level of the augment that was used
     */
    @Override
    public void onGenerate(FishGenerateEvent event, int level) {
        Player player = event.getPlayer();
        ComboData data = this.combos.getOrDefault(player.getUniqueId(), ComboData.empty());
        if (data.current() == 0) return;
        if (data.lastCatch() == 0) return;

        if (System.currentTimeMillis() - data.lastCatch() > this.timeout.toMillis()) {
            data = ComboData.empty();
            this.combos.put(player.getUniqueId(), data);
        }

        // Increase the rate of catching a fish based on the combo
        double increase = this.percentageIncrease(data.current());
        event.addIncrease(increase);
    }

    /**
     * The functionality provided by the augment when a player obtains a fish from the initial catch
     * This method is run for each fish caught
     *
     * @param context The context of the fish event
     * @param fish    The fish that was caught
     * @param stack   The item stack of the fish
     */
    /**
     * The functionality provided by the augment when a player obtains a fish from the initial catch
     * This method is run for each fish caught
     *
     * @param event The context of the fish event
     * @param level The level of the augment that was used
     */
    @Override
    public void onFishCatch(FishCatchEvent event, int level) {
        Player player = event.getPlayer();
        long timeout = this.timeout.toSeconds();
        ComboData current = this.combos.getOrDefault(player.getUniqueId(), ComboData.empty());

        // If the timeout has passed, reset the combo
        if (System.currentTimeMillis() - current.lastCatch() > timeout * 1000L) {
            current = ComboData.empty();
        }

        // Increase the combo
        int max = this.maxCombo(level);
        current.current(Math.min(current.current() + 1, max));
        current.lastCatch(System.currentTimeMillis());

        this.combos.put(player.getUniqueId(), current);
    }

    /**
     * The maximum combo a player can have based on their level
     *
     * @param level The level of the player
     *
     * @return The max combo
     */
    private int maxCombo(int level) {
        StringPlaceholders plc = StringPlaceholders.of("level", level);
        return (int) FishUtils.evaluate(plc.apply(this.maxFormula));
    }

    /**
     * How much the rate of catching a fish increases for the active combo
     *
     * @param combo The current combo level
     *
     * @return The player
     */
    private double percentageIncrease(int combo) {
        StringPlaceholders plc = StringPlaceholders.of("combo", combo);
        return FishUtils.evaluate(plc.apply(this.chanceFormula));
    }

    /**
     * The comments to be generated at the top of the file when it is created
     *
     * @return The comments
     */
    @Override
    public List<String> comments() {
        return List.of(
                "Skill [Combo Catcher] - Increases the chance of catching higher rarity fish with each consecutive catch",
                "This skill will reset after not catching a fish for 30 seconds",
                "",
                "max-formula: The formula to calculate the maximum combo a player can have based on their level",
                "chance-formula: The formula to calculate the chance increase per combo",
                "timeout: The time in seconds until the combo is reset after not catching a fish"
        );
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
        super.loadSettings(config);

        this.maxFormula = config.getString("max-formula", this.maxFormula);
        this.chanceFormula = config.getString("chance-formula", this.chanceFormula);
        this.timeout = Duration.ofSeconds(config.getInt("timeout", (int) this.timeout.getSeconds()));
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
        super.saveSettings(config);

        config.set("max-formula", this.maxFormula);
        config.set("chance-formula", this.chanceFormula);
        config.set("timeout", (int) this.timeout.getSeconds());
    }

    public static class ComboData {

        private int current;
        private long lastCatch;

        /**
         * Holder for all relevant info about the player's current combo
         *
         * @param current   The current combo level
         * @param lastCatch The last time a player caught a fish
         */
        public ComboData(int current, long lastCatch) {
            this.current = current;
            this.lastCatch = lastCatch;
        }

        /**
         * Brand-new combo data for the player
         *
         * @return The new data
         */
        public static ComboData empty() {
            return new ComboData(0, 0);
        }

        /**
         * @return The current combo the player has
         */
        public int current() {
            return this.current;
        }

        /**
         * Apply a new combo count to the player
         *
         * @param newCurrent The new combo count
         */
        public void current(int newCurrent) {
            this.current = newCurrent;
        }

        /**
         * @return The last time the player caught a fish
         */
        public long lastCatch() {
            return this.lastCatch;
        }

        /**
         * Apply a new time to the player's last catch
         *
         * @param newLastCatch The new time
         */
        public void lastCatch(long newLastCatch) {
            this.lastCatch = newLastCatch;
        }

    }

}
