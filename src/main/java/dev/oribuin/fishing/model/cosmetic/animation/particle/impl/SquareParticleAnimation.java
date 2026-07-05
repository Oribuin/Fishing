package dev.oribuin.fishing.model.cosmetic.animation.particle.impl;

import dev.oribuin.fishing.model.cosmetic.animation.particle.ParticleAnimation;
import org.bukkit.Location;
import org.bukkit.World;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Create a 3D Cube Animation for particles
 */
@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class SquareParticleAnimation extends ParticleAnimation {

    private int radiusX;
    private int radiusZ;

    /**
     * Create a 3D Cube Animation for particles
     */
    public SquareParticleAnimation() {
        this.radiusX = 5;
        this.radiusZ = 5;
    }

    /**
     * Create a 3D Cube Animation for particles
     *
     * @param radiusX The width of the cube on the x-axis
     * @param radiusZ The width of the cube on the z-axis
     */
    public SquareParticleAnimation(int radiusX, int radiusZ) {
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
    }

    /**
     * Get a variety of locations around a specified radius
     *
     * @param center The centre of the animation
     *
     * @return The positions of it
     */
    @Override
    public List<Location> getPositions(Location center) {
        List<Location> bounds = new ArrayList<>();
        
        Location min = center.clone().subtract(radiusX, 0, radiusZ);
        Location max = center.clone().add(radiusX, 0, radiusZ);
        double minX = min.getBlockX();
        double minZ = min.getBlockZ();
        double maxX = max.getBlockX();
        double maxZ = max.getBlockZ();
        double y = center.getY() + this.offsetY;

        World world = center.getWorld();
        for (double x = minX; x <= maxX; x++) {
            bounds.add(new Location(world, x, y, minZ).toCenterLocation());
            bounds.add(new Location(world, x, y, maxZ).toCenterLocation());
        }

        for (double z = minZ; z <= maxZ; z++) {
            bounds.add(new Location(world, minX, y, z).toCenterLocation());
            bounds.add(new Location(world, maxX, y, z).toCenterLocation());
        }

        return bounds.stream().distinct().toList();
    }

    public int getRadiusX() {
        return radiusX;
    }
    
    public int getRadiusZ() {
        return radiusZ;
    }
}
