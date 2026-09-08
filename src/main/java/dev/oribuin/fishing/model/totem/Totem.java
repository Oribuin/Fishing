package dev.oribuin.fishing.model.totem;

import com.destroystokyo.paper.ParticleBuilder;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.api.event.impl.FishCatchEvent;
import dev.oribuin.fishing.api.event.impl.InitialFishCatchEvent;
import dev.oribuin.fishing.api.event.impl.TotemActivateEvent;
import dev.oribuin.fishing.api.event.impl.TotemDeactivateEvent;
import dev.oribuin.fishing.api.task.AsyncTicker;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.impl.TotemConfig;
import dev.oribuin.fishing.model.cosmetic.skin.TotemSkin;
import dev.oribuin.fishing.model.totem.upgrade.Toggleable;
import dev.oribuin.fishing.model.totem.upgrade.TotemTickable;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgradeRegistry;
import dev.oribuin.fishing.model.totem.upgrade.impl.TUpgradeCooldown;
import dev.oribuin.fishing.model.totem.upgrade.impl.TUpgradeDuration;
import dev.oribuin.fishing.model.totem.upgrade.impl.TUpgradeRadius;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.scheduler.task.ScheduledTask;
import dev.oribuin.fishing.storage.persistent.PDCSerializable;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.NMSUtil;
import dev.oribuin.fishing.util.Placeholders;
import dev.oribuin.fishing.util.math.MathL;
import io.papermc.paper.math.Rotations;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.jeff_media.morepersistentdatatypes.DataType.TAG_CONTAINER;
import static dev.oribuin.fishing.storage.util.KeyRegistry.*;

public class Totem extends FishEventHandler implements PDCSerializable, AsyncTicker { // extends Propertied implements AsyncTicker, Animated

    private Location position;
    private UUID owner;
    private boolean active;
    private long lastActive;
    private int level;
    private TotemPrivacy privacy;
    private TotemSkin skin;
    private String ownerName;
    private String displayName;
    private Map<Integer, ItemStack> bag;
    private Set<UUID> users;
    private Map<String, TotemUpgrade> upgrades;
    private UUID displayId;
    private boolean confirmedActivate;
    private ArmorStand display;
    private ScheduledTask foliaTask;
    private int bagCapacity;

    /**
     * Create a new totem from an armor stand with a container
     *
     * @param display The armor stand that is a totem
     */
    public Totem(ArmorStand display) {
        this(display, display.getPersistentDataContainer());
        this.displayId = display.getUniqueId();
        this.display = display;
    }

    /**
     * Create a new totem from an armor stand with a container
     *
     * @param display   The armor stand that is a totem
     * @param container The container with the data from the totem
     */
    public Totem(ArmorStand display, PersistentDataContainer container) {
        this(display.getLocation().toCenterLocation(), container);
        this.displayId = display.getUniqueId();
        this.display = display;
        this.readContainer(container);
    }

    /**
     * Create a new totem at a specified position with a container
     *
     * @param position  The position to spawn the totem at
     * @param container The container with the data from the totem
     * @param owner     The new owner of the container
     */
    public Totem(Location position, PersistentDataContainer container, UUID owner) {
        this(position, container);
        this.owner = owner;
        if (this.owner != null) {
            this.ownerName = Optional.ofNullable(Bukkit.getPlayer(this.owner)).map(Player::getName).orElse("N/A");
        } else {
            this.ownerName = "N/A";
        }
        this.readContainer(container);
    }

    /**
     * Create a new totem at a specified position with a container
     *
     * @param position  The position to spawn the totem at
     * @param container The container with the data from the totem
     */
    public Totem(Location position, PersistentDataContainer container) {
        this.position = position;
        this.active = false;
        this.lastActive = 0L;
        this.level = 1;
        this.privacy = TotemPrivacy.FRIENDS_ONLY;
        this.ownerName = "N/A";
        this.displayName = null;
        this.bag = new HashMap<>();
        this.bagCapacity = 9;
        this.users = new HashSet<>();
        this.upgrades = new LinkedHashMap<>(TotemUpgradeRegistry.getDefault());
        this.confirmedActivate = false;
        this.readContainer(container);
        this.registerListener(InitialFishCatchEvent.class, this::onInitialCatch);
        this.registerListener(FishCatchEvent.class, this::onFishCatch);
    }

