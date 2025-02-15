package dev.oribuin.fishing.api.event;

import dev.oribuin.fishing.model.augment.Augment;
import org.bukkit.event.Event;

import java.util.Map;

/**
 * Wrapper for a fishing event handler to be registered with a function
 */
public class MutableEventWrapper<T extends FishEventHandler> {

    private final T type;
    private final Integer level;
    private final EventWrapper<?> wrapper;

    /**
     * Wrapper for a fish event handler to be registered with a function in an event, Used to make the stream less annoying
     *
     * @param type  The type to be registered
     * @param level The level of the augment
     * @param event The event to be registered
     *
     * @see FishEventHandler#callEvents(Map, Event) Where this is used
     */
    public MutableEventWrapper(T type, Integer level, Event event) {
        this.type = type;
        this.level = level;
        this.wrapper = type.getWrapper(event.getClass());
    }

    /**
     * Wrapper for a fish event handler to be registered with a function in an event, Used to make the stream less annoying
     *
     * @param entry The entry to be registered
     * @param event The event to be registered
     *
     * @see FishEventHandler#callEvents(Map, Event) Where this is used
     */
    public MutableEventWrapper(Map.Entry<T, Integer> entry, Event event) {
        this(entry.getKey(), entry.getValue(), event);
    }

    /**
     * Compare the priority of two events to determine which one should be called first
     *
     * @param other The other event to compare
     *
     * @return The comparison of the two events
     */
    public int compare(MutableEventWrapper<T> other) {
        return Integer.compare(this.wrapper.order().getSlot(), other.wrapper().order().getSlot());
    }

    /**
     * Get the augment that was registered
     *
     * @return The {@link Augment} that was registered
     */
    public T type() {
        return type;
    }

    /**
     * Get the level of the augment that was registered
     *
     * @return The level of the augment that was registered
     */
    public Integer level() {
        return level;
    }

    /**
     * Get the event wrapper that was registered
     *
     * @return The event wrapper that was registered
     */
    public EventWrapper<?> wrapper() {
        return wrapper;
    }

}