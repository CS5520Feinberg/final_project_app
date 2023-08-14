package edu.northeastern.final_project;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Objects;

public class FoodData {
    private int PROTEIN_ID = 1003;
    private int FATS_ID = 1004;
    private int CARBS_ID = 1005;
    private int CALS_ID = 1008;
    private float protein_g = 0;
    private float fat_g = 0;
    private float carb_g = 0;
    private float kcal = 0;
    private int fdcId = -1;
    private String food_name = null;
    private float servingSize = -1;
    private String servingSizeUnit = null;

    public FoodData(JsonObject jsonData) {
        fdcId = jsonData.get("fdcId").getAsInt();
        food_name = jsonData.get("description").getAsString();

        if (jsonData.get("servingSize") != null) {
            servingSize = jsonData.get("servingSize").getAsFloat();
            servingSizeUnit = jsonData.get("servingSizeUnit").getAsString();
        }
        JsonArray nutrients = jsonData.get("foodNutrients").getAsJsonArray();

        for (int i = 0, size = nutrients.size(); i < size; i++) {
            JsonObject nutrient = nutrients.get(i).getAsJsonObject();
            int nutrientId = nutrient.get("nutrientId").getAsInt();

            if (nutrient.get("value") != null) {
                if (nutrientId == PROTEIN_ID) {
                    protein_g = nutrient.get("value").getAsInt();
                    if (!Objects.equals(nutrient.get("unitName").getAsString(), "G")) {
                        Log.e("FoodData", "Nutrient unit not in grams! Need to convert!");
                    }
                } else if (nutrientId == FATS_ID) {
                    fat_g = nutrient.get("value").getAsInt();
                    if (!Objects.equals(nutrient.get("unitName").getAsString(), "G")) {
                        Log.e("FoodData", "Nutrient unit not in grams! Need to convert!");
                    }
                } else if (nutrientId == CARBS_ID) {
                    carb_g = nutrient.get("value").getAsInt();
                    if (!Objects.equals(nutrient.get("unitName").getAsString(), "G")) {
                        Log.e("FoodData", "Nutrient unit not in grams! Need to convert!");
                    }
                } else if (nutrientId == CALS_ID) {
                    kcal = nutrient.get("value").getAsInt();
                    if (!Objects.equals(nutrient.get("unitName").getAsString(), "KCAL")) {
                        Log.e("FoodData", "Energy not in KCAL! Need to convert!");
                    }
                }
            }
        }
        return;
    }

    public String getName() {
        return food_name;
    }

    public int getFdcId() {
        return fdcId;
    }

    public float getProtein() {
        return protein_g;
    }

    public float getFats() {
        return fat_g;
    }

    public float getCarbs() {
        return carb_g;
    }

    public float getCals() {
        return kcal;
    }


    /*
     * Edited for display purposes.
     */
/*    public String getServingSize() {
        if (servingSizeUnit == null) {
            servingSizeUnit = "portion";
        }
        return String.valueOf(servingSize) + " " + servingSizeUnit;
    }*/

    public String getServingSize() {
        if (servingSize == -1) {
            return "1";
        }
        return String.valueOf(servingSize);
    }

    public String getPortionUnit() {
        if (servingSizeUnit == null) {
            return "portion";
        }
        return servingSizeUnit;
    }
}
