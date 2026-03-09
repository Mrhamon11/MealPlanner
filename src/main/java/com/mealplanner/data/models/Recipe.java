package com.mealplanner.data.models;

import java.util.Collections;
import java.util.Set;

public record Recipe(
        String name,
        Set<String> ingredients,
        boolean isFreezable,
        FoodType foodType,
        int numTimesUsed,
        KashrutStatus kashrutStatus,
        boolean canBeVegan,
        double weight)
{
    public Recipe
    {
        ingredients = ingredients != null ? Collections.unmodifiableSet(ingredients) : Collections.emptySet();
    }

    public boolean isKashrutCompatible(Recipe recipe)
    {
        KashrutStatus otherKashrutStatus = recipe.kashrutStatus();
        return (this.kashrutStatus == KashrutStatus.PARVE) ||
               (otherKashrutStatus == KashrutStatus.PARVE) ||
               (otherKashrutStatus == this.kashrutStatus) ||
               this.canBeVegan ||
               recipe.canBeVegan();
    }
}
