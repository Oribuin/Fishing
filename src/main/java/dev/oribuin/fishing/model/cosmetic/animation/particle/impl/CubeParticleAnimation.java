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
public class CubeParticleAnimation extends ParticleAnimation {

    private int radiusX;
    private int radiusY;
    private int radiusZ;

    /**
     * Create a 3D Cube Animation for particles
     */
    public CubeParticleAnimation() {
        this.radiusX = 5;
        this.radiusY = 5;
        this.radiusZ = 5;
    }

    /**
     * Create a 3D Cube Animation for particles
     *
     * @param radiusX The width of the cube on the x-axis
     * @param radiusY The width of the cube on the y-axis
     * @param radiusZ The width of the cube on the z-axis
     */
    public CubeParticleAnimation(int radiusX, int radiusY, int radiusZ) {
        this.radiusX = radiusX;
        this.radiusY = radiusY;
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

        Location min = center.clone().subtract(radiusX, (double) radiusY / 2, radiusZ);
        Location max = center.clone().add(radiusX, (double) radiusY / 2, radiusZ);
        double minX = min.getBlockX();
        double minZ = min.getBlockZ();
        double maxX = max.getBlockX();
        double maxZ = max.getBlockZ();
        int minY = min.getBlockY();
        int maxY = max.getBlockY();

        World world = center.getWorld();
        for (int y = minY; y < maxY; y++) {
            for (double x = minX; x <= maxX; x++) {
                bounds.add(new Location(world, x, y, minZ).toCenterLocation());
                bounds.add(new Location(world, x, y, maxZ).toCenterLocation());
            }

            for (double z = minZ; z <= maxZ; z++) {
                bounds.add(new Location(world, minX, y, z).toCenterLocation());
                bounds.add(new Location(world, maxX, y, z).toCenterLocation());
            }
        }

        return bounds.stream().distinct().toList();
    }

    public int getRadiusX() {
        return radiusX;
    }

    public int getRadiusY() {
        return radiusY;
    }

    public int getRadiusZ() {
        return radiusZ;
    }
}
