package dev.oribuin.fishing.model.animation.impl.particle;

import dev.oribuin.fishing.model.animation.Animated;
import dev.oribuin.fishing.model.animation.type.ParticleAnimation;
import dev.oribuin.fishing.util.math.MathL;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class RingParticleAnimation extends ParticleAnimation {

    private final int radius;

    /**
     * Create a new Particle Animation object to display particles
     *
     * @param animated The Animated object to display the particles
     */
    public RingParticleAnimation(Animated animated, int radius) {
        super(animated);

        this.radius = radius;
    }

    /**
     * The list of locations to spawn the particles at for the animation
     *
     * @param animated The Animated object to display the particles
     *
     * @return A list of locations to spawn the particles at
     */
    @Override
    public List<Location> create(Animated animated) {
        List<Location> results = new ArrayList<>();
        Location location = this.animated.getSource().get();
        int numSteps = 120;
        for (int i = 0; i < numSteps; i++) {
            double dx = MathL.cos(Math.PI * 2 * ((double) i / numSteps)) * radius;
            double dz = MathL.sin(Math.PI * 2 * ((double) i / numSteps)) * radius;

            results.add(location.clone().add(dx, 0, dz));
        }

        return results;
    }

}
