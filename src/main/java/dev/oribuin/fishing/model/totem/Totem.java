package dev.oribuin.fishing.model.totem;

import dev.oribuin.fishing.api.Propertied;
import dev.oribuin.fishing.storage.persistent.FishDataType;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static dev.oribuin.fishing.storage.util.KeyRegistry.*;

public class Totem extends Propertied { // extends Propertied implements AsyncTicker, Animated

    private Location position;

    public Totem(Location position, UUID owner) {
        super();
        this.position = position;
        this.registerType(TOTEM_OWNER, owner);
        this.registerType(TOTEM_ACTIVE, false);
        this.registerType(TOTEM_LAST_ACTIVE, 0L);
        this.registerType(TOTEM_SKIN, "default");
        this.registerType(TOTEM_LEVEL, 1);
        this.registerType(TOTEM_PRIVACY, TotemPrivacy.PUBLIC);
        this.registerType(TOTEM_UPGRADES, new HashMap<>());
        this.registerType(TOTEM_USERS, new HashSet<>());
    }

    /**
     * Store a {@link FishDataType} into a {@link PersistentDataContainer}
     *
     * @param container The container to store the serializer in
     */
    @Override
    public <T extends PersistentDataContainer> void serialize(T container) {
        super.serialize(container);

        Map<String, Integer> upgrades = this.getValue(container, TOTEM_UPGRADES);
        if (upgrades != null && !upgrades.isEmpty()) {
            // TODO: Serialize each upgrade
        }
    }


