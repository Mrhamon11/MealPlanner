package core;

import data.models.FoodType;
import data.models.MealComponent;
import data.output.Dish;

import java.util.*;

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
        List<MealComponent> proteins = mealComponentsByFoodType.remove(FoodType.PROTEIN);
        int randIndexProteins = this.random.nextInt(proteins.size());
        MealComponent selectedProtein = proteins.get(randIndexProteins);

        Set<MealComponent> componentsInDish = new HashSet<>();
        componentsInDish.add(selectedProtein);

        for (FoodType foodType : mealComponentsByFoodType.keySet())
        {
            // TODO will this be a bug since we are removing elements in a loop without an iterator? Might have to do this outside of the for loop?
            List<MealComponent> mealComponentList = removeMealComponents(mealComponentsByFoodType.get(foodType),
                    selectedProtein);
            int randIndex = this.random.nextInt(mealComponentList.size());
            componentsInDish.add(mealComponentList.get(randIndex));
        }

        return new Dish(componentsInDish);
    }

    private Map<FoodType, List<MealComponent>> splitMealComponentsByFoodType(Set<MealComponent> mealComponents)
    {
        Map<FoodType, List<MealComponent>> map = new HashMap<>();
        for (MealComponent mealComponent : mealComponents)
        {
            FoodType foodType = mealComponent.getFoodType();
            map.computeIfAbsent(foodType, k -> new ArrayList<>());
            map.get(foodType).add(mealComponent);
        }
        return map;
    }

    // TODO This might need to be done outside of the loop it's called in, and might need to be smarter
    private List<MealComponent> removeMealComponents(List<MealComponent> mealComponents, MealComponent comparingAgainst)
    {
        mealComponents.removeIf(next -> !next.canBeEatenWithOtherMealComponent(comparingAgainst));

        FoodType veggieTypeToRemove = this.random.nextInt(2) == 0 ? FoodType.VEGGIE : FoodType.SALAD;
        mealComponents.removeIf(next -> next.getFoodType() == veggieTypeToRemove);

        return mealComponents;
    }
}
