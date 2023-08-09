package edu.northeastern.final_project;

public class Intake {
    public String mealType, mealName, calories, protein, carbs, fats, timestamp;

    public Intake (String mealtype, String mealname, String calories, String protein, String carbs, String fats, String timestamp) {
        this.mealType = mealtype;
        this.mealName = mealname;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.timestamp = timestamp;
    }

    public float getCal() {
        return Float.parseFloat(calories);
    }

    public float getProtein() {
        return Float.parseFloat(protein);
    }

    public float getCarbs() {
        return Float.parseFloat(carbs);
    }

    public float getFats() {
        return Float.parseFloat(fats);
    }

    public String getTimestamp() {
        return timestamp;
    }
}
