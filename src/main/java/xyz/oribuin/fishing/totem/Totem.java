package xyz.oribuin.fishing.totem;

import org.bukkit.Location;
import xyz.oribuin.fishing.util.math.MathL;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Totem {

    private final UUID owner;
    private final Location center;
    private int radius;
    private boolean active;

    public Totem(UUID owner, Location center, int radius) {
        this.owner = owner;
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
    public boolean withinRadius(Location location) {
        // Radius will be in a circle around the center
        if (location.getWorld() != this.center.getWorld()) return false;

        return location.distance(this.center) <= this.radius;
    }

    /**
     * Get the outer bounds of the totem in a circle
     *
     * @return The outer bounds of the totem
     */
    public List<Location> bounds() {
        List<Location> results = new ArrayList<>();
        int numSteps = 120;
        for (int i = 0; i < numSteps; i++) {
            double dx = MathL.cos(Math.PI * 2 * ((double) i / numSteps)) * this.radius;
            double dz = MathL.sin(Math.PI * 2 * ((double) i / numSteps)) * this.radius;

            results.add(this.center.clone().add(dx, 0, dz));
        }

        return results;

    }

    public UUID owner() {
        return owner;
    }

    public Location center() {
        return center;
    }

    public int radius() {
        return radius;
    }

    public void radius(int radius) {
        this.radius = radius;
    }

    public boolean active() {
        return active;
    }

    public void active(boolean active) {
        this.active = active;
    }

}
