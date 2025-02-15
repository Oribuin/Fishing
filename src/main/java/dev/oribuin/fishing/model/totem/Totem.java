package dev.oribuin.fishing.model.totem;

import com.destroystokyo.paper.ParticleBuilder;
import com.jeff_media.morepersistentdatatypes.DataType;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.Propertied;
import dev.oribuin.fishing.api.task.AsyncTicker;
import dev.oribuin.fishing.manager.TotemManager;
import dev.oribuin.fishing.model.item.ItemConstruct;
import dev.oribuin.fishing.model.item.ItemRegistry;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.math.MathL;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import io.papermc.paper.math.Rotations;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.jeff_media.morepersistentdatatypes.DataType.LONG;
import static com.jeff_media.morepersistentdatatypes.DataType.UUID;
import static dev.oribuin.fishing.storage.util.KeyRegistry.TOTEM_LASTACTIVE;
import static dev.oribuin.fishing.storage.util.KeyRegistry.TOTEM_OWNER;

public class Totem extends Propertied implements AsyncTicker {

    private static final Duration PARTICLE_DELAY = Duration.ofSeconds(1);
    private static final String TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY2MjRhNDRlNzdiOTVkYmUxY2M3MzU1MzY5NTNlODQwYmE2YzE5YjAxNTdjNTQ5ZDliODI1MzQ2NDhjOGNlNCJ9fX0=";

    private final UUID owner;
    private Location center; // Center of the totem
    private long lastActive; // The last time the totem was active

    private ArmorStand entity; // The entity that will be spawned.
    private long lastTick; // The last time the totem was ticked
    private List<Location> bounds; // The bounds of the totem
    private int rotation; // The rotation of the totem

    /**
     * Create a new totem owner with all the required values
     *
     * @param owner     The owner of the totem
     * @param ownerName The name of the owner
     */
    public Totem(UUID owner, String ownerName) {
        this.owner = owner;
        this.ownerName = ownerName;
        this.radius = 10;
        this.duration = Duration.ofMinutes(2);
        this.cooldown = Duration.ofHours(1);
        this.lastActive = 0;
        this.center = null;
    }

    /**
     * Create a new totem owner with all the required values
     * =
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
        if (this.entity == null) return;

        // Spawn particles around the totem
        if (System.currentTimeMillis() - this.lastTick > PARTICLE_DELAY.toMillis()) {

            Color color = Color.RED;
            if (this.active) color = Color.LIME;
            if (!this.active && this.onCooldown()) color = Color.YELLOW;

            new ParticleBuilder(Particle.DUST)
                    .location(this.entity.getEyeLocation().toCenterLocation())
                    .offset(0.5, 0.5, 0.5)
                    .count(10)
                    .extra(0)
                    .color(color)
                    .spawn();

            // Spawn additional particles around the totem bounds while active
            if (this.active) {
                ParticleBuilder dust = this.dust(Color.LIME);
                this.bounds.forEach(x -> dust.clone().location(x.clone().add(0, 1.5, 0)).spawn());
            }

            this.lastTick = System.currentTimeMillis();
        }

        // Make the totem rotate it's head
        if (this.active && this.entity != null) {
            if (this.rotation >= 360) this.rotation = -1;
            this.rotation += 2;

            this.entity.setHeadRotations(Rotations.ofDegrees(0, this.rotation, 0));
        }

        // Check if the totem should be disabled
        if (this.active && System.currentTimeMillis() - this.lastActive > this.duration.toMillis()) {
            this.active(false);
            this.lastActive = System.currentTimeMillis();
            this.rotation = 0;
            this.entity.setHeadRotations(Rotations.ZERO);
            this.update(); // Update the totem
        }
    }

    /**
     * Activate the totem for the player to use
     */
    public void activate() {
        if (this.onCooldown()) {
            FishingPlugin.get().getLogger().warning("Failed to activate totem for player: " + this.ownerName + ", The totem is on cooldown.");
            return;
        }

        this.active(true);
        this.lastActive = System.currentTimeMillis();
        this.update();
    }

