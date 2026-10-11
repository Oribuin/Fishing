package dev.oribuin.fishing.model.rod;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record RodRarity(int capacity, String requiredRarity) {

    // TODO: Recipe 

    public RodRarity() {
        this(5, null);
    }

}
