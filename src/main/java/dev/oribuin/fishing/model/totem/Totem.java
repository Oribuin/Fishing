package dev.oribuin.fishing.model.totem;

import com.destroystokyo.paper.ParticleBuilder;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.Propertied;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.api.event.impl.TotemActivateEvent;
import dev.oribuin.fishing.api.event.impl.TotemDeactivateEvent;
import dev.oribuin.fishing.api.task.AsyncTicker;
import dev.oribuin.fishing.manager.TotemManager;
import dev.oribuin.fishing.model.item.ItemConstruct;
import dev.oribuin.fishing.model.item.ItemRegistry;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.model.totem.upgrade.UpgradeRegistry;
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
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jeff_media.morepersistentdatatypes.DataType.*;
import static dev.oribuin.fishing.storage.util.KeyRegistry.*;

public class Totem extends Propertied implements AsyncTicker {

    private static final Duration PARTICLE_DELAY = Duration.ofSeconds(1);
    private static final String TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY2MjRhNDRlNzdiOTVkYmUxY2M3MzU1MzY5NTNlODQwYmE2YzE5YjAxNTdjNTQ5ZDliODI1MzQ2NDhjOGNlNCJ9fX0=";
    private Location center; // The center of the totem
    private Map<TotemUpgrade, Integer> upgrades; // The upgrades of the totem

    private ArmorStand entity; // The entity that will be spawned.
    private long lastTick; // The last time the totem was ticked
    private List<Location> bounds; // The bounds of the totem
    private int rotation; // The rotation of the totem

    /**
     * Create a new totem owner with all the required values
     *
     * @param owner  The owner of the totem
     * @param center The block the totem lives
     */
    public Totem(@Nullable Location center, @Nullable Player owner) {
        // load the basic properties
        this.applyProperty(BOOLEAN, TOTEM_ACTIVE, false);
        this.applyProperty(LONG, TOTEM_LAST_ACTIVE, 0L);
        this.applyProperty(UUID, TOTEM_OWNER, owner == null ? null : owner.getUniqueId());
        this.applyProperty(STRING, TOTEM_OWNER_NAME, owner == null ? "Unknown" : owner.getName());

        // Load the upgrades
        this.upgrades = UpgradeRegistry.from(this);

        // Load the center location
        if (center != null) {
            this.center = center.toBlockLocation().add(0.5, -0.3, 0.5);
            this.bounds = this.bounds();
        }
    }

    /**
     * The method that should run everytime the task is ticked,
     * this method will be ran asynchronously
     */
    @Override
    public void tickAsync() {
        if (!this.center.isChunkLoaded()) return;
        if (this.entity == null) return;

        boolean active = this.getProperty(TOTEM_ACTIVE, false);

        // Spawn particles around the totem 
        // TODO: Move this to an animation API
        if (System.currentTimeMillis() - this.lastTick > PARTICLE_DELAY.toMillis()) {

            Color color = Color.RED;
            if (active) color = Color.LIME;
            if (!active && this.onCooldown()) color = Color.YELLOW;

            new ParticleBuilder(Particle.DUST)
                    .location(this.entity.getEyeLocation().toCenterLocation())
                    .offset(0.5, 0.5, 0.5)
                    .count(10)
                    .extra(0)
                    .color(color)
                    .spawn();

            // Spawn additional particles around the totem bounds while active
            if (active) {
                ParticleBuilder dust = this.dust(Color.LIME);
                this.bounds = this.bounds(); // regularly update the bounds of the totem
                this.bounds.forEach(x -> dust.clone().location(x.clone().add(0, 1.5, 0)).spawn());
            }

            this.lastTick = System.currentTimeMillis();
        }

        // Make the totem rotate it's head
        if (active && this.entity != null) {
            if (this.rotation >= 360) this.rotation = -1;
            this.rotation += 2;

            this.entity.setHeadRotations(Rotations.ofDegrees(0, this.rotation, 0));
        }

        // Check if the totem should be disabled
        // TODO: Move this to a disabled state
        long duration = UpgradeRegistry.DURATION_UPGRADE.calculateDuration(this).toMillis();
        long lastActive = this.getProperty(TOTEM_LAST_ACTIVE, 0L);
        if (active && System.currentTimeMillis() - lastActive > duration) {
            this.setProperty(TOTEM_ACTIVE, false);
            this.setProperty(TOTEM_LAST_ACTIVE, System.currentTimeMillis());

            this.rotation = 0;
            this.entity.setHeadRotations(Rotations.ZERO);
            this.update(); // Update the totem

            // Call the totem activate event on upgrades
            FishEventHandler.callEvents(this.upgrades, new TotemDeactivateEvent(this));
        }
    }

