package xyz.oribuin.fishing.totem;

import com.jeff_media.morepersistentdatatypes.DataType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.storage.PersistKeys;
import xyz.oribuin.fishing.util.FishUtils;
import xyz.oribuin.fishing.util.math.MathL;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Totem {

    private final UUID owner;
    private String ownerName; // The name of the owner
    private boolean active; // If the totem is active
    private int radius; // Radius of the totem
    private Duration duration; // Duration for the totem
    private Duration cooldown; // Cooldown for the totem
    private long lastActive; // Last time the totem was active
    private Location center; // Center of the totem
    private ArmorStand entity; // The entity that will be spawned.

    public Totem(Player owner, Location center, int radius) {
        this.owner = owner.getUniqueId();
        this.ownerName = owner.getName();
        this.center = center;
        this.radius = radius;
        this.active = false;
    }

    /**
     * Spawn in the totem in the world at a location
     *
     * @param location The block location to spawn the totem
     */
    public void spawn(Location location) {
        this.center = location.clone().add(0.5, 1.5, 0.5);
        this.entity = this.center.getWorld().spawn(
                this.center,
                ArmorStand.class,
                CreatureSpawnEvent.SpawnReason.CUSTOM,
                result -> {
                    result.setInvisible(true);
                    result.setCanTick(false);
                    result.setGravity(false);
                    result.setDisabledSlots(EquipmentSlot.values());
                    result.setVisible(false);
                    result.setCustomNameVisible(true);
                    // TODO: Allow configurable name
                    result.customName(Component.text(this.ownerName + "'s Totem"));
                    this.saveToContainer(result.getPersistentDataContainer());
                });

        // Create spawning particles around the totem
        int maxRadius = 2;
        double height = -0.25;
        List<Location> results = new ArrayList<>();
        int numSteps = 120;
        for (double newRadius = 0; newRadius < maxRadius; newRadius += 0.25) {
            for (int i = 0; i < numSteps; i++) {
                height += 0.25;
                double dx = MathL.cos(Math.PI * 2 * ((double) i / numSteps)) * maxRadius - newRadius;
                double dz = MathL.sin(Math.PI * 2 * ((double) i / numSteps)) * maxRadius - newRadius;

                results.add(this.center.clone().add(dx, height, dz));
            }
        }

        List<Location> bounds = this.bounds();
        long startTime = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(FishingPlugin.get(), task -> {
            // if longer than 3 seconds cancel
            if (System.currentTimeMillis() - startTime > Duration.ofSeconds(3).toMillis()) {
                task.cancel();
                return;
            }

            // Spawn totem particles around the totem
            results.forEach(x -> this.center.getWorld().spawnParticle(
                    Particle.TOTEM,
                    x,
                    2,
                    0, 0, 0, 0
            ));

            // Spawn dust particles to display the totem radius
            Particle.DustOptions options = new Particle.DustOptions(Color.LIME, 1f);
            results.forEach(x -> this.center.getWorld().spawnParticle(
                    FishUtils.getEnum(Particle.class, "DUST", Particle.REDSTONE),
                    x,
                    2,
                    0,0,0,0,
                    options
            ));
        });
    }

    /**
     * Save all the totem values to the container
     *
     * @param container The container to save the values to
     */
    public void saveToContainer(PersistentDataContainer container) {
        container.set(PersistKeys.TOTEM_OWNER, DataType.UUID, this.owner);
        container.set(PersistKeys.TOTEM_RADIUS, DataType.INTEGER, this.radius);
        container.set(PersistKeys.TOTEM_ACTIVE, DataType.BOOLEAN, this.active);
        container.set(PersistKeys.TOTEM_DURATION, DataType.LONG, this.duration.toMillis());
        container.set(PersistKeys.TOTEM_COOLDOWN, DataType.LONG, this.cooldown.toMillis());
        container.set(PersistKeys.TOTEM_LASTACTIVE, DataType.LONG, this.lastActive);
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
