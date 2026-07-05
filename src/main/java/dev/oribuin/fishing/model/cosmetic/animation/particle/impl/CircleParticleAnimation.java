package dev.oribuin.fishing.model.cosmetic.animation.particle.impl;

import dev.oribuin.fishing.model.cosmetic.animation.particle.ParticleAnimation;
import dev.oribuin.fishing.util.math.MathL;
import org.bukkit.Location;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Create a new particle animation involving 
 */
@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class CircleParticleAnimation extends ParticleAnimation {

    private int radius = 5;

    /**
     * Get a variety of locations around a specified radius
     *
     * @param center The centre of the animation
     *
     * @return The positions of it
     */
    @Override
    public List<Location> getPositions(Location center) {
        List<Location> results = new ArrayList<>();
        int numSteps = 120;
        for (int i = 0; i < numSteps; i++) {
            double dx = MathL.cos(Math.PI * 2 * ((double) i / numSteps)) * this.radius;
            double dz = MathL.sin(Math.PI * 2 * ((double) i / numSteps)) * this.radius;
            results.add(center.clone().add(dx, 0, dz));
        }

        return results;
    }


}
