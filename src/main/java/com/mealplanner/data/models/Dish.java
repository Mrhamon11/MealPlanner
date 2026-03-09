package com.mealplanner.data.models;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public record Dish(Set<Recipe> recipes, String name)
{
    public Dish(Set<Recipe> recipes)
    {
        this(recipes, null);
    }

    public Dish
    {
        recipes = recipes != null ? Collections.unmodifiableSet(recipes) : Collections.emptySet();
    }

    public boolean isFullyFreezable()
    {
        return recipes.stream().allMatch(Recipe::isFreezable);
    }

    public Set<Recipe> getFreezableComponents()
    {
        return recipes.stream()
                .filter(Recipe::isFreezable)
                .collect(Collectors.toUnmodifiableSet());
    }
}
