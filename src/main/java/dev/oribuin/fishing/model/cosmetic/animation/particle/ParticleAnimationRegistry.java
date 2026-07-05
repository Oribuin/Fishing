package dev.oribuin.fishing.model.cosmetic.animation.particle;

import dev.oribuin.fishing.model.cosmetic.animation.particle.impl.CircleParticleAnimation;
import dev.oribuin.fishing.model.cosmetic.animation.particle.impl.CubeParticleAnimation;
import dev.oribuin.fishing.model.cosmetic.animation.particle.impl.SquareParticleAnimation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ParticleAnimationRegistry {

    private static final Map<String, Supplier<ParticleAnimation>> REGISTRY = new LinkedHashMap<>();
    
    static {
        register("circle", CircleParticleAnimation::new);
        register("cube", CubeParticleAnimation::new);
        register("square", SquareParticleAnimation::new);
//        register("sphere", SphereParticleAnimation::new); // TODO
//        register("raining", RainingParticleAnimation::new); // TODO
//        register("vortex", VortexParticleAnimation::new); // TODO who knows
//        register("batman", SphereParticleAnimation::new); // TODO batman logo :)
//        register("pulse", PulseParticleAnimation::new); // TODO particle wave going outwards
//        register("magnet", MagnetParticleAnimation::new); // TODO particle wave coming inwards
//        register("fairy", FairyParticleAnimation::new); // TODO just something flying around 
//        register("scanner", ScannerParticleAnimation::new); // TODO circle but going up and down
//        register("helixscanner", HelixScannerParticleAnimation::new); // TODO dots going up and down & spinning
//        register("gravitywell", GravityWellParticleAnimation::new); // TODO dots going up and down & spinning
//        register("somethingaboutlandbeingfunky", WhoKnows::new); // TODO think just the land around you glowing with particles 
    }

    /**
     * Register a new particle animation into the plugin
     *
     * @param identifier The identifier to get
     * @param animation  The animation to supply
     */
    public static void register(String identifier, Supplier<ParticleAnimation> animation) {
        REGISTRY.put(identifier, animation);
    }
    
}
