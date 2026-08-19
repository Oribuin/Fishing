package dev.oribuin.fishing.config.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.item.ItemConstruct;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class LootConfig {

    private final Map<String, ItemConstruct> items = new HashMap<>() {{
        this.put("crab_claw", ItemConstruct.of(Material.RED_DYE)
                .setName("<#e34840><b>Crab Claw")
                .setLore("you got crabs"));
                
        
        this.put("crab_scale", ItemConstruct.of(Material.ORANGE_DYE)
                .setName("<#e34840><b>Crab Scale")
                .setLore("you got crabs"));
        
    }};

    public static LootConfig get() {
        return FishingPlugin.get().getConfigLoader().get(LootConfig.class);
    }

    public Map<String, ItemConstruct> getItems() {
        return items;
    }
}
