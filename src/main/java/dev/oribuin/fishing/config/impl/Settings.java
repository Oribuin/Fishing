package dev.oribuin.fishing.config.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.rod.RodRarity;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class Settings {

    private boolean instantLootPickup = false;
    private boolean rodStatistics = true;
    private String experienceFormula = "<level> * 625";
    private List<String> augmentsHeader = List.of(
            "<dark_gray><st>                                      </st>",
            "<#93bc80>         Fishing Augments",
            ""
    );

    private List<String> augmentsFooter = List.of(
            "<dark_gray><st>                                      </st>"
    );
    private Map<String, RodRarity> rodUpgrades = new HashMap<>() {{
        this.put("basic", new RodRarity(5, null));
    }};

    public static Settings get() {
        return FishingPlugin.get().getConfigLoader().get(Settings.class);
    }

    public boolean isInstantLootPickup() {
        return instantLootPickup;
    }

    public boolean isRodStatistics() {
        return rodStatistics;
    }

    public String getExperienceFormula() {
        return experienceFormula;
    }

    public List<String> getAugmentsHeader() {
        return augmentsHeader;
    }

    public List<String> getAugmentsFooter() {
        return augmentsFooter;
    }

    public Map<String, RodRarity> getRodUpgrades() {
        return rodUpgrades;
    }

}
