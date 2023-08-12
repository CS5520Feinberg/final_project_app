package edu.northeastern.final_project;

public class Intake {
    public String mealType, mealName, calories, protein, carbs, fats, timestamp, isCloudSynced;

    public Intake() {
        // Default constructor
    }

    public Intake(String mealtype, String mealname, String calories, String protein, String carbs, String fats, String timestamp, String isCloudSynced) {
        this.mealType = mealtype;
        this.mealName = mealname;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.timestamp = timestamp;
        this.isCloudSynced = isCloudSynced;
    }

    public void setCloudSynced() {
        this.isCloudSynced = "1";
    }

    public void setCloudNotSynced() {
        this.isCloudSynced = "0";
    }
}