    /**
     * Get the effective radius of the totem
     *
     * @return The totem radius
     */
    public double getRadius() {
        TUpgradeRadius upgrade = this.getUpgrade(TUpgradeRadius.class);
        return upgrade != null ? upgrade.getRadius() : 2.5;
    }

    /**
     * Get the duration of the totem
     *
     * @return The duration of the totem
     */
    public Duration getDuration() {
        TUpgradeDuration upgrade = this.getUpgrade(TUpgradeDuration.class);
        return upgrade != null ? upgrade.getDuration() : Duration.ofMinutes(5);
    }

    /**
     * Get the cooldown of the totem
     *
     * @return The duration of the totem
     */
    public Duration getCooldown() {
        TUpgradeCooldown upgrade = this.getUpgrade(TUpgradeCooldown.class);
        return upgrade != null ? upgrade.getCooldown() : Duration.ofHours(1);
    }

    /**
     * Get an upgrade for the totem
     *
     * @param upgradeClass The class for the upgrade
     * @param <T>          The totem upgrade type
     *
     * @return The upgrade if available
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends TotemUpgrade> T getUpgrade(@NotNull Class<T> upgradeClass) {
        String id = TotemUpgradeRegistry.getUpgradeId(upgradeClass);
        if (id == null) return null;

        TotemUpgrade upgrade = this.upgrades.get(id);
        return upgrade != null ? (T) upgrade : null;
    }

    /**
     * The method that should run everytime the task is ticked,
     * this method will be run asynchronously
     */
    @Override
    public void tickAsync() {

        // display doesnt exist, stop ticking
        if (this.display == null || !this.display.isValid() || this.display.isDead()) {
            if (this.foliaTask != null) PluginScheduler.cancelNull(this.foliaTask);
        }

        // Deactivate the totem when unused
        long duration = this.getDuration().toMillis();
        if (this.active && System.currentTimeMillis() - this.lastActive > duration) {
            this.active = false;
            this.lastActive = System.currentTimeMillis();
            if (this.display != null) this.writeContainer(this.display.getPersistentDataContainer()); // Update the totem
            if (this.foliaTask != null) this.foliaTask = PluginScheduler.cancelNull(this.foliaTask);

            // Call the totem activate event on upgrades
            TotemDeactivateEvent deactivateEvent = new TotemDeactivateEvent(this);
            deactivateEvent.callEvent();
            return;
        }

        // Spawn particles around the totem 
        // TODO: Move this to an animation API

        Color color = Color.RED;
        if (active) color = Color.LIME;
        if (!active && this.onCooldown()) color = Color.YELLOW;

        new ParticleBuilder(Particle.DUST)
                .location(this.position.clone().add(0, 1, 0))
                .offset(0.5, 0.5, 0.5)
                .count(10)
                .extra(0)
                .color(color)
                .spawn();

        // Spawn additional particles around the totem bounds while active
        // TODO: Active particle builder
        if (active) {
            ParticleBuilder dust = this.getDust(Color.LIME);
            this.getBounds().forEach(x -> dust.clone().location(x.clone().add(0, 1.5, 0)).spawn());

            Rotations rotations = this.display.getHeadRotations();
            double y = rotations.y() >= 360 ? 0 : rotations.y() + 2;
            this.display.setHeadRotations(Rotations.ofDegrees(0, y, 0));

            this.upgrades.values().forEach(totemUpgrade -> {
                if (totemUpgrade instanceof Toggleable toggleable && !toggleable.isActivated()) return;
                if (totemUpgrade instanceof TotemTickable tickable) tickable.tick(this);
            });
        }
    }

