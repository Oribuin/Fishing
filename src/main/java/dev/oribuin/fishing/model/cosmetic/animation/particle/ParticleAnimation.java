package dev.oribuin.fishing.model.cosmetic.animation.particle;

import com.destroystokyo.paper.ParticleBuilder;
import dev.oribuin.fishing.config.ParticleWrapper;
import dev.oribuin.fishing.model.cosmetic.animation.Animation;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Create a new type of animation for the plugin
 */
@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public abstract class ParticleAnimation extends Animation<Player> {

    protected Duration duration = Duration.ofSeconds(5);
    protected Duration delay = Duration.ofSeconds(1);
    protected ParticleWrapper particle = new ParticleWrapper();
    protected boolean targetOnly = false;
    protected int offsetY;

    /**
     * Get a variety of locations around a specified radius
     *
     * @param center The centre of the animation
     *
     * @return The positions of it
     */
    public abstract List<Location> getPositions(Location center);

    /**
     * Display the animation to the nearby audience, usually the player.
     *
     * @param position The centre point of the animation
     * @param target   The audience to display the animation to.
     */
    @Override
    public void display(Location position, Player target) {
        ParticleBuilder builder = this.particle.getBuilder();
        if (this.targetOnly && target.isOnline()) builder.receivers(target);
        PluginScheduler.get().runTaskTimerAsync(() -> { // TODO: See about doing this all on the same task </3
            List<Location> positions = this.getPositions(position);
            positions.forEach(location -> builder.clone().location(position).spawn());
        }, this.delay.toSeconds(), this.duration.toSeconds(), TimeUnit.SECONDS);
    }

}
