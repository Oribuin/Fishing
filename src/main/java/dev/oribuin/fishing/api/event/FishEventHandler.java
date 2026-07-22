package dev.oribuin.fishing.api.event;

import dev.oribuin.fishing.api.event.def.FishingEvents;
import dev.oribuin.fishing.api.event.def.TotemEvents;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A global handler to parse any fishing related events, used to detect
 * and modify fish when they are caught, generated and given
 */
public abstract class FishEventHandler implements FishingEvents, TotemEvents {

    private final transient Map<Class<? extends Event>, Consumer<? extends Event>> listeners;

    public FishEventHandler() {
        this.listeners = new HashMap<>();
    }

    /**
     * Check if an event class is registered into the plugin
     *
     * @param eventClass The event class to register
     * @param <T>        The event type
     *
     * @return Whether the event is registered
     */
    public <T extends Event> boolean isRegistered(Class<T> eventClass) {
        return eventClass != null && this.listeners.containsKey(eventClass);
    }

    /**
     * Register a listener into the event handler to be used later
     *
     * @param eventClass The event class to register
     * @param listener   The listener to register
     * @param <T>        The type of event
     */
    public <T extends Event> void registerListener(Class<T> eventClass, Consumer<T> listener) {
        this.listeners.put(eventClass, listener);
    }

    /**
     * Handle an event and call the consumer for it
     *
     * @param event The event to handle
     * @param <T>   The type of event
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void handleEvent(T event) {
        if (event == null || event instanceof Cancellable cancellable && cancellable.isCancelled()) return;

        Class<? extends Event> eventClass = event.getClass();
        Consumer<? extends Event> consumer = this.listeners.get(eventClass);
        if (consumer == null) return;

        ((Consumer<T>) consumer).accept(event);
    }

}


