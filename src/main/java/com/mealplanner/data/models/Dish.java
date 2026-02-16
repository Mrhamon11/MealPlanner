package com.mealplanner.data.models;

import java.util.HashSet;
import java.util.Set;

public class Dish
{
    private final Set<MealComponent> components;
    private String name;
    private final boolean isFullyFreezable;
    private final Set<MealComponent> freezableComponents;

    public Dish(Set<MealComponent> components)
    {
        this.components = components;

        this.freezableComponents = new HashSet<>();
        for (MealComponent mealComponent : this.components)
        {
            if (mealComponent.isFreezable())
            {
                this.freezableComponents.add(mealComponent);
            }
        }

        this.isFullyFreezable = this.components.size() == this.freezableComponents.size();
    }

    public Set<MealComponent> getComponents()
    {
        return this.components;
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
        return this.isFullyFreezable;
    }

    public Set<MealComponent> getFreezableComponents()
    {
        return this.freezableComponents;
    }
}
