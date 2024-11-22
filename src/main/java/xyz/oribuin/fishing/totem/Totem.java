package xyz.oribuin.fishing.totem;

import com.jeff_media.morepersistentdatatypes.DataType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.task.AsyncTicker;
import xyz.oribuin.fishing.storage.util.PersistKeys;
import xyz.oribuin.fishing.util.FishUtils;
import xyz.oribuin.fishing.util.math.MathL;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Totem implements AsyncTicker {

    private final UUID owner;
    private String ownerName; // The name of the owner
    private boolean active; // If the totem is active
    private int radius; // Radius of the totem
    private Duration duration; // Duration for the totem
    private Duration cooldown; // Cooldown for the totem
    private long lastActive; // Last time the totem was active
    private Location center; // Center of the totem

    private ArmorStand entity; // The entity that will be spawned.
    private long thetaTicks; // The ticks for the totem to spin
    private double heightOffset; // The height offset for the totem


    public Totem(UUID owner, String ownerName) {
        this.owner = owner;
        this.ownerName = ownerName;
        this.radius = 10;
        this.duration = Duration.ofMinutes(2);
        this.cooldown = Duration.ofHours(1);
        this.lastActive = System.currentTimeMillis();
        this.center = null;
    }

    /**
     * Create a new totem owner with all the required values
     *
     * @param owner  The owner of the totem
     * @param center The block the totem lives
     * @param radius The effective radius of the totem
     */
    public Totem(Player owner, Location center, int radius) {
        this(owner.getUniqueId(), owner.getName());
        this.radius = radius;
        this.center = center;
    }

    /**
     * The method that should run everytime the task is ticked,
     * this method will be ran asynchronously
     */
    @Override
    public void tickAsync() {
        if (!this.center.isChunkLoaded()) return;
        if (!this.active) return;
        if (this.entity == null) return;

        this.thetaTicks++;

        // Create the totem animations for radius of the totem
        List<Location> bounds = this.bounds();
        Particle.DustOptions options = new Particle.DustOptions(Color.LIME, 1f);
        bounds.forEach(x -> this.center.getWorld().spawnParticle(
                FishUtils.getEnum(Particle.class, "REDSTONE", Particle.DUST),
                x,
                2,
                0, 0, 0, 0,
                options
        ));

        // Make the totem spin :3
        double theta = thetaTicks * 0.05;
        Location newLocation = this.center.clone();
        newLocation.setY(newLocation.getY() - this.heightOffset + Math.sin(theta) * 0.2 + this.heightOffset);
        newLocation.setYaw((float) theta * 100);

        this.entity.teleport(newLocation);

        // TODO: Run the animations for all totem upgrades
        // TODO: for each upgrade: if instanceof AsyncTickable then upgrade.tickAsync();
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
                    result.setPersistent(true);
                    // TODO: Allow configurable name
                    result.customName(Component.text(this.ownerName + "'s Totem"));
                    this.saveToContainer(result.getPersistentDataContainer());
                });

        this.heightOffset = this.entity.isSmall() ? 1.0 : 1.5; // Height offset

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
                    FishUtils.getEnum(Particle.class, "TOTEM", Particle.TOTEM_OF_UNDYING),
                    x,
                    2,
                    0, 0, 0, 0
            ));

            // Spawn dust particles to display the totem radius
            Particle.DustOptions options = new Particle.DustOptions(Color.LIME, 1f);
            results.forEach(x -> this.center.getWorld().spawnParticle(
                    FishUtils.getEnum(Particle.class, "DUST", FishUtils.getEnum(Particle.class, "REDSTONE", Particle.DUST)),
                    x,
                    2,
                    0, 0, 0, 0,
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
        container.set(PersistKeys.TOTEM_OWNERNAME, DataType.STRING, this.ownerName);
        container.set(PersistKeys.TOTEM_RADIUS, DataType.INTEGER, this.radius);
        container.set(PersistKeys.TOTEM_ACTIVE, DataType.BOOLEAN, this.active);
        container.set(PersistKeys.TOTEM_DURATION, DataType.LONG, this.duration.toMillis());
        container.set(PersistKeys.TOTEM_COOLDOWN, DataType.LONG, this.cooldown.toMillis());
        container.set(PersistKeys.TOTEM_LASTACTIVE, DataType.LONG, this.lastActive);
    }

    /**
     * Create a new totem from a container with all the required values
     *
     * @param container The container to get the values from
     *
     * @return The totem object
     */
    public static Totem fromContainer(PersistentDataContainer container) {
        UUID owner = container.get(PersistKeys.TOTEM_OWNER, DataType.UUID);
        String ownerName = container.getOrDefault(PersistKeys.TOTEM_OWNERNAME, DataType.STRING, "N/A");
        Integer radius = container.get(PersistKeys.TOTEM_RADIUS, DataType.INTEGER);
        boolean active = container.getOrDefault(PersistKeys.TOTEM_ACTIVE, DataType.BOOLEAN, false);
        Long duration = container.get(PersistKeys.TOTEM_DURATION, DataType.LONG);
        Long cooldown = container.get(PersistKeys.TOTEM_COOLDOWN, DataType.LONG);
        long lastActive = container.getOrDefault(PersistKeys.TOTEM_LASTACTIVE, DataType.LONG, System.currentTimeMillis());

        if (owner == null) return null;
        if (radius == null) return null;
        if (duration == null) return null;
        if (cooldown == null) return null;

        Totem totem = new Totem(owner, ownerName);
        totem.active(active);
        totem.radius(radius);
        totem.duration(Duration.ofMillis(duration));
        totem.cooldown(Duration.ofMillis(cooldown));
        totem.lastActive(lastActive);
        return null;
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

    public String ownerName() {
        return ownerName;
    }

    public void ownerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public boolean active() {
        return this.active;
    }

    public void active(boolean active) {
        this.active = active;
    }

    public Location center() {
        return center;
    }

    public void center(Location center) {
        this.center = center;
    }

    public int radius() {
        return this.radius;
    }

    public void radius(int radius) {
        this.radius = radius;
    }

    public Duration duration() {
        return this.duration;
    }

    public void duration(Duration duration) {
        this.duration = duration;
    }

    public Duration cooldown() {
        return cooldown;
    }

    public void cooldown(Duration cooldown) {
        this.cooldown = cooldown;
    }

    public long lasActive() {
        return this.lastActive;
    }

    public void lastActive(long lastActive) {
        this.lastActive = lastActive;
    }

    public ArmorStand entity() {
        return this.entity;
    }
}
