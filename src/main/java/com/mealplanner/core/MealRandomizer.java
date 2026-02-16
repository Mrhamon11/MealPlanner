package com.mealplanner.core;

import com.mealplanner.data.models.Dish;
import com.mealplanner.data.models.FoodType;
import com.mealplanner.data.models.MealComponent;

import java.util.*;
import java.util.stream.Collectors;

public class MealRandomizer
{
    private final Random random;

    public MealRandomizer(Random random)
    {
        this.random = random;
    }

    public Dish generateDishFromMealComponents(Set<MealComponent> mealComponents)
    {
        Map<FoodType, List<MealComponent>> mealComponentsByFoodType = splitMealComponentsByFoodType(mealComponents);

        MealComponent selectedComponent = selectInitialMealComponent(mealComponentsByFoodType);

        // it the selected initial type is one pot, dish is self-contained, no need to proceed
        if (selectedComponent.getFoodType() == FoodType.ONE_POT)
        {
            return new Dish(Set.of(selectedComponent));
        }

        // else, it's a protein, so we need to build the dish
        Set<MealComponent> componentsInDish = new HashSet<>();
        componentsInDish.add(selectedComponent);

        FoodType veggieFoodTypeToUse = getVeggieFoodTypeToUse();
        componentsInDish.add(
                getVeggieComponentFromMap(mealComponentsByFoodType, veggieFoodTypeToUse, selectedComponent));

        componentsInDish.add(getRandomMealComponent(mealComponentsByFoodType.get(FoodType.CARB)));

        return new Dish(componentsInDish);
    }

    private Map<FoodType, List<MealComponent>> splitMealComponentsByFoodType(Set<MealComponent> mealComponents)
    {
        Map<FoodType, List<MealComponent>> map = new HashMap<>();
        for (MealComponent mealComponent : mealComponents)
        {
            FoodType foodType = mealComponent.getFoodType();
            map.computeIfAbsent(foodType, ft -> new ArrayList<>());
            map.get(foodType).add(mealComponent);
        }
        return map;
    }

    private MealComponent selectInitialMealComponent(Map<FoodType, List<MealComponent>> mealComponentsByFoodType)
    {
        FoodType initialFoodTypeToUse = this.random.nextInt(2) == 0 ? FoodType.PROTEIN : FoodType.ONE_POT;
        List<MealComponent> mealComponents = mealComponentsByFoodType.remove(initialFoodTypeToUse);
        return getRandomMealComponent(mealComponents);
    }

    private MealComponent getRandomMealComponent(List<MealComponent> mealComponents)
    {
        int randIndex = this.random.nextInt(mealComponents.size());
        return mealComponents.get(randIndex);
    }

    private FoodType getVeggieFoodTypeToUse()
    {
        return this.random.nextInt(2) == 0 ? FoodType.VEGGIE : FoodType.SALAD;
    }

    private MealComponent getVeggieComponentFromMap(Map<FoodType, List<MealComponent>> mealComponentsMap,
            FoodType veggieTypeToUse, MealComponent selectedProtein)
    {
        List<MealComponent> mealComponentsForSelectedVeggieType = mealComponentsMap.get(veggieTypeToUse);
        MealComponent initialVeggie = getRandomMealComponent(mealComponentsForSelectedVeggieType.stream()
                .filter(mealComponent -> mealComponent.canBeEatenWithOtherMealComponent(selectedProtein))
                .collect(Collectors.toList()));

        if (initialVeggie != null)
        {
            return initialVeggie;
        }

        FoodType veggieDefault = veggieTypeToUse == FoodType.VEGGIE ? FoodType.SALAD : FoodType.VEGGIE;
        List<MealComponent> mealComponentsForVeggieDefault = mealComponentsMap.get(veggieDefault);
        return getRandomMealComponent(mealComponentsForVeggieDefault.stream()
                .filter(mealComponent -> mealComponent.canBeEatenWithOtherMealComponent(selectedProtein))
                .collect(Collectors.toList()));
    }
}