    /**
     * Activate the totem for the player to use
     *
     * @param player The activating player
     */
    public void activate(Player player) {
        ArmorStand display = this.getDisplay();
        if (display == null || display.isDead()) return;

        if (this.onCooldown()) {
            PluginMessages.get().getTotem().getOnCooldown().send(player);
            return;
        }

        Totem closestActive = FishingPlugin.get().getTotemManager().getClosestActive(this.position);
        if (closestActive != null) {
            boolean isInRadius = this.isWithinRadius(closestActive.getPosition()) || closestActive.isWithinRadius(this.position);
            // Checks whether either totems are within each other's bounds
            if (isInRadius && !confirmedActivate) {
                PluginMessages.get().getTotem().getOtherActiveNearby().send(player);
                confirmedActivate = true;
                return;
            }

        }

        this.confirmedActivate = false;
        this.active = true;
        this.lastActive = System.currentTimeMillis();
        this.writeContainer(display.getPersistentDataContainer());

        if (NMSUtil.isFolia()) {
            if (this.foliaTask != null) this.foliaTask = PluginScheduler.cancelNull(this.foliaTask);

            this.foliaTask = PluginScheduler.get().runTaskTimerAtLocation(this.display.getLocation(), () -> {
                if (!this.active || this.display == null) {
                    this.foliaTask = PluginScheduler.cancelNull(this.foliaTask);
                    return;
                }

                if (this.position.isChunkLoaded()) this.tickAsync();
            }, this.getTickDelay().toMillis(), this.getTickDelay().toMillis(), TimeUnit.MILLISECONDS);
        }


        // Tell the player they activated the totem
        PluginMessages.get().getTotem().getActivated().send(player, "time", FishUtils.formatTime(this.getDuration().toMillis()));

        new TotemActivateEvent(this, player).callEvent();
    }

    /**
     * Deposit an itemstack into the totem
     *
     * @param stack The stack to deposit
     */
    public void depositBag(@NotNull ItemStack stack) {
        if (this.display == null || this.display.isDead()) return;

        int left = stack.getAmount();
        for (int i = 0; i < this.bagCapacity; i++) {
            if (left <= 0) break;

            // Slot is empty, put the itemstack
            ItemStack current = this.bag.get(i);
            if (current == null || current.getType().isAir()) {
                this.bag.put(i, stack);
                stack.setAmount(0);
                break;
            }

            // Check additional slots
            if (current.getAmount() == current.getMaxStackSize()) continue; // Is the current item already at max capacity
            if (current.getType() != stack.getType()) continue; // Are the items the same type
            if (!current.isSimilar(stack)) continue; // Do the items match

            int combined = stack.getAmount() + current.getAmount();
            if (combined > stack.getMaxStackSize()) {
                current.setAmount(stack.getMaxStackSize());
                left = combined - stack.getMaxStackSize();
            } else {
                current.setAmount(combined);
                left -= current.getAmount();
            }

            this.bag.put(i, current);
        }

        stack.setAmount(left);
        this.writeContainer(this.getDisplay().getPersistentDataContainer());
    }

    /**
     * Withdraw an itemstack from the totem's bag
     *
     * @param slot The stack to withdraw
     *
     * @return The resulting itemstack
     */
    @Nullable
    public ItemStack withdrawBag(int slot) {
        if (this.display == null || this.display.isDead()) return null;

        ItemStack current = this.bag.get(slot);
        if (current == null || current.getType().isAir()) return null; // Are the items the same type
        ItemStack result = this.bag.remove(slot);
        this.writeContainer(this.getDisplay().getPersistentDataContainer());
        return result;
    }

    /**
     * The functionality provided when a player is first starting to catch a fish, Use this to determine how many fish should be generated.
     * <p>
     * Use {@link InitialFishCatchEvent#setAmountToCatch(int)} to set the amount of fish to catch
     *
     * @param event The event that was called when the fish was caught
     */
    @Override
    public void onInitialCatch(InitialFishCatchEvent event) {
        Location hook = event.getHook().getLocation().clone().add(0, 1, 0);
        Location position = this.position.clone().add(0, 1, 0);
        int distance = (int) hook.distance(position);
        Vector direction = hook.toVector().clone().subtract(position.toVector()).normalize();
        List<Location> line = new ArrayList<>();
        for (double i = 0; i < distance; i += 0.5) {
            Location location = position.clone().add(direction.clone().multiply(i));
            line.add(location);
        }

        ParticleBuilder builder = new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
                .location(position)
                .receivers((int) this.getRadius() + 5)
                .colorTransition(
                        Color.fromRGB(162, 191, 254),
                        Color.fromRGB(193, 225, 193)
                )
                .extra(0);

        line.forEach(location -> builder.clone().location(location).spawn());
    }

