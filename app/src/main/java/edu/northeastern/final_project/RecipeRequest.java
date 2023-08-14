package edu.northeastern.final_project;

import com.google.gson.annotations.SerializedName;

public class RecipeRequest {
    @SerializedName("preferred_ingredients")
    private String preferredIngredients;

    @SerializedName("calorie_limit")
    private String calorieLimit;

    @SerializedName("allergic_ingredients")
    private String allergicIngredients;

    @SerializedName("meal_type")
    private String mealType;

    @SerializedName("cooking_time")
    private String cookingTime;

    @SerializedName("serving_size")
    private String servingSize;

    @SerializedName("dietary_preference")
    private String dietaryPreference;

    @SerializedName("nutrition_needs")
    private String nutritionNeeds;

    @SerializedName("cuisine_type")
    private String cuisineType;

    @SerializedName("skill_level")
    private String skillLevel;

    public RecipeRequest(String preferredIngredients, String calorieLimit, String allergicIngredients,
                         String mealType, String cookingTime, String servingSize, String dietaryPreference,
                         String nutritionNeeds, String cuisineType, String skillLevel) {
        this.preferredIngredients = preferredIngredients;
        this.calorieLimit = calorieLimit;
        this.allergicIngredients = allergicIngredients;
        this.mealType = mealType;
        this.cookingTime = cookingTime;
        this.servingSize = servingSize;
        this.dietaryPreference = dietaryPreference;
        this.nutritionNeeds = nutritionNeeds;
        this.cuisineType = cuisineType;
        this.skillLevel = skillLevel;
    }
}
