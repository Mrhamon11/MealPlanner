package com.mealplanner.core;

import com.mealplanner.data.models.Dish;
import com.mealplanner.data.models.FoodType;
import com.mealplanner.data.models.KashrutStatus;
import com.mealplanner.data.models.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DishConstructorTest
{

    private DishConstructor dishConstructor;
    private Random random;
    private Set<Recipe> recipes;

    @BeforeEach
    public void setUp()
    {
        this.dishConstructor = new DishConstructor();
        this.random = new Random(42);
        this.recipes = new HashSet<>();
    }

    private Recipe createRecipe(String name, FoodType foodType, KashrutStatus kashrutStatus, boolean canBeVegan)
    {
        return new Recipe(name, null, false, foodType, 0, kashrutStatus, canBeVegan, 0.0);
    }

    @Test
    public void testBothMeatAndDairyReturnsNull()
    {
        Set<KashrutStatus> preferredKashrutStatus =
                new HashSet<>(Arrays.asList(KashrutStatus.MEAT, KashrutStatus.DAIRY));
        Dish dish = this.dishConstructor.constructDish(this.random, this.recipes, true, null, preferredKashrutStatus,
                false);
        assertNull(dish);
    }

    @Test
    public void testPreferredFoodTypeFiltering()
    {
        this.recipes.add(createRecipe("OnePot1", FoodType.ONE_POT, KashrutStatus.PARVE, false));
        this.recipes.add(createRecipe("Soup1", FoodType.SOUP, KashrutStatus.PARVE, false));
        this.recipes.add(createRecipe("Protein1", FoodType.PROTEIN, KashrutStatus.PARVE, false));

        Set<FoodType> preferredFoodType = new HashSet<>(Collections.singletonList(FoodType.PROTEIN));
        Dish dish =
                this.dishConstructor.constructDish(this.random, this.recipes, false, preferredFoodType, null, false);

        assertNotNull(dish);
        assertEquals(1, dish.recipes().size());
        assertEquals(FoodType.PROTEIN, dish.recipes().iterator().next().foodType());
    }

    @Test
    public void testKashrutMeatFiltering()
    {
        this.recipes.add(createRecipe("MeatRecipe", FoodType.ONE_POT, KashrutStatus.MEAT, false));
        this.recipes.add(createRecipe("DairyRecipe", FoodType.ONE_POT, KashrutStatus.DAIRY, false));
        this.recipes.add(createRecipe("DairyVeganRecipe", FoodType.ONE_POT, KashrutStatus.DAIRY, true));
        this.recipes.add(createRecipe("ParveRecipe", FoodType.ONE_POT, KashrutStatus.PARVE, false));

        Set<KashrutStatus> preferredKashrutStatus = new HashSet<>(Collections.singletonList(KashrutStatus.MEAT));

        Dish dish = this.dishConstructor.constructDish(this.random, this.recipes, true, null, preferredKashrutStatus,
                false);
        assertNotNull(dish);
        KashrutStatus status = dish.recipes().iterator().next().kashrutStatus();
        assertTrue(status == KashrutStatus.MEAT || status == KashrutStatus.PARVE);

        // Case 2: allowVeganSubstitutions = true
        // We want to make sure DairyVeganRecipe is reachable.
        // We'll run it a few times to increase the chance of picking it.
        boolean foundVegan = false;
        for (int i = 0; i < 20; i++)
        {
            dish = this.dishConstructor.constructDish(new Random(i), this.recipes, true, null, preferredKashrutStatus,
                    true);
            Recipe r = dish.recipes().iterator().next();
            if (r.kashrutStatus() == KashrutStatus.DAIRY && r.canBeVegan())
            {
                foundVegan = true;
                break;
            }
        }
        assertTrue(foundVegan, "Should have found a vegan dairy recipe when allowed");
    }

    @Test
    public void testFactorInKashrutFalseIgnoresKashrutStatus()
    {
        this.dishConstructor = new DishConstructor();
        this.recipes = new HashSet<>();
        this.recipes.add(createRecipe("MeatRecipe", FoodType.ONE_POT, KashrutStatus.MEAT, false));
        this.recipes.add(createRecipe("DairyRecipe", FoodType.ONE_POT, KashrutStatus.DAIRY, false));

        // Use a preferred kashrut status that WOULD normally filter out DAIRY
        Set<KashrutStatus> preferredKashrutStatus = new HashSet<>(Collections.singletonList(KashrutStatus.MEAT));

        // With factorInKashrut=false, we expect to get both MEAT and DAIRY recipes over multiple runs
        boolean foundDairy = false;
        boolean foundMeat = false;
        for (int i = 0; i < 100; i++)
        {
            // Using a new Random without a fixed seed to ensure variety
            Dish dish =
                    this.dishConstructor.constructDish(new Random(), this.recipes, false, null, preferredKashrutStatus,
                            false);
            if (dish != null)
            {
                Recipe r = dish.recipes().iterator().next();
                if (r.kashrutStatus() == KashrutStatus.DAIRY)
                {
                    foundDairy = true;
                }
                if (r.kashrutStatus() == KashrutStatus.MEAT)
                {
                    foundMeat = true;
                }
            }
        }
        assertTrue(foundDairy, "Should have been able to pick a dairy recipe when factorInKashrut is false");
        assertTrue(foundMeat, "Should have been able to pick a meat recipe");
    }

    @Test
    public void testFactorInKashrutTrueDoesNotIgnoreKashrutStatus()
    {
        this.dishConstructor = new DishConstructor();
        this.recipes = new HashSet<>();
        this.recipes.add(createRecipe("MeatRecipe", FoodType.ONE_POT, KashrutStatus.MEAT, false));
        this.recipes.add(createRecipe("DairyRecipe", FoodType.ONE_POT, KashrutStatus.DAIRY, false));

        Set<KashrutStatus> preferredKashrutStatus = new HashSet<>(Collections.singletonList(KashrutStatus.MEAT));

        for (int i = 0; i < 20; i++)
        {
            Dish dish =
                    this.dishConstructor.constructDish(new Random(i), this.recipes, true, null, preferredKashrutStatus,
                            false);
            Recipe r = dish.recipes().iterator().next();
            assertNotEquals(KashrutStatus.DAIRY, r.kashrutStatus(),
                    "Should NOT have picked a dairy recipe when factorInKashrut is true");
        }
    }

    @Test
    public void testOnePotReturnsSingleRecipeDish()
    {
        this.recipes.add(createRecipe("OnePot", FoodType.ONE_POT, KashrutStatus.PARVE, false));
        Dish dish = this.dishConstructor.constructDish(this.random, this.recipes, false, null, null, false);
        assertNotNull(dish);
        assertEquals(1, dish.recipes().size());
        assertEquals(FoodType.ONE_POT, dish.recipes().iterator().next().foodType());
    }

    @Test
    public void testValidCombinationProteinCarbVeggie()
    {
        this.recipes.add(createRecipe("Protein", FoodType.PROTEIN, KashrutStatus.PARVE, false));
        this.recipes.add(createRecipe("Carb", FoodType.CARB, KashrutStatus.PARVE, false));
        this.recipes.add(createRecipe("Veggie", FoodType.VEGGIE, KashrutStatus.PARVE, false));

        Dish dish = this.dishConstructor.constructDish(this.random, this.recipes, false, null, null, false);
        assertNotNull(dish);
        assertEquals(3, dish.recipes().size());

        Set<FoodType> types = new HashSet<>();
        for (Recipe r : dish.recipes())
        {
            types.add(r.foodType());
        }
        assertTrue(types.contains(FoodType.PROTEIN));
        assertTrue(types.contains(FoodType.CARB));
        assertTrue(types.contains(FoodType.VEGGIE));
    }
}
