package xyz.oribuin.fishing.totem.upgrade.impl;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import xyz.oribuin.fishing.economy.Cost;
import xyz.oribuin.fishing.economy.Currencies;
import xyz.oribuin.fishing.totem.Totem;
import xyz.oribuin.fishing.totem.upgrade.TotemUpgrade;

public class UpgradeMythicWaters extends TotemUpgrade {

    /**
     * The name of the totem upgrade to be displayed to the player
     *
     * @return The name of the upgrade
     */
    @Override
    public String name() {
        return "mythic_waters";
    }

    /**
     * Get the cost of the upgrade for a specific level
     *
     * @param level The level of the upgrade
     *
     * @return The cost of the upgrade
     */
    @Override
    public Cost costFor(int level) {
        return new Cost(Currencies.ENTROPY.get(), 0);
    }

    /**
     * Apply the upgrade to the totem object
     *
     * @param totem The totem object to apply the upgrade to
     */
    public static void playAnimation(Totem totem) {
        if (!totem.active()) return; // Check if the totem is active
        if (totem.entity() == null) return;

        // spawn purple dust particles within the totem's radius
        int radius = totem.radius(); // Get the totem's radius (radius is circular)
        int toSpawn = (int) Math.pow(radius, 1.5); // Calculate the amount of particles to spawn

        Location center = totem.center().toCenterLocation().add(0, 1.5, 0); // Get the center of the totem

        for (int i = 0; i < toSpawn; i++) {
            // Pick a random location within the totem's radius
            double x = center.getX() + (Math.random() * radius * 2) - radius;
            double z = center.getZ() + (Math.random() * radius * 2) - radius;

            // Spawn the particle
            // choose which particle to spawn at random (50% chance)
            ParticleBuilder particle = Math.random() > 0.5 ? PURPLE_DUST : PORTAL;
            particle.location(center.getWorld(), x, center.getY(), z)
                    .spawn();
        }
    }

    private static final ParticleBuilder PURPLE_DUST = new ParticleBuilder(Particle.FALLING_DUST)
            .count(2)
            .extra(0.0)
            .data(Material.PURPLE_CONCRETE_POWDER.createBlockData());

    private static final ParticleBuilder PORTAL = new ParticleBuilder(Particle.REVERSE_PORTAL)
            .count(2)
            .offset(0.5, 0.5, 0.5)
            .extra(0.0);
}