    //
    //    private static final Duration PARTICLE_DELAY = Duration.ofSeconds(1);
    //    private Location center; // The center of the totem
    //    private Map<TotemUpgrade, Integer> upgrades; // The upgrades of the totem
    //
    //    private ArmorStand entity; // The entity that will be spawned.
    //    private long lastTick; // The last time the totem was ticked
    //    private List<Location> bounds; // The bounds of the totem
    //    private int rotation; // The rotation of the totem
    //
    //    /**
    //     * Create a new totem owner with all the required values
    //     *
    //     * @param owner  The owner of the totem
    //     * @param center The block the totem lives
    //     */
    //    public Totem(@Nullable Location center, @Nullable Player owner) {
    //        // load the basic properties
    //        this.applyProperty(BOOLEAN, TOTEM_ACTIVE, false);
    //        this.applyProperty(LONG, TOTEM_LAST_ACTIVE, 0L);
    //        this.applyProperty(UUID, TOTEM_OWNER, owner == null ? null : owner.getUniqueId());
    //        this.applyProperty(STRING, TOTEM_OWNER_NAME, owner == null ? "Unknown" : owner.getName());
    //        this.applyProperty(STRING, TOTEM_PRIVACY, );
    //
    //        // Load the upgrades
    //        this.upgrades = UpgradeRegistry.from(this);
    //
    //        // Load the center location
    //        if (center != null) {
    //            this.center = center.toBlockLocation().add(0.5, -0.3, 0.5);
    //            this.bounds = this.getBounds();
    //        }
    //    }
    //
    //    /**
    //     * The method that should run everytime the task is ticked,
    //     * this method will be ran asynchronously
    //     */
    //    @Override
    //    public void tickAsync() {
    //        if (!this.center.isChunkLoaded()) return;
    //        if (this.entity == null) return;
    //
    //        boolean active = this.getProperty(TOTEM_ACTIVE, false);
    //
    //        // Spawn particles around the totem 
    //        // TODO: Move this to an animation API
    //        if (System.currentTimeMillis() - this.lastTick > PARTICLE_DELAY.toMillis()) {
    //
    //            Color color = Color.RED;
    //            if (active) color = Color.LIME;
    //            if (!active && this.onCooldown()) color = Color.YELLOW;
    //
    //            new ParticleBuilder(Particle.DUST)
    //                    .location(this.entity.getEyeLocation().toCenterLocation())
    //                    .offset(0.5, 0.5, 0.5)
    //                    .count(10)
    //                    .extra(0)
    //                    .color(color)
    //                    .spawn();
    //
    //            // Spawn additional particles around the totem bounds while active
    //            if (active) {
    //                ParticleBuilder dust = this.getDust(Color.LIME);
    //                this.bounds = this.getBounds(); // regularly update the bounds of the totem
    //                this.bounds.forEach(x -> dust.clone().location(x.clone().add(0, 1.5, 0)).spawn());
    //            }
    //
    //            this.lastTick = System.currentTimeMillis();
    //        }
    //
    //        // Make the totem rotate it's head
    //        if (active && this.entity != null) {
    //            if (this.rotation >= 360) this.rotation = -1;
    //            this.rotation += 2;
    //
    //            this.entity.setHeadRotations(Rotations.ofDegrees(0, this.rotation, 0));
    //        }
    //
    //        // Check if the totem should be disabled
    //        // TODO: Move this to a disabled state
    //        long duration = UpgradeRegistry.DURATION_UPGRADE.calculateDuration(this).toMillis();
    //        long lastActive = this.getProperty(TOTEM_LAST_ACTIVE, 0L);
    //        if (active && System.currentTimeMillis() - lastActive > duration) {
    //            this.setProperty(TOTEM_ACTIVE, false);
    //            this.setProperty(TOTEM_LAST_ACTIVE, System.currentTimeMillis());
    //
    //            this.rotation = 0;
    //            this.entity.setHeadRotations(Rotations.ZERO);
    //            this.update(); // Update the totem
    //
    //            // Call the totem activate event on upgrades
    //            FishEventHandler.callEvents(this.upgrades, new TotemDeactivateEvent(this));
    //        }
    //    }
    //
    //    /**
    //     * Activate the totem for the player to use
    //     *
    //     * @param player The activating player
    //     */
    //    public void activate(Player player) {
    //        if (this.onCooldown()) {
    //            FishingPlugin.get().getLogger().warning("Failed to activate totem, The totem is on cooldown.");
    //            return;
    //        }
    //
    //        this.bounds = this.getBounds(); // Update the bounds of the totem
    //        this.setProperty(TOTEM_ACTIVE, true);
    //        this.setProperty(TOTEM_LAST_ACTIVE, System.currentTimeMillis());
    //        this.update();
    //
    //        // Call the totem activate event on upgrades
    //        FishEventHandler.callEvents(this.upgrades, new TotemActivateEvent(this, player));
    //    }
    //
    //    /**
    //     * Spawn in the totem in the world at a location
    //     *
    //     * @param location The block location to spawn the totem
    //     */
    //    public void spawn(Location location) {
    //        this.center = location.toBlockLocation().add(0.5, -0.3, 0.5);
    //        this.bounds = this.getBounds();
    //        this.entity = this.center.getWorld().spawn(this.center, ArmorStand.class, CreatureSpawnEvent.SpawnReason.CUSTOM, result -> {
    //            result.setInvisible(false);
    //            result.setCanTick(false);
    //            result.setGravity(false);
    //            result.setVisible(false);
    //            result.setCustomNameVisible(true);
    //            result.setPersistent(true);
    //            result.customName(Component.text(this.getProperty(TOTEM_OWNER_NAME) + "'s Totem")); // TODO: Allow configurable name
    //            result.setItem(EquipmentSlot.HEAD, TotemConfig.get().getTotemItem().build(this.placeholders()));
    //
    //            // Lock all the slots
    //            for (EquipmentSlot slot : EquipmentSlot.values()) {
    //                result.addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING);
    //                result.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING);
    //            }
    //
    //            // Save the properties to the entity
    //            this.saveProperties(result.getPersistentDataContainer());
    //        });
    //
    //        // Create spawning particles around the totem
    //        long startTime = System.currentTimeMillis();
    //        // TODO: Spawn particles in a better way than a task
    //        Bukkit.getScheduler().runTaskTimerAsynchronously(FishingPlugin.get(), task -> {
    //
    //            // Remove the task if the entity or center is null
    //            if (this.entity == null || this.entity.isDead() || this.center == null) {
    //                task.cancel();
    //                return;
    //            }
    //
    //            // if longer than 3 seconds cancel
    //            if (System.currentTimeMillis() - startTime > Duration.ofSeconds(5).toMillis()) {
    //                task.cancel();
    //                return;
    //            }
    //
    //            // Spawn dust particles to display the totem radius
    //            // TODO: RadiusParticleAnimation 
    //            this.bounds.forEach(x -> this.getDust(Color.LIME).location(x.clone().add(0, 0.5, 0)).spawn());
    //        }, 0L, 5L);
    //    }
    //
    //    /**
    //     * Update the totem values
    //     */
    //    public void update() {
    //        if (this.entity != null) {
    //            this.saveProperties(this.entity.getPersistentDataContainer());
    //        }
    //
    //        FishingPlugin.get().getTotemManager().registerTotem(this);
    //    }
    //
    //    /**
    //     * Save the totem values to the itemstack
    //     *
    //     * @param itemStack The itemstack to save the values to
    //     */
    //    public void saveTo(ItemStack itemStack) {
    //        if (itemStack == null || itemStack.getItemMeta() == null) {
    //            FishingPlugin.get().getLogger().severe("ItemStack is null, could not save totem by owner: " + this.getProperty(TOTEM_OWNER_NAME, "Unknown"));
    //            return;
    //        }
    //
    //        itemStack.editMeta(itemMeta -> this.saveProperties(itemMeta.getPersistentDataContainer()));
    //    }
    //
    //    /**
    //     * Create a new totem from an entity
    //     *
    //     * @param stand The armor stand to get the values from
    //     *
    //     * @return The totem object
    //     */
    //    @Nullable
    //    public static Totem fromEntity(@NotNull ArmorStand stand) {
    //        PersistentDataContainer container = stand.getPersistentDataContainer();
    //        if (!container.has(TOTEM_ACTIVE)) return null;
    //
    //        Totem totem = new Totem(stand.getLocation().toCenterLocation(), null);
    //        totem.loadProperties(container);
    //        totem.setEntity(stand);
    //        return totem;
    //    }
    //
    //    /**
    //     * Get all the placeholders for the totem
    //     *
    //     * @return The placeholders for the totem
    //     */
    //    public Placeholders placeholders() {
    //        Placeholders.Builder builder = Placeholders.builder();
    //        builder.add("owner", this.getProperty(TOTEM_OWNER_NAME, "Unknown"));
    //        builder.add("active", this.getProperty(TOTEM_ACTIVE, false) ? "Active" : "Inactive");
    //
    //        // Add the upgrade placeholders
    //        this.upgrades.forEach((upgrade, level) -> {
    //            builder.add("upgrade_" + upgrade.getName(), level);
    //
    //            // Add all the placeholders for the upgrade
    //            upgrade.getPlaceholders(this)
    //                    .getPlaceholders()
    //                    .forEach((key, value) ->
    //                            builder.add(String.format("upgrade_%s_%s", upgrade.getName(), key), value)
    //                    );
    //        });
    //
    //        return builder.build();
    //    }
    //
    //    /**
    //     * Create a new particle builder with the dust particle
    //     *
    //     * @param color The color of the dust
    //     *
    //     * @return The particle builder
    //     */
    //    private ParticleBuilder getDust(Color color) {
    //        return new ParticleBuilder(Particle.DUST)
    //                .count(1)
    //                .extra(0)
    //                .offset(0, 0, 0.)
    //                .color(color)
    //                .clone();
    //    }
    //
    //    /**
    //     * Check if the totem is currently on cooldown
    //     *
    //     * @return If the totem is on cooldown
    //     *
    //     * @see UpgradeRegistry#COOLDOWN_UPGRADE Calculate the cooldown from the upgrade
    //     * @see #getCurrentCooldown() Get the current cooldown of the totem
    //     */
    //    public boolean onCooldown() {
    //        long lastActive = this.getProperty(TOTEM_LAST_ACTIVE, 0L);
    //        if (lastActive <= 0) return false;
    //
    //        Duration cooldown = UpgradeRegistry.COOLDOWN_UPGRADE.calculateCooldown(this); // Get the cooldown from the upgrade
    //        return System.currentTimeMillis() - lastActive < cooldown.toMillis();
    //    }
    //
    //    /**
    //     * Get the current cooldown timer of the totem in milliseconds
    //     * <p>
    //     *
    //     * @return The cooldown of the totem
    //     *
    //     * @see UpgradeRegistry#COOLDOWN_UPGRADE Calculate the cooldown from the upgrade
    //     * @see #onCooldown() Check if the totem is on cooldown
    //     */
    //    public long getCurrentCooldown() {
    //        long lastActive = this.getProperty(TOTEM_LAST_ACTIVE, 0L);
    //        if (lastActive <= 0) return 0;
    //
    //        Duration cooldown = UpgradeRegistry.COOLDOWN_UPGRADE.calculateCooldown(this); // Get the cooldown from the upgrade
    //        return cooldown.toMillis() - (System.currentTimeMillis() - lastActive);
    //    }
    //
    //    /**
    //     * Get the current duration of the totem in milliseconds
    //     *
    //     * @return The duration of the totem
    //     *
    //     * @see UpgradeRegistry#DURATION_UPGRADE Calculate the duration from the upgrade
    //     * @see #getCurrentDuration() Get the duration of the totem
    //     * @see #onCooldown() Check if the totem is on cooldown
    //     */
    //    public long getCurrentDuration() {
    //        if (!this.getProperty(TOTEM_ACTIVE, false)) return 0;
    //
    //        long lastActive = this.getProperty(TOTEM_LAST_ACTIVE, 0L);
    //        if (lastActive <= 0) return 0;
    //
    //        Duration duration = UpgradeRegistry.DURATION_UPGRADE.calculateDuration(this); // Get the duration from the upgrade
    //        return duration.toMillis() - (System.currentTimeMillis() - lastActive);
    //    }
    //
    //    /**
    //     * Test if the location is within the radius of the totem
    //     *
    //     * @param location The location to test
    //     *
    //     * @return If the location is within the radius of the totem
    //     */
    //    public boolean isWithinRadius(Location location) {
    //        // Radius will be in a circle around the center
    //        if (location.getWorld() != this.center.getWorld()) return false;
    //
    //        return location.distance(this.center) <= UpgradeRegistry.RADIUS_UPGRADE.calculateRadius(this);
    //    }
    //
    //    /**
    //     * Get the outer bounds of the totem in a circle
    //     *
    //     * @return The outer bounds of the totem
    //     */
    //    public List<Location> getBounds() {
    //        if (this.center == null) return new ArrayList<>();
    //
    //        int radius = UpgradeRegistry.RADIUS_UPGRADE.calculateRadius(this);
    //
    //        List<Location> results = new ArrayList<>();
    //        int numSteps = 120;
    //        for (int i = 0; i < numSteps; i++) {
    //            double dx = MathL.cos(Math.PI * 2 * ((double) i / numSteps)) * radius;
    //            double dz = MathL.sin(Math.PI * 2 * ((double) i / numSteps)) * radius;
    //
    //            results.add(this.center.clone().add(dx, 0, dz));
    //        }
    //
    //        return results;
    //    }
    //
    //    public Location getCenter() {
    //        return center;
    //    }
    //
    //    public void setCenter(Location center) {
    //        this.center = center;
    //        this.bounds = this.getBounds();
    //    }
    //
    //    public ArmorStand getEntity() {
    //        return this.entity;
    //    }
    //
    //    public void setEntity(ArmorStand entity) {
    //        this.entity = entity;
    //        if (entity != null) {
    //            this.setCenter(entity.getLocation());
    //        }
    //    }
    //
    //    /**
    //     * Get the upgrades of the totem
    //     *
    //     * @return The upgrades of the totem
    //     */
    //    public Map<TotemUpgrade, Integer> getUpgrades() {
    //        return upgrades;
    //    }
    //
    //    /**
    //     * Set the upgrades of the totem
    //     *
    //     * @param upgrades The upgrades of the totem
    //     */
    //    public void setUpgrades(Map<TotemUpgrade, Integer> upgrades) {
    //        this.upgrades = upgrades;
    //    }
    //
    //    /**
    //     * The delay between each tick, Set to Duration#ZERO for no delay
    //     *
    //     * @return The delay between each tick
    //     */
    //    public Duration getDelay() {
    //        return Duration.ofMillis(500);
    //    }
    //
    //    /**
    //     * Create a list of animations to be used in the module
    //     *
    //     * @return A list of animations
    //     */
    //    @Override
    //    public @NotNull List<Supplier<Animation>> createAnimations() {
    //        return new ArrayList<>();
    //    }
    //
    //    /**
    //     * Get the source location of the animation to be used
    //     *
    //     * @return The source location
    //     */
    //    @Override
    //    public @NotNull Supplier<Location> getSource() {
    //        return this::getCenter;
    //    }

}
