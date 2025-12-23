package dev.oribuin.fishing.config.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.item.component.TextureConstructType;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class TotemConfig {
    
    private ItemConstruct totemItem = new ItemConstruct(Material.PLAYER_HEAD)
            .setName("<white>[<#94bc80><bold>Fishing Totem<white>]")
            .setLore(
                    "<gray>Place in the world to create local",
                    "<gray>booster for players within it's radius.",
                    "",
                    "<#94bc80>Information",
                    " <#94bc80>- <gray>Owner: <#94bc80><owner>",
                    " <#94bc80>- <gray>Radius: <#94bc80><upgrade_radius_value% blocks>",
                    " <#94bc80>- <gray>Duration: <#94bc80><upgrade_duration_value>",
                    " <#94bc80>- <gray>Cooldown: <#94bc80><upgrade_cooldown_value>",
                    ""
            )
            .setTexture(new TextureConstructType("base64-eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2ZhMTVlNDJjNGJjNTI3N2EzMTc5OTYzMjAxMjZjNzY2N2Y4ZTcxNTdmYzc3ZTE3ZDViMzQ1OGE3M2ExNWE0NCJ9fX0="))
            .setGlowing(true);

    public static TotemConfig get() {
        return FishingPlugin.get().getConfigLoader().get(TotemConfig.class);
    }

    public ItemConstruct getTotemItem() {
        return totemItem;
    }
}
