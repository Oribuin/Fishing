package dev.oribuin.fishing.model.animation;

import net.kyori.adventure.audience.Audience;

public abstract class Animation {

    /**
     * Display the animation to the nearby audience, usually the player.
     *
     * @param animated The module to display the animation for.
     * @param audience The audience to display the animation to.
     */
    public abstract void display(Animated animated, Audience audience);

}
