package dev.oribuin.fishing.api.event.def;

import dev.oribuin.fishing.api.event.EventWrapper;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.api.event.impl.TotemActivateEvent;

/**
 * A global list of methods that can be used to modify all the relevant events in the plugin
 * <p>
 * This is to be used as a template reference for all totem based events.
 * <p>
 * If you are implementing these methods, They should be registered through {@link FishEventHandler#register(EventWrapper)} class.
 */
@SuppressWarnings("unused")
public interface TotemEvents {

    /**
     * The functionality provided when a player activates the fishing totem
     *
     * @param event The event that was called when a player activates a totem
     */
    default void onActivate(TotemActivateEvent event) {}
    
    /**
     * The functionality provided when the fishing totem deactivates
     *
     * @param event The event that was called when the totem deactivates
     */
    default void onDeactivate(TotemActivateEvent event) {}
    
}
