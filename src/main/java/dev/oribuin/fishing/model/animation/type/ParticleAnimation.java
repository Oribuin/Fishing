package dev.oribuin.fishing.model.animation.type;

import com.destroystokyo.paper.ParticleBuilder;
import dev.oribuin.fishing.api.task.AsyncTicker;
import dev.oribuin.fishing.model.animation.Animated;
import dev.oribuin.fishing.model.animation.Animation;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.scheduler.task.ScheduledTask;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class ParticleAnimation extends Animation implements AsyncTicker {

    protected final Animated animated; // The Animated object to display the particles
    private final Duration maxLength; // The maximum length of the animation
    protected ScheduledTask task; // The task that is running the animation

    /**
     * Create a new Particle Animation object to display particles
     *
     * @param animated The Animated object to display the particles
     */
    public ParticleAnimation(Animated animated) {
        this.animated = animated;
        this.maxLength = Duration.ofMinutes(30);
        this.task = null;
    }

    /**
     * The list of locations to spawn the particles at for the animation
     *
     * @param animated The Animated object to display the particles
     *
     * @return A list of locations to spawn the particles at
     */
    public abstract List<Location> create(Animated animated);

    /**
     * Display the animation to the nearby audience, usually the player.
     *
     * @param animated The module to display the animation for.
     * @param audience The audience to display the animation to.
     */
    @Override
    public void display(Animated animated, Audience audience) {
        if (this.animated == null) return;

        // Schedule the task
        this.task = this.schedule();

        // Cancel the task after the designated amount of time
        PluginScheduler.get().runTaskLater(() -> this.task.cancel(), this.maxLength.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Create a new Particle Animation object to display particles
     *
     * @param animated The Animated object to display the particles
     *
     * @return The ParticleBuilder object
     */
    @NotNull
    public ParticleBuilder particle(Animated animated) {
        return new ParticleBuilder(Particle.FLAME).location(animated.getSource().get());
    }

    /**
     * The method that should run everytime the task is ticked,
     * this method will be run asynchronously
     */
    @Override
    public void tickAsync() {
        if (this.animated == null) return;

        // Display the particles
        ParticleBuilder builder = this.particle(this.animated);

        for (Location location : this.create(this.animated)) {
            builder.clone().location(location).spawn();
        }
    }

    /**
     * @return The maximum length of the animation
     */
    public Duration getMaxLength() {
        return maxLength;
    }

}
