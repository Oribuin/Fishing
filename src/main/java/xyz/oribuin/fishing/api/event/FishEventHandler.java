package xyz.oribuin.fishing.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A global handler to parse any fishing related events, used to detect
 * and modify fish when they are caught, generated and given
 */
public abstract class FishEventHandler implements FishingEvents{

    private final Map<Class<? extends Event>, EventWrapper<?>> events = new HashMap<>();

    /**
     * Call an event from the handler's registered events, This will not take priority into account.
     *
     * @param event The {@link Event} to call
     * @param level The level of the event
     * @param <T>   The event type to call
     *
     * @return The event that was called
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> T callEvent(T event, int level) {
        EventWrapper<?> wrapper = this.events.get(event.getClass());
        if (wrapper == null) return event;

        ((EventWrapper<T>) wrapper).function().accept(event, level);
        return event;
    }

    /**
     * Register a function to be called when an {@link Event} is fired for the specified event with {@link EventPriority#NORMAL} priority
     *
     * @param event    The event to register the function for
     * @param function The function to be called when the event is fired
     * @param <T>      The event type to register
     */
    public <T extends Event> void register(Class<T> event, BiConsumer<T, Integer> function) {
        this.register(new EventWrapper<>(event, function, EventPriority.NORMAL));
    }

    /**
     * Register a function to be called when an {@link Event} is fired for the specified event
     *
     * @param event    The event to register the function for
     * @param function The function to be called when the event is fired
     * @param order    The priority of the event, used to determine when it is called relative to other events. Uses the {@link EventPriority} enum and behaves like Bukkit's event priority
     * @param <T>      The event type to register
     */
    public <T extends Event> void register(Class<T> event, BiConsumer<T, Integer> function, EventPriority order) {
        this.register(new EventWrapper<>(event, function, order));
    }

    /**
     * Register a function to be called when an {@link Event} is fired for the specified event
     *
     * @param wrapper The event to register the function for
     * @param <T>     The event type to register
     */
    public <T extends Event> void register(EventWrapper<T> wrapper) {
        this.events.put(wrapper.event(), wrapper);
    }

    /**
     * Get all the events that are registered with the handler
     *
     * @return A map of all the events that are registered
     */
    public Map<Class<? extends Event>, EventWrapper<?>> events() {
        return events;
    }

    /**
     * Wrapper for an event to be registered with a function
     *
     * @param event    The event to be registered
     * @param function The function to be called when the event is fired
     * @param order    The priority of the event
     */
    public record EventWrapper<T extends Event>(Class<T> event, BiConsumer<T, Integer> function, EventPriority order) {

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

}
