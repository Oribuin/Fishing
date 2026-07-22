package dev.oribuin.fishing.model.augment;

import java.util.function.Supplier;

public record RegisteredAugment<T extends Augment>(String identifier, Class<T> registeredClass, Supplier<T> supplier) {
    
}
