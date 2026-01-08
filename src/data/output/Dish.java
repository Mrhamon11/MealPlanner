package data.output;

import data.models.MealComponent;

import java.util.Set;

public class Dish
{
    private final Set<MealComponent> components;

    public Dish(Set<MealComponent> components)
    {
        this.components = components;
    }

    public Set<MealComponent> getComponents()
    {
        return this.components;
    }
}
