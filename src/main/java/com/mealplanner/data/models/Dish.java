package com.mealplanner.data.models;

import java.util.HashSet;
import java.util.Set;

public class Dish
{
    private final Set<Recipe> recipes;
    private String name;
    private final Set<Recipe> freezableComponents;

    public Dish(Set<Recipe> recipes)
    {
        this.recipes = recipes;

        this.freezableComponents = new HashSet<>();
        for (Recipe recipe : this.recipes)
        {
            if (recipe.isFreezable())
            {
                this.freezableComponents.add(recipe);
            }
        }
    }

    public Set<Recipe> getRecipes()
    {
        return this.recipes;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isFullyFreezable()
    {
        return this.recipes.size() == this.freezableComponents.size();
    }

    public Set<Recipe> getFreezableComponents()
    {
        return this.freezableComponents;
    }
}
