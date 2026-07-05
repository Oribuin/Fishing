package dev.oribuin.fishing.config;

import com.destroystokyo.paper.ParticleBuilder;
import dev.oribuin.fishing.util.FishUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class ParticleWrapper {

    private Particle particle;
    private int count;
    private Color color;
    private DustTransition transition;
    private Material material;

    /**
     * Create a new particle wrapper for the plugin
     */
    public ParticleWrapper() {
        this.particle = Particle.FLAME;
        this.count = 1;
        this.color = Color.fromRGB(147, 188, 128);
        this.transition = new DustTransition();
        this.material = Material.STONE;
    }

    /**
     * Create a new particle wrapper for the plugin
     *
     * @param particle The particle wrapper
     * @param count    The amount of particles to spawn part
     */
    public ParticleWrapper(Particle particle, int count) {
        this.particle = particle;
        this.count = count;
    }

    /**
     * Get the particle builder for the wrapper
     *
     * @return The particle builder
     */
    public ParticleBuilder getBuilder() {
        ParticleBuilder builder = new ParticleBuilder(this.particle);
        builder.count(this.count);

        if (this.particle == Particle.DUST) builder.color(this.color);
        if (this.particle == Particle.DUST_COLOR_TRANSITION) {
            builder.colorTransition(
                    this.transition.getFirst(),
                    this.transition.getSecond(),
                    this.transition.getSize());
        }

        if (this.particle.getDataType().equals(BlockData.class)) {
            builder.data(FishUtils.getMaterialData(this.material));
        }

        if (this.particle.getDataType().equals(ItemStack.class)) {
            builder.data(new ItemStack(this.material));
        }

        return builder.clone();
    }

    public Particle getParticle() {
        return particle;
    }

    public ParticleWrapper setParticle(Particle particle) {
        this.particle = particle;
        return this;
    }

    public int getCount() {
        return count;
    }

    public ParticleWrapper setCount(int count) {
        this.count = count;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public ParticleWrapper setColor(Color color) {
        this.color = color;
        return this;
    }

    public DustTransition getTransition() {
        return transition;
    }

    public ParticleWrapper setTransition(DustTransition transition) {
        this.transition = transition;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public ParticleWrapper setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Create a config wrapper for dust transition particles
     */
    @ConfigSerializable
    public static class DustTransition {

        private Color first;
        private Color second;
        private float size;

        public DustTransition() {
            this(Color.WHITE, Color.BLACK);
            this.size = 1f;
        }

        public DustTransition(Color first, Color second) {
            this.first = first;
            this.second = second;
            this.size = 1f;
        }

        public Particle.DustTransition getTransition() {
            return new Particle.DustTransition(this.first, this.second, this.size);
        }

        public Color getFirst() {
            return first;
        }

        public Color getSecond() {
            return second;
        }

        public float getSize() {
            return size;
        }
    }

}
