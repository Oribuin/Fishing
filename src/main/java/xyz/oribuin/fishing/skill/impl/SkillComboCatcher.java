package xyz.oribuin.fishing.skill.impl;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.FishContext;
import xyz.oribuin.fishing.api.event.FishGenerateEvent;
import xyz.oribuin.fishing.fish.Fish;
import xyz.oribuin.fishing.manager.ConfigurationManager.Setting;
import xyz.oribuin.fishing.skill.Skill;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillComboCatcher extends Skill {

    private final Map<UUID, ComboData> combos = new HashMap<>();

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

        if (System.currentTimeMillis() - data.lastCatch() > Setting.SKILLS_COMBO_TIMEOUT.getInt() * 1000L) {
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
    @Override
    public void onFishCatch(FishContext context, Fish fish, ItemStack stack) {
        Player player = context.player();
        int timeout = Setting.SKILLS_COMBO_TIMEOUT.getInt();
        ComboData current = this.combos.getOrDefault(player.getUniqueId(), ComboData.empty());

        // If the timeout has passed, reset the combo
        if (System.currentTimeMillis() - current.lastCatch() > timeout * 1000L) {
            current = ComboData.empty();
        }

        // Increase the combo
        int max = this.maxCombo(context.level());
        current.current(Math.min(current.current() + 1, max));
        current.lastCatch(System.currentTimeMillis());

        this.combos.put(player.getUniqueId(), current);
    }

    /**
     * The maximum combo a player can have based on their level
     *
     * @param level  The level of the player
     *
     * @return The max combo
     */
    private int maxCombo(int level) {
        StringPlaceholders plc = StringPlaceholders.of("level", level);
        return (int) FishUtils.evaluate(plc.apply(Setting.SKILLS_COMBO_MAX_FORMULA.getString()));
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
        return FishUtils.evaluate(plc.apply(Setting.SKILLS_COMBO_CHANCE_INCREASE.getString()));
    }

    public static class ComboData {

        private int current;
        private long lastCatch;

        /**
         * Holder for all relevant info about the player's current combo
         *
         * @param current The current combo level
         * @param lastCatch    The last time a player caught a fish
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
         * @param newLastCatch The new time
         */
        public void lastCatch(long newLastCatch) {
            this.lastCatch = newLastCatch;
        }

    }

}