    /**
     * The functionality provided when a player has finished catching a fish, Use this to modify the rewards given to the player once caught
     * <p>
     * Use {@link FishCatchEvent#setCatchEntropy(int)} to change the entropy received
     * <p>
     * Use {@link FishCatchEvent#setNaturalExp(float)} to change the minecraft experience received
     * <p>
     * Use {@link FishCatchEvent#setCatchExp(int)} to change the fishing experience received
     *
     * @param event The event that was called when the fish was caught
     */
    @Override
    public void onFishCatch(FishCatchEvent event) {

        // Increase the experience gain by 15%
        double multiplier = TotemConfig.get().getExperienceMultiplier();
        event.setCatchExp((int) (event.getCatchExp() + (event.getCatchExp() * multiplier)));

        // region Particle effects on catch
        Location hook = event.getHook().getLocation().clone().add(0, 1, 0);
        Location position = this.position.clone().add(0, 1, 0);
        int distance = (int) hook.distance(position);
        Vector direction = hook.toVector().clone().subtract(position.toVector()).normalize();
        List<Location> line = new ArrayList<>();
        for (double i = 0; i < distance; i += 0.5) {
            Location location = position.clone().add(direction.clone().multiply(i));
            line.add(location);
        }

        ParticleBuilder builder = new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
                .location(position)
                .receivers((int) this.getRadius() + 5)
                .colorTransition(
                        Color.fromRGB(162, 191, 254),
                        Color.fromRGB(193, 225, 193)
                )
                .extra(0);

        ScheduledTask task = PluginScheduler.get().runTaskTimerAsync(() -> line.forEach(location -> builder
                .clone()
                .location(location)
                .spawn()
        ), 500, 500, TimeUnit.MILLISECONDS);

        PluginScheduler.get().runTaskLater(task::cancel, 3, TimeUnit.SECONDS);
        // endregion
    }