    /**
     * Activate the totem for the player to use
     *
     * @param player The activating player
     */
    public void activate(Player player) {
        if (this.onCooldown()) {
            FishingPlugin.get().getLogger().warning("Failed to activate totem, The totem is on cooldown.");
            return;
        }

        this.bounds = this.bounds(); // Update the bounds of the totem
        this.setProperty(TOTEM_ACTIVE, true);
        this.setProperty(TOTEM_LAST_ACTIVE, System.currentTimeMillis());
        this.update();

        // Call the totem activate event on upgrades
        FishEventHandler.callEvents(this.upgrades, new TotemActivateEvent(this, player));
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
            result.customName(Component.text(this.getProperty(TOTEM_OWNER_NAME) + "'s Totem")); // TODO: Allow configurable name
            result.setItem(EquipmentSlot.HEAD, ItemRegistry.FISHING_TOTEM.build());

            // Lock all the slots
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                result.addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING);
                result.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING);
            }

            // Save the properties to the entity
            this.saveProperties(result.getPersistentDataContainer());
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
        if (this.entity != null) {
            this.saveProperties(this.entity.getPersistentDataContainer());
        }

        FishingPlugin.get().getManager(TotemManager.class).registerTotem(this);
    }

    /**
     * Save the totem values to the itemstack
     *
     * @param itemStack The itemstack to save the values to
     */
    public void saveTo(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            FishingPlugin.get().getLogger().severe("ItemStack is null, could not save totem by owner: " + this.getProperty(TOTEM_OWNER_NAME, "Unknown"));
            return;
        }

        itemStack.editMeta(itemMeta -> this.saveProperties(itemMeta.getPersistentDataContainer()));
    }

    /**
     * Create a new totem from an entity
     *
     * @param stand The armor stand to get the values from
     *
     * @return The totem object
     */
    public static Totem fromEntity(ArmorStand stand) {
        Totem totem = new Totem(stand.getLocation().toCenterLocation(), null);
        totem.loadProperties(stand.getPersistentDataContainer());
        totem.entity(stand);
        return totem;

    }

    /**
     * Get all the placeholders for the totem
     *
     * @return The placeholders for the totem
     */
    public StringPlaceholders placeholders() {
        StringPlaceholders.Builder builder = StringPlaceholders.builder();
        builder.add("owner", this.getProperty(TOTEM_OWNER_NAME, "Unknown"));
        builder.add("active", this.getProperty(TOTEM_ACTIVE, false) ? "Active" : "Inactive");

        // Add the upgrade placeholders
        this.upgrades.forEach((upgrade, level) -> {
            builder.add("upgrade_" + upgrade.name(), level);

            // Add all the placeholders for the upgrade
            upgrade.placeholders(this)
                    .getPlaceholders()
                    .forEach((key, value) ->
                            builder.add(String.format("upgrade_%s_%s", upgrade.name(), key), value)
                    );
        });

        return builder.build();
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
     * Check if the totem is currently on cooldown
     *
     * @return If the totem is on cooldown
     *
     * @see UpgradeRegistry#COOLDOWN_UPGRADE Calculate the cooldown from the upgrade
     * @see #getCurrentCooldown() Get the current cooldown of the totem
     */
    public boolean onCooldown() {
        long lastActive = this.getProperty(TOTEM_LAST_ACTIVE, 0L);
        if (lastActive <= 0) return false;

        Duration cooldown = UpgradeRegistry.COOLDOWN_UPGRADE.calculateCooldown(this); // Get the cooldown from the upgrade
        return System.currentTimeMillis() - lastActive < cooldown.toMillis();
    }

    /**
     * Get the current cooldown timer of the totem in milliseconds
     * <p>
     *
     * @return The cooldown of the totem
     *
     * @see UpgradeRegistry#COOLDOWN_UPGRADE Calculate the cooldown from the upgrade
     * @see #onCooldown() Check if the totem is on cooldown
     */
    public long getCurrentCooldown() {
        long lastActive = this.getProperty(TOTEM_LAST_ACTIVE, 0L);
        if (lastActive <= 0) return 0;

        Duration cooldown = UpgradeRegistry.COOLDOWN_UPGRADE.calculateCooldown(this); // Get the cooldown from the upgrade
        return cooldown.toMillis() - (System.currentTimeMillis() - lastActive);
    }

    /**
     * Get the current duration of the totem in milliseconds
     *
     * @return The duration of the totem
     *
     * @see UpgradeRegistry#DURATION_UPGRADE Calculate the duration from the upgrade
     * @see #getCurrentDuration() Get the duration of the totem
     * @see #onCooldown() Check if the totem is on cooldown
     */
    public long getCurrentDuration() {
        if (!this.getProperty(TOTEM_ACTIVE, false)) return 0;

        long lastActive = this.getProperty(TOTEM_LAST_ACTIVE, 0L);
        if (lastActive <= 0) return 0;

        Duration duration = UpgradeRegistry.DURATION_UPGRADE.calculateDuration(this); // Get the duration from the upgrade
        return duration.toMillis() - (System.currentTimeMillis() - lastActive);
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

        return location.distance(this.center) <= UpgradeRegistry.RADIUS_UPGRADE.calculateRadius(this);
    }

    /**
     * Get the outer bounds of the totem in a circle
     *
     * @return The outer bounds of the totem
     */
    public List<Location> bounds() {
        if (this.center == null) return new ArrayList<>();

        int radius = UpgradeRegistry.RADIUS_UPGRADE.calculateRadius(this);

        List<Location> results = new ArrayList<>();
        int numSteps = 120;
        for (int i = 0; i < numSteps; i++) {
            double dx = MathL.cos(Math.PI * 2 * ((double) i / numSteps)) * radius;
            double dz = MathL.sin(Math.PI * 2 * ((double) i / numSteps)) * radius;

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
                        " &#4f73d6- &7Radius: &f%upgrade_radius_value% blocks",
                        " &#4f73d6- &7Duration: &f%upgrade_duration_value%",
                        " &#4f73d6- &7Cooldown: &f%upgrade_cooldown_value%",
                        ""
                )
                .glowing(true)
                .texture(TEXTURE);
    }

    public Location center() {
        return center;
    }

    public void center(Location center) {
        this.center = center;
        this.bounds = this.bounds();
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
     * Get the upgrades of the totem
     *
     * @return The upgrades of the totem
     */
    public Map<TotemUpgrade, Integer> upgrades() {
        return upgrades;
    }

    /**
     * Set the upgrades of the totem
     *
     * @param upgrades The upgrades of the totem
     */
    public void upgrades(Map<TotemUpgrade, Integer> upgrades) {
        this.upgrades = upgrades;
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
}
