package dev.oribuin.fishing.config.impl;

import dev.oribuin.fishing.FishingPlugin;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class Config {
    private String experienceFormula = "<level> * 625";

    public static Config get() {
        return FishingPlugin.get().getConfigLoader().get(Config.class);
    }

    public String getExperienceFormula() {
        return experienceFormula;
    }
}