    /**
     * Update the armourstand in the plugin
     *
     * @return The stand to update
     */
    public Consumer<ArmorStand> updateStand() {
        return result -> {
            result.setInvisible(false);
            result.setCanTick(false);
            result.setGravity(false);
            result.setVisible(false);
            result.setCustomNameVisible(true);
            result.setPersistent(true);
            result.customName(FishUtils.kyorify(this.displayName));

            // TODO: Use the totem skin
            result.setItem(EquipmentSlot.HEAD, TotemConfig.get().getTotemItem().create());

            // Lock all the slots
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                result.addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING);
                result.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING);
            }
        };
    }

    /**
     * Spawn in the totem in the world at a location
     *
     * @param location The block location to spawn the totem
     */
    public void spawn(Location location) {
        if (this.displayName == null) {
            this.displayName = this.ownerName + "'s Totem";
        }

        this.position = location.toBlockLocation().add(0.5, -0.3, 0.5);
        ArmorStand stand = this.position.getWorld().spawn(this.position, ArmorStand.class, CreatureSpawnEvent.SpawnReason.CUSTOM, result -> {
            // Save the properties to the entity
            this.updateStand().accept(result);
            this.displayId = result.getUniqueId();
            this.writeContainer(result.getPersistentDataContainer());
        });

        // If the totem is active then actiate the task
        if (this.isActive() && NMSUtil.isFolia()) {
            if (this.foliaTask != null) this.foliaTask = PluginScheduler.cancelNull(this.foliaTask);

            this.foliaTask = PluginScheduler.get().runTaskTimerAtLocation(this.display.getLocation(), () -> {
                if (!this.active || this.display == null) {
                    this.foliaTask = PluginScheduler.cancelNull(this.foliaTask);
                    return;
                }

                if (this.position.isChunkLoaded()) this.tickAsync();
            }, this.getTickDelay().toMillis(), this.getTickDelay().toMillis(), TimeUnit.MILLISECONDS);

            return;
        }

        // Create spawning particles around the totem
        List<Location> bounds = this.getBounds();
        ScheduledTask repeating = PluginScheduler.get().runTaskTimerAsync(() -> {
            // dont display particles if running
            if (stand.isDead() || this.position == null || !this.position.isChunkLoaded()) return;

            bounds.forEach(x ->
                    this.getDust(Color.LIME)
                            .location(x.clone().add(0, 0.5, 0))
                            .spawn()
            );
        }, 0, 250, TimeUnit.MILLISECONDS);

        PluginScheduler.get().runTaskLaterAsync(repeating::cancel, 3, TimeUnit.SECONDS);
    }

    /**
     * Save the totem values to the itemstack
     *
     * @param itemStack The itemstack to save the values to
     */
    public void saveTo(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            FishingPlugin.get().getLogger().severe("ItemStack is null, could not save totem by owner: " + this.ownerName);
            return;
        }

        itemStack.editMeta(itemMeta -> this.writeContainer(itemMeta.getPersistentDataContainer()));
    }

    /**
     * Create a new particle builder with the dust particle
     *
     * @param color The color of the dust
     *
     * @return The particle builder
     */
    private ParticleBuilder getDust(Color color) {
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
     * @see TUpgradeCooldown Calculate the cooldown from the upgrade
     * @see #getCurrentCooldown() Get the current cooldown of the totem
     */
    public boolean onCooldown() {
        if (this.lastActive <= 0) return false;

        return System.currentTimeMillis() - lastActive < this.getCooldown().toMillis();
    }

    /**
     * Get the current cooldown timer of the totem in milliseconds
     * <p>
     *
     * @return The cooldown of the totem
     *
     * @see TUpgradeCooldown Calculate the cooldown from the upgrade
     * @see #onCooldown() Check if the totem is on cooldown
     */
    public long getCurrentCooldown() {
        if (this.lastActive <= 0) return 0;

        return this.getCooldown().toMillis() - (System.currentTimeMillis() - lastActive);
    }

    /**
     * Get the current duration of the totem in milliseconds
     *
     * @return The duration of the totem
     *
     * @see TUpgradeDuration Calculate the duration from the upgrade
     * @see #getCurrentDuration() Get the duration of the totem
     * @see #onCooldown() Check if the totem is on cooldown
     */
    public long getCurrentDuration() {
        if (!this.active || this.lastActive <= 0) return 0;

        return this.getDuration().toMillis() - (System.currentTimeMillis() - lastActive);
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
        if (!location.getWorld().getName().equalsIgnoreCase(this.position.getWorld().getName())) {
            return false;
        }

        double radius = this.getRadius();
        double dx = location.getX() - position.getX();
        double dz = location.getZ() - position.getZ();
        double dy = Math.abs(location.getY() - position.getY());
        double normalized = (dx * dx) / (radius * radius) + (dz * dz) / (radius * radius);
        return normalized <= 1.0 && dy <= radius;
    }

    /**
     * Get the outer bounds of the totem in a circle
     *
     * @return The outer bounds of the totem
     */
    public List<Location> getBounds() {
        if (this.position == null) return new ArrayList<>();

        List<Location> results = new ArrayList<>();
        double radius = this.getRadius();
        int numSteps = 120;
        for (int i = 0; i < numSteps; i++) {
            double dx = MathL.cos(Math.PI * 2 * ((double) i / numSteps)) * radius;
            double dz = MathL.sin(Math.PI * 2 * ((double) i / numSteps)) * radius;

            results.add(this.position.clone().add(dx, 0, dz));
        }

        return results;
    }

    /**
     * Handle an event and call the consumer for it
     *
     * @param event The event to handle
     */
    @Override
    public <T extends Event> void handleEvent(T event) {
        super.handleEvent(event);
        this.upgrades.values().forEach(x -> {
            if (x.getLevel() <= 0) return;
            if (x instanceof Toggleable toggleable && !toggleable.isActivated()) return;
            x.handleEvent(event);
        });
    }

    /**
     * The delay between each time the task is run
     *
     * @return The delay between each task
     */
    @Override
    public Duration getTickDelay() {
        return TotemConfig.get().getTickDelay();
    }

    /**
     * Write data into a data container
     *
     * @param container The container to write into
     */
    @Override
    public void writeContainer(PersistentDataContainer container) {
        if (container == null) return;

        container.set(TOTEM_OWNER.key(), TOTEM_OWNER, this.owner);
        container.set(TOTEM_ACTIVE.key(), TOTEM_ACTIVE, this.active);
        container.set(TOTEM_LAST_ACTIVE.key(), TOTEM_LAST_ACTIVE, this.lastActive);
        container.set(TOTEM_LEVEL.key(), TOTEM_LEVEL, this.level);
        container.set(TOTEM_PRIVACY.key(), TOTEM_PRIVACY, this.privacy);
        container.set(TOTEM_USERS.key(), TOTEM_USERS, this.users);
        container.set(TOTEM_BAG.key(), TOTEM_BAG, this.bag);
        container.set(TOTEM_BAG_CAPACITY.key(), TOTEM_BAG_CAPACITY, this.bagCapacity);
        container.set(TOTEM_OWNER_NAME.key(), TOTEM_OWNER_NAME, this.ownerName);

        if (this.displayName != null) container.set(TOTEM_DISPLAY_NAME.key(), TOTEM_DISPLAY_NAME, this.displayName);

        //        if (this.skin != null) container.set(TOTEM_SKIN.key(), TOTEM_SKIN, this.skin.id()); // TODO: Totem Skin

        // Write the upgrade containers
        PersistentDataAdapterContext context = container.getAdapterContext();
        PersistentDataContainer upgradesContainer = context.newPersistentDataContainer();
        for (TotemUpgrade totemUpgrade : this.upgrades.values()) {
            PersistentDataContainer upgradeContainer = context.newPersistentDataContainer();
            String identifier = totemUpgrade.getIdentifier().get();
            NamespacedKey key = NamespacedKey.fromString("upgrade_" + identifier, FishingPlugin.get());
            if (identifier == null || key == null) {
                FishingPlugin.get().getLogger().warning("Totem Upgrade[" + totemUpgrade.getClass().getSimpleName() + "] does not have an identifier");
                return;
            }

            totemUpgrade.writeContainer(upgradeContainer);
            upgradesContainer.set(key, TAG_CONTAINER, upgradeContainer);
        }

        container.set(TOTEM_UPGRADES.key(), TAG_CONTAINER, upgradesContainer);

        if (this.displayId != null) {
            FishingPlugin.get().getTotemManager().registerTotem(this.displayId, this);
        }
    }

    /**
     * Load and deserialize data from a data container
     *
     * @param container The container to read from
     */
    @Override
    public void readContainer(PersistentDataContainerView container) {
        if (container == null) return;

        this.owner = container.get(TOTEM_OWNER.key(), TOTEM_OWNER);
        this.active = container.getOrDefault(TOTEM_ACTIVE.key(), TOTEM_ACTIVE, false);
        this.lastActive = container.getOrDefault(TOTEM_LAST_ACTIVE.key(), TOTEM_LAST_ACTIVE, 0L);
        this.level = container.getOrDefault(TOTEM_LEVEL.key(), TOTEM_LEVEL, 1);
        this.privacy = container.get(TOTEM_PRIVACY.key(), TOTEM_PRIVACY);
        this.users = container.get(TOTEM_USERS.key(), TOTEM_USERS);
        this.bag = container.get(TOTEM_BAG.key(), TOTEM_BAG);
        this.ownerName = container.get(TOTEM_OWNER_NAME.key(), TOTEM_OWNER_NAME);
        this.displayName = container.get(TOTEM_DISPLAY_NAME.key(), TOTEM_DISPLAY_NAME);

        // TODO: Load skin individually
        // this.skin = container.get(TOTEM_SKIN.key(), TOTEM_SKIN);

        // Load all the totem upgrades from the container
        PersistentDataContainer upgradeContainer = container.get(TOTEM_UPGRADES.key(), TAG_CONTAINER);
        if (upgradeContainer != null) {
            for (NamespacedKey key : upgradeContainer.getKeys()) {
                PersistentDataContainer upgrade = upgradeContainer.get(key, TAG_CONTAINER);
                if (upgrade == null) continue;

                TotemUpgrade totemUpgrade = this.upgrades.get(key.value().replace("upgrade_", ""));
                if (totemUpgrade != null) totemUpgrade.readContainer(upgrade);
            }
        }
    }

    /**
     * Get all the placeholders for the totem
     *
     * @return The placeholders for the totem
     */
    public Placeholders getPlaceholders() {
        Placeholders.Builder builder = Placeholders.builder();
        builder.add("owner", this.ownerName);
        builder.add("active", this.active ? "Active" : "Inactive");
        builder.add("status", FishUtils.capitalizeFully(this.privacy.name().replace("_", " ")));
        builder.add("name", this.displayName != null ? this.displayName : "N/A");

        // Add the upgrade placeholders
        this.upgrades.forEach((upgradeId, upgrade) -> {
            builder.add("upgrade_" + upgradeId, upgrade.getLevel());

            // Add all the placeholders for the upgrade
            upgrade.getPlaceholders(this)
                    .getAll()
                    .forEach((key, value) -> {
                        String upgradeKey = "upgrade_" + upgradeId + "_" + key;
                        builder.add(upgradeKey, value);
                    });
        });

        return builder.build();
    }

    @Override
    public String toString() {
        return "Totem{" +
               "position=" + position +
               ", owner=" + owner +
               ", active=" + active +
               ", lastActive=" + lastActive +
               ", level=" + level +
               ", privacy=" + privacy +
               ", skin=" + skin +
               ", ownerName='" + ownerName + '\'' +
               ", displayName='" + displayName + '\'' +
               ", bag=" + bag +
               ", users=" + users +
               ", upgrades=" + upgrades +
               ", displayId=" + displayId +
               ", confirmedActivate=" + confirmedActivate +
               '}';
    }

    public Location getPosition() {
        return position;
    }

    public void setPosition(Location position) {
        this.position = position;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getLastActive() {
        return lastActive;
    }

    public void setLastActive(long lastActive) {
        this.lastActive = lastActive;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public TotemPrivacy getPrivacy() {
        return privacy;
    }

    public void setPrivacy(TotemPrivacy privacy) {
        this.privacy = privacy;
    }

    public TotemSkin getSkin() {
        return skin;
    }

    public void setSkin(TotemSkin skin) {
        this.skin = skin;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<Integer, ItemStack> getBag() {
        return bag;
    }

    public void setBag(Map<Integer, ItemStack> bag) {
        this.bag = bag;
    }

    public int getBagCapacity() {
        return bagCapacity;
    }

    public void setBagCapacity(int bagCapacity) {
        this.bagCapacity = bagCapacity;
    }

    public Set<UUID> getUsers() {
        return users;
    }

    public void setUsers(Set<UUID> users) {
        this.users = users;
    }

    public Map<String, TotemUpgrade> getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(Map<String, TotemUpgrade> upgrades) {
        this.upgrades = upgrades;
    }

    public ArmorStand getDisplay() {
        if (this.displayId == null) return null;
        if (this.display != null && !this.display.isDead() && this.display.isValid()) {
            return this.display;
        }

        return this.display = this.position.getWorld().getEntity(this.displayId) instanceof ArmorStand stand ? stand : null;
    }

    public UUID getDisplayId() {
        return displayId;
    }

    public void setDisplayId(UUID displayId) {
        this.displayId = displayId;
    }

    public boolean isConfirmedActivate() {
        return confirmedActivate;
    }

    public void setConfirmedActivate(boolean confirmedActivate) {
        this.confirmedActivate = confirmedActivate;
    }

    public ScheduledTask getFoliaTask() {
        return foliaTask;
    }

    public void setFoliaTask(ScheduledTask foliaTask) {
        this.foliaTask = foliaTask;
    }

}
