package xyz.oribuin.fishing.totem;

import org.bukkit.Location;
import xyz.oribuin.fishing.util.math.MathL;

import java.util.ArrayList;
import java.util.List;

public class Totem {

    private final Location center;
    private final int radius;
    private boolean active;

    public Totem(Location center, int radius) {
        this.center = center;
        this.radius = radius;
        this.active = false;
    }

    /**
     * Test if the location is within the radius of the totem
     *
     * @param location The location to test
     *
     * @return If the location is within the radius of the totem
     */
    public boolean isWithinRadius(Location location) {
        // Radius will be in a circle around the center
        if (location.getWorld() != this.center.getWorld()) return false;

        return location.distance(this.center) <= this.radius;
    }

    /**
     * Get the outer bounds of the totem in a circle
     *
     * @return The outer bounds of the totem
     */
    public List<Location> getBounds() {
        List<Location> results = new ArrayList<>();
        int numSteps = 120;
        for (int i = 0; i < numSteps; i++) {
            double dx = MathL.cos(Math.PI * 2 * ((double) i / numSteps)) * this.radius;
            double dz = MathL.sin(Math.PI * 2 * ((double) i / numSteps)) * this.radius;

            results.add(this.center.clone().add(dx, 0, dz));
        }

        return results;

    }

    public Location getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
