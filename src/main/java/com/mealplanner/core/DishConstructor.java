package com.mealplanner.core;

import com.mealplanner.data.models.Dish;
import com.mealplanner.data.models.FoodType;
import com.mealplanner.data.models.KashrutStatus;
import com.mealplanner.data.models.Recipe;

import java.util.*;
import java.util.stream.Collectors;

public class DishConstructor
{

    public Dish constructDish(Random random, Set<Recipe> recipes, boolean factorInKashrut,
            Set<FoodType> preferredFoodType, Set<KashrutStatus> preferredKashrutStatus, boolean allowVeganSubstitutions)
    {
        Set<Recipe> filteredRecipes = filterByFoodType(recipes, preferredFoodType);

        if (factorInKashrut && preferredKashrutStatus != null)
        {
            filteredRecipes = filterByKashrutStatus(filteredRecipes, preferredKashrutStatus, allowVeganSubstitutions);
            if (filteredRecipes == null)
            {
                return null;
            }
        }

        if (filteredRecipes.isEmpty())
        {
            return null;
        }

        List<Recipe> filteredList = new ArrayList<>(filteredRecipes);
        Recipe randomRecipe = filteredList.get(random.nextInt(filteredList.size()));

        if (isSingleRecipeFoodType(randomRecipe.foodType()))
        {
            return createSingleRecipeDish(randomRecipe);
        }

        if (isMultiRecipeFoodType(randomRecipe.foodType()))
        {
            return attemptMultiRecipeDish(random, filteredRecipes, randomRecipe);
        }

        return null;
    }

    private Set<Recipe> filterByFoodType(Set<Recipe> recipes, Set<FoodType> preferredFoodType)
    {
        if (preferredFoodType != null)
        {
            return recipes.stream().filter(recipe -> preferredFoodType.contains(recipe.foodType()))
                    .collect(Collectors.toSet());
        }
        return new HashSet<>(recipes);
    }

    private Set<Recipe> filterByKashrutStatus(Set<Recipe> recipes, Set<KashrutStatus> preferredKashrutStatus,
            boolean allowVeganSubstitutions)
    {
        if (preferredKashrutStatus == null)
        {
            return recipes;
        }

        if (preferredKashrutStatus.contains(KashrutStatus.DAIRY) && preferredKashrutStatus.contains(KashrutStatus.MEAT))
        {
            return null;
        }

        if (preferredKashrutStatus.contains(KashrutStatus.MEAT))
        {
            return filterRecipes(recipes, KashrutStatus.DAIRY, allowVeganSubstitutions);
        }
        else if (preferredKashrutStatus.contains(KashrutStatus.DAIRY))
        {
            return filterRecipes(recipes, KashrutStatus.MEAT, allowVeganSubstitutions);
        }
        else if (preferredKashrutStatus.size() == 1 && preferredKashrutStatus.contains(KashrutStatus.PARVE))
        {
            return recipes.stream().filter(recipe -> (recipe.kashrutStatus() != KashrutStatus.MEAT &&
                    recipe.kashrutStatus() != KashrutStatus.DAIRY) ||
                    (allowVeganSubstitutions && recipe.canBeVegan())).collect(Collectors.toSet());
        }

        return recipes;
    }

    private Set<Recipe> filterRecipes(Set<Recipe> recipes, KashrutStatus excludedStatus,
            boolean allowVeganSubstitutions)
    {
        return recipes.stream().filter(recipe -> recipe.kashrutStatus() != excludedStatus ||
                (allowVeganSubstitutions && recipe.canBeVegan())).collect(Collectors.toSet());
    }

    private boolean isSingleRecipeFoodType(FoodType foodType)
    {
        return switch (foodType)
        {
            case ONE_POT, SOUP -> true;
            default -> false;
        };
    }

    private boolean isMultiRecipeFoodType(FoodType foodType)
    {
        return switch (foodType)
        {
            case PROTEIN, CARB, VEGGIE, SALAD -> true;
            default -> false;
        };
    }

    private Dish createSingleRecipeDish(Recipe recipe)
    {
        return new Dish(Set.of(recipe));
    }

    private Dish attemptMultiRecipeDish(Random random, Set<Recipe> filteredRecipes, Recipe initialRecipe)
    {
        List<Recipe> pool = filteredRecipes.stream().filter(r -> isMultiRecipeFoodType(r.foodType()))
                .toList();

        List<FoodType[]> validCombos = new ArrayList<>(getValidMultiRecipeCombos());
        Collections.shuffle(validCombos, random);

        for (var combo : validCombos)
        {
            Set<Recipe> selected = new HashSet<>();
            for (FoodType type : combo)
            {
                var typedRecipes =
                        pool.stream().filter(r -> r.foodType() == type).toList();
                if (!typedRecipes.isEmpty())
                {
                    selected.add(typedRecipes.get(random.nextInt(typedRecipes.size())));
                }
            }
            if (selected.size() == 3)
            {
                return new Dish(selected);
            }
        }

        return createSingleRecipeDish(initialRecipe);
    }

    private List<FoodType[]> getValidMultiRecipeCombos()
    {
        return List.of(
            new FoodType[]{FoodType.PROTEIN, FoodType.CARB, FoodType.VEGGIE},
            new FoodType[]{FoodType.PROTEIN, FoodType.CARB, FoodType.SALAD},
            new FoodType[]{FoodType.PROTEIN, FoodType.VEGGIE, FoodType.SALAD}
        );
    }
}
