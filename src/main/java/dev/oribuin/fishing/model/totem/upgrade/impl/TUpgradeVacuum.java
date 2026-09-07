package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.Toggleable;
import dev.oribuin.fishing.model.totem.upgrade.TotemTickable;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

@ConfigSerializable
public class TUpgradeVacuum extends TotemUpgrade implements Toggleable, TotemTickable {

    private transient boolean activated;

    /**
     * Create a new totem upgrade with the name "radius"
     */
    public TUpgradeVacuum() {
        super();
        this.activated = false;
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
        Collection<Item> nearbyItems = position.getNearbyEntitiesByType(
                Item.class, 3, 3, 3,
                item -> FishingPlugin.get().getTierManager().isFish(item.getItemStack())
        );

        for (Item next : nearbyItems) {
            if (!next.isValid() || next.isDead() || next.getOwner() != null) continue; // entity doesn't exist / shouldn't be picked up

            ItemStack toDeposit = next.getItemStack();
            totem.depositBag(toDeposit);
            if (toDeposit.getAmount() <= 0) {
                next.remove();
            } else {
                next.setItemStack(toDeposit);
            }
        }
    }


    /**
     * Get the identifier for the totem upgrade
     *
     * @return The upgrade supplier
     */
    @Override
    public Supplier<String> getIdentifier() {
        return () -> "vacuum";
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
