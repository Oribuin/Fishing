package dev.oribuin.fishing.util.model;

import dev.oribuin.fishing.util.Placeholders;

public interface Placeholder {

    /**
     * The string placeholders for this object
     *
     * @return The compiled string placeholders
     */
    default Placeholders placeholders() {
        return Placeholders.empty();
    }

    /**
     * Append a collection of {@link Placeholders} in compound with the current one
     *
     * @param toAppend The placeholder to append
     *
     * @return A compiled {@link Placeholders}
     */
    default Placeholders placeholders(Placeholders... toAppend) {
        Placeholders.Builder builder = Placeholders.builder();
        builder.addAll(this.placeholders());

        // Append any new Placeholders into the new builder
        if (toAppend != null) {
            for (Placeholders append : toAppend) builder.addAll(append);
        }

        return builder.build();
    }

}
