package dev.oribuin.fishing.api.recipe;

import dev.oribuin.fishing.economy.Cost;

import java.util.List;

public interface Recipe<T> {

    /**
     * The result of the recipe
     *
     * @return The result of the recipe
     */
    T result();

    /**
     * The list of ingredients for the recipe to be crafted
     *
     * @return The list of ingredients
     */
    List<RecipeItem<?>> ingredients();

    /**
     * The cost of the recipe
     *
     * @return The cost of the recipe
     */
    Cost cost();

}
