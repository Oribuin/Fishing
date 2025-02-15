package dev.oribuin.fishing.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A global handler to parse any fishing related events, used to detect
 * and modify fish when they are caught, generated and given
 */
public abstract class FishEventHandler implements FishingEvents {

    private final Map<Class<? extends Event>, EventWrapper<?>> events = new HashMap<>();

    /**
     * Call an event from the handler's registered events, This will take priority into account.
     *
     * @param event The {@link Event} to call
     * @param <T>   The event type to call
     */
    public static <T extends FishEventHandler, Z extends Event> void callEvents(Map<T, Integer> values, Z event) {
        if (!event.callEvent()) return; // Call the event through bukkit to allow other plugins to listen to the event

        Map<T, Integer> applicable = new HashMap<>(values);
        applicable.keySet().removeIf(x -> !x.applicable(event));

        // Sort the events by their priority and call them
        applicable.entrySet().stream().map(x -> new MutableEventWrapper<>(x, event)).sorted(MutableEventWrapper::compare).forEachOrdered(x -> x.type().callEvent(event, x.level()));
    }

    /**
     * Call an event from the handler's registered events, This will not take priority into account.
     *
     * @param event The {@link Event} to call
     * @param level The level of the event
     * @param <T>   The event type to call
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void callEvent(T event, int level) {
        EventWrapper<?> wrapper = this.events.get(event.getClass());
        if (wrapper == null) return;

        ((EventWrapper<T>) wrapper).function().accept(event, level);
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
     * Check if an {@link Event} is applicable to the handler and has a function registered
     *
     * @param event The event to check
     *
     * @return If the event is applicable
     */
    public boolean applicable(Event event) {
        return this.events.containsKey(event.getClass());
    }

    /**
     * Get the wrapper for an event
     *
     * @param event The event to get the wrapper for
     * @param <T>   The event type to get the wrapper for
     *
     * @return The wrapper for the event
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> EventWrapper<T> getWrapper(Class<T> event) {
        if (!this.events.containsKey(event)) return null;

        return (EventWrapper<T>) this.events.get(event);
    }

}
