package dev.oribuin.fishing.model.cosmetic.animation;

import org.bukkit.Location;

/**
 * Create a new type of animation for the plugin
 *
 * @param <T> The target of the animation, Player, Armour Stand
 */
public abstract class Animation<T> {

    /**
     * Display the animation to the nearby audience, usually the player.
     *
     * @param position The centre point of the animation 
     * @param target   The audience to display the animation to.
     */
    public abstract void display(Location position, T target);

}
