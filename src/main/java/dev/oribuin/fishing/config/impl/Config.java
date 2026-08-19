package dev.oribuin.fishing.config.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.rod.RodRarity;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class Config {

    private String experienceFormula = "<level> * 625";
    
    private Map<String, RodRarity> rodUpgrades = new HashMap<>() {{
        this.put("basic", new RodRarity(5, null));
    }};

    public static Config get() {
        return FishingPlugin.get().getConfigLoader().get(Config.class);
    }

    public String getExperienceFormula() {
        return experienceFormula;
    }

    public Map<String, RodRarity> getRodUpgrades() {
        return rodUpgrades;
    }
    
}
