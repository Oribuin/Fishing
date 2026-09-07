package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.Toggleable;
import dev.oribuin.fishing.model.totem.upgrade.TotemTickable;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

@ConfigSerializable
public class TUpgradeGravityWell extends TotemUpgrade implements Toggleable, TotemTickable {

    private transient boolean activated;
    private boolean onlyCustomFish;

    /**
     * Create a new totem upgrade with the name "radius"
     */
    public TUpgradeGravityWell() {
        super();
        this.activated = false;
        this.onlyCustomFish = true;
        this.description = List.of("<gray>Pulls entities into the upgrade");
    }

    /**
     * The functionality to run while the totem is active
     *
     * @param totem The totem that is active
     */
    @Override
    public void tick(@NotNull Totem totem) {
        Location position = totem.getPosition();
        double radius = totem.getRadius();

        Collection<Item> nearbyItems = position.getNearbyEntitiesByType(Item.class, radius, radius, radius, item -> {
            if (!this.onlyCustomFish) return true;

            return FishingPlugin.get().getTierManager().isFish(item.getItemStack());
        });
        
        for (Item next : nearbyItems) {
            if (!next.isValid() || next.isDead() || next.getOwner() != null) continue; // entity doesnt exist / shouldn't be picked up

            double distance = next.getLocation().distance(position);
            if (distance < 2) {
                next.setVelocity(new Vector());
                continue;
            }

            Vector attractionVelocity = position.toVector().clone().subtract(next.getLocation().toVector());
            double pullStrength = 2.5 - distance / radius;
            next.setVelocity(next.getVelocity().add(
                    attractionVelocity.normalize().multiply(pullStrength * pullStrength * 0.1)
            ));
        }
    }


    /**
     * Get the identifier for the totem upgrade
     *
     * @return The upgrade supplier
     */
    @Override
    public Supplier<String> getIdentifier() {
        return () -> "gravity_well";
    }

    /**
     * Check whether a totem upgrade has been toggled on
     *
     * @return Whether the totem upgrade is activated
     */
    @Override
    public boolean isActivated() {
        return this.activated;
    }

    /**
     * Set a totem upgrade to activated or not
     *
     * @param activated Whether the totem upgrade is activated
     */
    @Override
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

}
