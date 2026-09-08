package dev.oribuin.fishing.model.rod;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class RodRarity {

    private final int capacity;
    private final String requiredRarity;
    // TODO: Recipe 

    public RodRarity() {
        this(5, null);
    }

    public RodRarity(int capacity, String requiredRarity) {
        this.capacity = capacity;
        this.requiredRarity = requiredRarity;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getRequiredRarity() {
        return requiredRarity;
    }

}
