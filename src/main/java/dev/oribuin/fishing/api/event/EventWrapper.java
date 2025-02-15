package dev.oribuin.fishing.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.function.BiConsumer;

/**
 * Wrapper for an event to be registered with a function
 *
 * @param event    The event to be registered
 * @param function The function to be called when the event is fired
 * @param order    The priority of the event
 * @param <T>      The event type to be registered with the function
 */
public record EventWrapper<T extends Event>(Class<T> event, BiConsumer<T, Integer> function, EventPriority order) {

    /**
     * Call the function that was registered with the event, This will cast the event to the correct type
     *
     * @param event The {@link Event} to call
     * @param level The level of the event
     */
    @SuppressWarnings("unchecked")
    public void accept(Event event, int level) {
        this.function.accept((T) event, level);
    }

    /**
     * Get the event type that was registered
     *
     * @return The event that was registered
     */
    @Override
    public Class<T> event() {
        return event;
    }

    /**
     * Get the function that was registered
     *
     * @return The function that was registered
     */
    @Override
    public BiConsumer<T, Integer> function() {
        return function;
    }

    /**
     * Get the priority of the event
     *
     * @return The priority of the event
     */
    @Override
    public EventPriority order() {
        return order;
    }

}