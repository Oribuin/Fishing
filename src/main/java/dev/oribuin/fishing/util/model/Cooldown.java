package dev.oribuin.fishing.util.model;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Cooldown<T> {

    private final Map<T, Long> cooldown = new HashMap<>();

    /**
     * Check if something is still on cooldown
     *
     * @param entry The cooldown to employ
     *
     * @return true if on cooldown
     */
    public boolean onCooldown(T entry) {
        long timeRemaining = this.getTimeRemaining(entry);
        if (timeRemaining <= 0) {
            this.cooldown.remove(entry);
            return false;
        }

        return true;
    }

    /**
     * Get the time remaining for something that is on cooldown
     *
     * @param entry The value on cooldown
     *
     * @return The returning time in milliseconds
     */
    public long getTimeRemaining(T entry) {
        long now = System.currentTimeMillis();
        return cooldown.getOrDefault(entry, now) - now;
    }

    /**
     * Get the time remaining for something that is on cooldown
     *
     * @param entry The value on cooldown
     *
     * @return The returning time in milliseconds
     */
    public Duration getDurationRemaining(T entry) {
        return Duration.ofMillis(this.getTimeRemaining(entry));
    }

    /**
     * Apply a new cooldown set for x amount of time
     *
     * @param entry The entry to add to cooldown
     * @param time  How long the cooldown lasts
     */
    public void setCooldown(T entry, Duration time) {
        if (time.isNegative() || time.isZero()) return;

        this.cooldown.put(entry, System.currentTimeMillis() + time.toMillis());
    }

}
