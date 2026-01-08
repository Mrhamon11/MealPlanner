package data.models;

import java.util.Set;

public class MealComponent
{
    private String name;
    private Set<String> ingredients;
    private boolean isFreezable;
    private FoodType foodType;
    private int numTimesUsed;
    private KashrutStatus kashrutStatus;

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Set<String> getIngredients()
    {
        return this.ingredients;
    }

    public void setIngredients(Set<String> ingredients)
    {
        this.ingredients = ingredients;
    }

    public boolean isFreezable()
    {
        return this.isFreezable;
    }

    public void setFreezable(boolean freezable)
    {
        this.isFreezable = freezable;
    }

    public FoodType getFoodType()
    {
        return this.foodType;
    }

    public void setFoodType(FoodType foodType)
    {
        this.foodType = foodType;
    }

    public int getNumTimesUsed()
    {
        return this.numTimesUsed;
    }

    public void setNumTimesUsed(int numTimesUsed)
    {
        this.numTimesUsed = numTimesUsed;
    }

    public KashrutStatus getKashrutStatus()
    {
        return this.kashrutStatus;
    }

    public void setKashrutStatus(KashrutStatus kashrutStatus)
    {
        this.kashrutStatus = kashrutStatus;
    }

    public boolean canBeEatenWithOtherMealComponent(MealComponent mealComponent)
    {
        KashrutStatus otherKashrutStatus = mealComponent.getKashrutStatus();
        return (this.kashrutStatus == KashrutStatus.PARVE) || (otherKashrutStatus == KashrutStatus.PARVE) ||
                (otherKashrutStatus != this.kashrutStatus);
    }
}
