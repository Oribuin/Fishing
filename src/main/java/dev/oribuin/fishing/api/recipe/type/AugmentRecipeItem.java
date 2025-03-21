package dev.oribuin.fishing.api.recipe.type;

import dev.oribuin.fishing.api.recipe.RecipeItem;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.augment.AugmentRegistry;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class AugmentRecipeItem extends RecipeItem<Augment> {

    public AugmentRecipeItem() {
        super(Augment.class);
    }


    /**
     * Check if the item is the same as the recipe item
     *
     * @param item The item to check
     *
     * @return If the item is the same as the recipe item
     */
    @Override
    public boolean check(ItemStack item) {
        if (item == null) return false;

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        String augmentType = container.get(KeyRegistry.AUGMENT_TYPE, PersistentDataType.STRING);

        return augmentType != null && augmentType.equalsIgnoreCase(this.item.name());
    }


    /**
     * Initialize a {@link CommentedConfigurationSection} from a configuration file to establish the settings
     * for the configurable class, will be automatically called when the configuration file is loaded using {@link #reload()}
     * <p>
     * If your class inherits from another configurable class, make sure to call super.loadSettings(config)
     * to save the settings from the parent class
     * <p>
     * A class must be initialized before settings are loaded, If you wish to have a configurable data class style, its best to create a
     * static method that will create a new instance and call this method on the new instance
     * <p>
     * The {@link CommentedConfigurationSection} should never be null, when creating a new section,
     * use {@link #pullSection(CommentedConfigurationSection, String)} to establish new section if it doesn't exist
     *
     * @param config The {@link CommentedConfigurationSection} to load the settings from, this cannot be null.
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        super.loadSettings(config);

        this.item = AugmentRegistry.from(config.getString("item"));
    }

    /**
     * Serialize the settings of the configurable class into a {@link CommentedConfigurationSection} to be saved later
     * <p>
     * This functionality will not update the configuration file, it will only save the settings into the section to be saved later.
     * <p>
     * The function {@link #reload()} will save the settings on first load, please override this method if you wish to save the settings regularly
     * New sections should be created using {@link #pullSection(CommentedConfigurationSection, String)}
     *
     * @param config The {@link CommentedConfigurationSection} to save the settings to, this cannot be null.
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        super.saveSettings(config);

        config.set("item", this.item.name());
    }

}
