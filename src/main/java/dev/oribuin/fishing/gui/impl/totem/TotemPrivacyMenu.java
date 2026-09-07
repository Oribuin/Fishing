package dev.oribuin.fishing.gui.impl.totem;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.gui.GuiConfig;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.Fisher;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Supplier;

public class TotemPrivacyMenu extends PluginMenu<Gui, TotemPrivacyMenu.Config> {

    private final Supplier<Totem> totemSupplier;

    public TotemPrivacyMenu(FishingPlugin plugin, Supplier<Totem> totemSupplier) {
        super(plugin, Config.class);
        this.gui = this.createMenu().get();
        this.totemSupplier = totemSupplier;

        Totem totem = this.totemSupplier.get();
        Fisher fisher = plugin.getDataManager().get(totem.getOwner());
        Placeholders placeholders = Placeholders.builder()
                .addAll(fisher.getPlaceholders())
                .addAll(totem.getPlaceholders())
                .build();

        this.setDummyIcons(placeholders);
    }

    /**
     * Creates the menu for the plugin
     *
     * @return the resulting menu
     */
    @Override
    public Supplier<Gui> createMenu() {
        return () -> Gui.gui()
                .title(Component.text(this.config.getTitle()))
                .rows(this.config.getRows())
                .disableAllInteractions()
                .create();
    }

    @ConfigSerializable
    @SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
    public static class Config extends GuiConfig {
        // TODO: Totem Privacy

        public Config() {
            this.title = "Fishing Totem | Privacy";
            this.rows = 4;
            this.dummyItems.add(new MenuItem(this.border, FishUtils.parseList("0-9", "17-18", "26-35")));
        }

    }

}
