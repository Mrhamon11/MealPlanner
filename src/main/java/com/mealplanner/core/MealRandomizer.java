package com.mealplanner.core;

import com.mealplanner.data.models.Dish;
import com.mealplanner.data.models.FoodType;
import com.mealplanner.data.models.Recipe;

import java.util.*;
import java.util.stream.Collectors;

public class MealRandomizer
{
    private final Random random;

    public MealRandomizer(Random random)
    {
        this.random = random;
    }

    public Dish generateDishFromMealComponents(Set<Recipe> recipes)
    {
        Map<FoodType, List<Recipe>> mealComponentsByFoodType = this.splitMealComponentsByFoodType(recipes);

        Recipe selectedComponent = this.selectInitialMealComponent(mealComponentsByFoodType);

        // it the selected initial type is one pot, dish is self-contained, no need to proceed
        if (selectedComponent.getFoodType() == FoodType.ONE_POT)
        {
            return new Dish(Set.of(selectedComponent));
        }

        // else, it's a protein, so we need to build the dish
        Set<Recipe> componentsInDish = new HashSet<>();
        componentsInDish.add(selectedComponent);

        FoodType veggieFoodTypeToUse = this.getVeggieFoodTypeToUse();
        componentsInDish.add(
                this.getVeggieComponentFromMap(mealComponentsByFoodType, veggieFoodTypeToUse, selectedComponent));

        componentsInDish.add(this.getRandomMealComponent(mealComponentsByFoodType.get(FoodType.CARB)));

        return new Dish(componentsInDish);
    }

    private Map<FoodType, List<Recipe>> splitMealComponentsByFoodType(Set<Recipe> recipes)
    {
        Map<FoodType, List<Recipe>> map = new HashMap<>();
        for (Recipe recipe : recipes)
        {
            FoodType foodType = recipe.getFoodType();
            map.computeIfAbsent(foodType, ft -> new ArrayList<>());
            map.get(foodType).add(recipe);
        }
        return map;
    }

    private Recipe selectInitialMealComponent(Map<FoodType, List<Recipe>> mealComponentsByFoodType)
    {
        FoodType initialFoodTypeToUse = this.random.nextInt(2) == 0 ? FoodType.PROTEIN : FoodType.ONE_POT;
        List<Recipe> recipes = mealComponentsByFoodType.remove(initialFoodTypeToUse);
        return this.getRandomMealComponent(recipes);
    }

    private Recipe getRandomMealComponent(List<Recipe> recipes)
    {
        int randIndex = this.random.nextInt(recipes.size());
        return recipes.get(randIndex);
    }

    private FoodType getVeggieFoodTypeToUse()
    {
        return this.random.nextInt(2) == 0 ? FoodType.VEGGIE : FoodType.SALAD;
    }

    private Recipe getVeggieComponentFromMap(Map<FoodType, List<Recipe>> mealComponentsMap,
            FoodType veggieTypeToUse, Recipe selectedProtein)
    {
        List<Recipe> mealComponentsForSelectedVeggieType = mealComponentsMap.get(veggieTypeToUse);
        Recipe initialVeggie = this.getRandomMealComponent(mealComponentsForSelectedVeggieType.stream()
                .filter(recipe -> recipe.isKashrutCompatible(selectedProtein))
                .collect(Collectors.toList()));

        if (initialVeggie != null)
        {
            return initialVeggie;
        }

        FoodType veggieDefault = veggieTypeToUse == FoodType.VEGGIE ? FoodType.SALAD : FoodType.VEGGIE;
        List<Recipe> mealComponentsForVeggieDefault = mealComponentsMap.get(veggieDefault);
        return this.getRandomMealComponent(mealComponentsForVeggieDefault.stream()
                .filter(recipe -> recipe.isKashrutCompatible(selectedProtein))
                .collect(Collectors.toList()));
    }
}
