package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.api.event.impl.RodCastEvent;
import dev.oribuin.fishing.model.totem.upgrade.Toggleable;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.function.Supplier;

@ConfigSerializable
public class TUpgradeTorrent extends TotemUpgrade implements Toggleable {

    private transient boolean activated;

    /**
     * Create a new totem upgrade with the name "torrent"
     */
    public TUpgradeTorrent() {
        super();
        this.activated = false;
        this.description = List.of("<gray>Allows rain dance to be used during clear weather");
        this.upgradeIcon.setMaterial(Material.TUBE_CORAL);
        this.upgradeIcon.setLore(this.description, this.upgradeIcon.getLore());
        this.registerListener(RodCastEvent.class, this::onCastRod);
    }

    /**
     * The functionality provided when a fish bites the rod before it is caught
     *
     * @param event The fishing event
     */
    @Override
    public void onCastRod(RodCastEvent event) {
        event.getWrapper().setHookInRain(true); // Apply rain influence buff
    }

    /**
     * Get the identifier for the totem upgrade
     *
     * @return The upgrade supplier
     */
    @Override
    public Supplier<String> getIdentifier() {
        return () -> "torrent";
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
