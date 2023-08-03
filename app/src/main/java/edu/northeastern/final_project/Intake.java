package edu.northeastern.final_project;

public class Intake {
    public String mealType, mealName, calories, protein, carbs, macros;

    public Intake (String mealtype, String mealname, String calories, String protein, String carbs, String macros) {
        this.mealType = mealtype;
        this.mealName = mealname;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.macros = macros;
    }
}
