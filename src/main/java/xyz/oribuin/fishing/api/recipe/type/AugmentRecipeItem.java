package xyz.oribuin.fishing.api.recipe.type;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.recipe.RecipeItem;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.augment.AugmentRegistry;
import xyz.oribuin.fishing.storage.util.PersistKeys;

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
        String augmentType = container.get(PersistKeys.AUGMENT_TYPE, PersistentDataType.STRING);

        return augmentType != null && augmentType.equalsIgnoreCase(this.item.name());
    }


    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        super.loadSettings(config);

        this.item = AugmentRegistry.from(config.getString("item"));
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        super.saveSettings(config);

        config.set("item", this.item.name());
    }

}