    /**
     * Spawn in the totem in the world at a location
     *
     * @param location The block location to spawn the totem
     */
    public void spawn(Location location) {
        this.center = location.toBlockLocation().add(0.5, -0.3, 0.5);
        this.bounds = this.bounds();
        this.entity = this.center.getWorld().spawn(this.center, ArmorStand.class, CreatureSpawnEvent.SpawnReason.CUSTOM, result -> {
            result.setInvisible(false);
            result.setCanTick(false);
            result.setGravity(false);
            result.setVisible(false);
            result.setCustomNameVisible(true);
            result.setPersistent(true);
            result.customName(Component.text(this.ownerName + "'s Totem")); // TODO: Allow configurable name
            result.setItem(EquipmentSlot.HEAD, ItemRegistry.FISHING_TOTEM.build());

            // Lock all the slots
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                result.addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING);
                result.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING);
            }

            this.saveToContainer(result.getPersistentDataContainer());
        });

        // Create spawning particles around the totem
        long startTime = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskTimerAsynchronously(FishingPlugin.get(), task -> {

            // Remove the task if the entity or center is null
            if (this.entity == null || this.entity.isDead() || this.center == null) {
                task.cancel();
                return;
            }

            // if longer than 3 seconds cancel
            if (System.currentTimeMillis() - startTime > Duration.ofSeconds(5).toMillis()) {
                task.cancel();
                return;
            }

            // Spawn dust particles to display the totem radius
            this.bounds.forEach(x -> this.dust(Color.LIME).location(x.clone().add(0, 0.5, 0)).spawn());
        }, 0L, 5L);
    }

    /**
     * Update the totem values
     */
    public void update() {
        if (this.entity != null) this.saveToContainer(this.entity.getPersistentDataContainer());

        FishingPlugin.get().getManager(TotemManager.class).registerTotem(this);
    }

    /**
     * Save the totem values to the itemstack
     *
     * @param itemStack The itemstack to save the values to
     */
    public void saveTo(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            FishingPlugin.get().getLogger().severe("ItemStack is null, could not save totem by owner: " + this.owner);
            return;
        }

        itemStack.editMeta(itemMeta -> this.saveToContainer(itemMeta.getPersistentDataContainer()));
    }

    /**
     * Save all the totem values to the container
     *
     * @param container The container to save the values to
     */
    public void saveToContainer(PersistentDataContainer container) {
        container.set(KeyRegistry.TOTEM_OWNER, DataType.UUID, this.owner);
        container.set(KeyRegistry.TOTEM_OWNERNAME, DataType.STRING, this.ownerName);
        container.set(KeyRegistry.TOTEM_RADIUS, DataType.INTEGER, this.radius);
        container.set(KeyRegistry.TOTEM_ACTIVE, DataType.BOOLEAN, this.active);
        container.set(KeyRegistry.TOTEM_DURATION, DataType.LONG, this.duration.toMillis());
        container.set(KeyRegistry.TOTEM_COOLDOWN, DataType.LONG, this.cooldown.toMillis());
        container.set(KeyRegistry.TOTEM_LASTACTIVE, DataType.LONG, this.lastActive);
    }

    /**
     * Create a new totem from a container with all the required values
     *
     * @param container The container to get the values from
     *
     * @return The totem object
     */
    public static Totem fromContainer(PersistentDataContainer container) {
        UUID owner = container.get(KeyRegistry.TOTEM_OWNER, DataType.UUID);
        String ownerName = container.getOrDefault(KeyRegistry.TOTEM_OWNERNAME, DataType.STRING, "N/A");
        Integer radius = container.get(KeyRegistry.TOTEM_RADIUS, DataType.INTEGER);
        boolean active = container.getOrDefault(KeyRegistry.TOTEM_ACTIVE, DataType.BOOLEAN, false);
        Long duration = container.get(KeyRegistry.TOTEM_DURATION, DataType.LONG);
        Long cooldown = container.get(KeyRegistry.TOTEM_COOLDOWN, DataType.LONG);
        long lastActive = container.getOrDefault(KeyRegistry.TOTEM_LASTACTIVE, DataType.LONG, System.currentTimeMillis());

        if (owner == null || radius == null || duration == null || cooldown == null) return null;

        Totem totem = new Totem(owner, ownerName);
        totem.active(active);
        totem.radius(radius);
        totem.duration(Duration.ofMillis(duration));
        totem.cooldown(Duration.ofMillis(cooldown));
        totem.lastActive(lastActive);
        return totem;
    }

    /**
     * Create a new totem from an entity
     *
     * @param stand The armor stand to get the values from
     *
     * @return The totem object
     */
    public static Totem fromEntity(ArmorStand stand) {
        Totem totem = fromContainer(stand.getPersistentDataContainer());
        if (totem == null) return null;

        totem.entity(stand);
        totem.center(stand.getLocation());
        return totem;

    }

    public StringPlaceholders placeholders() {
        return StringPlaceholders.builder()
                .add("owner", this.ownerName)
                .add("radius", this.radius)
                .add("duration", FishUtils.formatTime(this.duration.toMillis()))
                .add("cooldown", FishUtils.formatTime(this.cooldown.toMillis()))
                .add("active", this.active ? "Yes" : "No")
                .add("timer", FishUtils.formatTime(this.duration.toMillis() - (System.currentTimeMillis() - this.lastActive)))
                .add("cooldown_timer", FishUtils.formatTime(this.cooldown.toMillis() - (System.currentTimeMillis() - this.lastActive)))
                .build();

    }

    /**
     * Create a new particle builder with the dust particle
     *
     * @param color The color of the dust
     *
     * @return The particle builder
     */
    private ParticleBuilder dust(Color color) {
        return new ParticleBuilder(Particle.DUST)
                .count(1)
                .extra(0)
                .offset(0, 0, 0.)
                .color(color)
                .clone();
    }

    /**
     * Test if the totem is on cooldown
     *
     * @return If the totem is on cooldown
     */
    public boolean onCooldown() {
        if (this.lastActive == 0) return false;

        return System.currentTimeMillis() - this.lastActive < this.cooldown.toMillis();
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
        if (this.center == null) return new ArrayList<>();

        List<Location> results = new ArrayList<>();
        int numSteps = 120;
        for (int i = 0; i < numSteps; i++) {
            double dx = MathL.cos(Math.PI * 2 * ((double) i / numSteps)) * this.radius;
            double dz = MathL.sin(Math.PI * 2 * ((double) i / numSteps)) * this.radius;

            results.add(this.center.clone().add(dx, 0, dz));
        }

        return results;
    }

    public static ItemConstruct defaultItem() {
        return ItemConstruct.of(Material.PLAYER_HEAD)
                .name("&f[&#4f73d6&lFishing Totem&f]")
                .lore(
                        "&7Place in the world to create local",
                        "&7booster for players within it's radius.",
                        "",
                        "&#4f73d6Information",
                        " &#4f73d6- &7Owner: &f%owner%",
                        " &#4f73d6- &7Radius: &f%radius% blocks",
                        " &#4f73d6- &7Duration: &f%duration%",
                        " &#4f73d6- &7Cooldown: &f%cooldown%",
                        ""
                )
                .glowing(true)
                .texture(TEXTURE);
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
        this.bounds = this.bounds();
    }

    public int radius() {
        return this.radius;
    }

    public void radius(int radius) {
        this.radius = radius;
        this.bounds = this.bounds();
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

    public void entity(ArmorStand entity) {
        this.entity = entity;
        if (entity != null) {
            this.center = entity.getLocation();
            this.bounds = this.bounds();
        }
    }

    /**
     * The delay between each tick, Set to Duration#ZERO for no delay
     *
     * @return The delay between each tick
     */
    @Override
    public Duration delay() {
        return Duration.ZERO;
    }

    /**
     * Register all the default properties to the class so they can be loaded and saved
     *
     * @param container The container to register the properties to
     *
     * @see #saveProperties(PersistentDataContainer)
     */
    @Override
    public void defineProperties(PersistentDataContainer container) {
        this.registerProperty(UUID, TOTEM_OWNER, this.owner);
        this.registerProperty(LONG, TOTEM_LASTACTIVE, this.lastActive);
    }
    
}
