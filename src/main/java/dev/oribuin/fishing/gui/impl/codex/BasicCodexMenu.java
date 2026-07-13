package dev.oribuin.fishing.gui.impl.codex;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.gui.GuiConfig;
import dev.oribuin.fishing.gui.MenuItem;
import dev.oribuin.fishing.gui.PluginMenu;
import dev.oribuin.fishing.gui.impl.totem.TotemMainMenu;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.function.Supplier;

public abstract class BasicCodexMenu<T, Z extends BasicCodexMenu.CodexGuiConfig> extends PluginMenu<PaginatedGui, Z> {

    /**
     * Creates a new plugin menu instance, with the specified name
     *
     * @param plugin The name of the menu, will be also function as the menu path
     */
    public BasicCodexMenu(FishingPlugin plugin, Class<Z> config) {
        super(plugin, config);
    }

    /**
     * Get all the content that is going to be displayed in the codex
     *
     * @return The content to display in the menu
     */
    public abstract List<T> getContent();

    /**
     * Get the t value as an itemstack to display
     *
     * @param value The value to show
     *
     * @return The itemstack form
     */
    @NotNull
    public abstract ItemStack getStack(@NotNull T value);

    /**
     * Creates the menu for the plugin
     *
     * @return the resulting menu
     */
    @Override
    public Supplier<PaginatedGui> createMenu() {
        return () -> Gui.paginated()
                .title(Component.text(this.config.getTitle()))
                .rows(this.config.getRows())
                .disableAllInteractions()
                .apply(paginatedGui -> {
                    this.config.getDummyItems().forEach(icon -> icon.place(
                            paginatedGui, 
                            Placeholders.empty(), 
                            EMPTY
                            ));

                    this.setDummyIcons();
                    this.config.getPreviousPage().place(paginatedGui, Placeholders.empty(), event -> gui.previous());
                    this.config.getNextPage().place(paginatedGui, Placeholders.empty(), event -> gui.next());
                    this.config.getCodexMainMenu().place(paginatedGui, Placeholders.empty(), event -> {
                        // TODO
                    });
                })
                .create();
    }

    @ConfigSerializable
    @SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
    public static abstract class CodexGuiConfig extends GuiConfig {
        private MenuItem previousPage = new ItemConstruct(Material.ARROW)
                .setName("<white>[<#94bc80>Previous Page<white>]")
                .setLore("<gray>Click here to go to the previous page")
                .asMenuItem(3);

        private MenuItem codexMainMenu = new ItemConstruct(Material.KNOWLEDGE_BOOK)
                .setName("<white>[<#94bc80>Codex Menu<white>]")
                .setLore(
                        "<gray>Click here to go to the index page",
                        "<gray>with all the different types of information",
                        "<gray>available in the codex"
                )
                .asMenuItem(4);

        private MenuItem nextPage = new ItemConstruct(Material.ARROW)
                .setName("<white>[<#94bc80>Next Page<white>]")
                .setLore("<gray>Click here to go to the next page")
                .asMenuItem(5);

        public CodexGuiConfig() {
            this.title = "Fishing Codex";
            this.rows = 5;
        }

        public MenuItem getPreviousPage() {
            return previousPage;
        }

        public MenuItem getCodexMainMenu() {
            return codexMainMenu;
        }

        public MenuItem getNextPage() {
            return nextPage;
        }
    }


}
