package dev.oribuin.fishing.gui;

import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public abstract class GuiConfig {

    protected transient final ItemConstruct border = ItemConstruct.of(Material.BLACK_STAINED_GLASS_PANE)
            .setProperty(ConstructType.TOOLTIP, tooltipItemType -> tooltipItemType.setVisible(false));
            
    protected String title = "Plugin Menu";
    protected int rows = 5;
    protected List<MenuItem> dummyItems = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public List<MenuItem> getDummyItems() {
        return dummyItems;
    }
    
}
