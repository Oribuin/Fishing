package dev.oribuin.fishing.model.animation;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public interface Animated {

    /**
     * Create a list of animations to be used in the module
     *
     * @return A list of animations
     */
    @NotNull
    List<Supplier<Animation>> createAnimations();

    /**
     * Get the source location of the animation to be used
     *
     * @return The source location
     */
    @NotNull
    Supplier<Location> getSource();

}
