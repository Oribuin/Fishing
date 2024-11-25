package xyz.oribuin.fishing.api.recipe.type;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.recipe.RecipeItem;
import xyz.oribuin.fishing.util.FishUtils;

public class MaterialRecipeItem extends RecipeItem<Material> {

    public MaterialRecipeItem() {
        super(Material.class);
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
        return item != null && item.getType() == this.item();
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

        this.item = FishUtils.getEnum(this.type(), config.getString("item"), Material.BEDROCK);
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

        config.set("item", this.item().name());
    }

}
