package dev.oribuin.fishing.storage.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public record FinePosition(String world, int x, int y, int z) {

    /**
     * Check if two fine positions are the same
     *
     * @param position The fine position
     *
     * @return true if the xyz and world match
     */
    public boolean compares(FinePosition position) {
        if (!this.world.equalsIgnoreCase(position.world())) return false;

        return this.x == position.x() && this.y == position.y() && this.z == position.z();
    }

    /**
     * Check if a fine position and bukkit location are the same
     *
     * @param location The bukkit location
     *
     * @return true if the xyz and world match
     */
    public boolean compares(Location location) {
        return this.compares(from(location));
    }

    /**
     * Convert a fine position to a BukkitLocation
     *
     * @return A new bukkit location
     */
    public Location asLoc() {
        return new Location(Bukkit.getWorld(world), this.x, this.y, this.z);
    }

    /**
     * Convert a Bukkit Location into a FinePosition
     *
     * @param loc The location to convert
     *
     * @return The fine position
     */
    public static FinePosition from(Location loc) {
        return new FinePosition(
                loc.getWorld().getName(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ()
        );
    }

    @Override
    public String toString() {
        return String.format("%s, %d, %d, %d", this.world, this.x, this.y, this.z);
    }
}
