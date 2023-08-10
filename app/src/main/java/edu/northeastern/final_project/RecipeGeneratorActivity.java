package edu.northeastern.final_project;




import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeGeneratorActivity extends AppCompatActivity {
    private EditText editTextPreferredIngredients;
    private EditText editTextCalorieLimit;
    private EditText editTextAllergicIngredients;
    private Spinner spinnerMealType;
    private EditText editTextCookingTime;
    private EditText editTextServingSize;
    private Spinner spinnerDietaryPreference;
    private Spinner spinnerNutritionNeeds;
    private Spinner spinnerCuisineType;
    private Spinner spinnerSkillLevel;
    private Button buttonGenerateRecipe;
    private RecipeApiServices apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_generator);

        editTextPreferredIngredients = findViewById(R.id.editTextPreferredIngredients);
        editTextCalorieLimit = findViewById(R.id.editTextCalorieLimit);
        editTextAllergicIngredients = findViewById(R.id.editTextAllergicIngredients);
        spinnerMealType = findViewById(R.id.spinnerMealType);
        editTextCookingTime = findViewById(R.id.editTextCookingTime);
        editTextServingSize = findViewById(R.id.editTextServingSize);
        spinnerDietaryPreference = findViewById(R.id.spinnerDietaryPreference);
        spinnerNutritionNeeds = findViewById(R.id.spinnerNutritionNeeds);
        spinnerCuisineType = findViewById(R.id.spinnerCuisineType);
        spinnerSkillLevel = findViewById(R.id.spinnerSkillLevel);
        buttonGenerateRecipe = findViewById(R.id.buttonGenerateRecipe);

        ArrayAdapter<CharSequence> mealTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.meal_types, android.R.layout.simple_spinner_item);
        mealTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMealType.setAdapter(mealTypeAdapter);

        // Initialize other adapters...

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.110.180.253:8000/")  // Replace with your API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(RecipeApiServices.class);

        buttonGenerateRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateRecipe();
            }
        });
    }

    private void generateRecipe() {
        String preferredIngredients = editTextPreferredIngredients.getText().toString();
        String calorieLimit = editTextCalorieLimit.getText().toString();
        String allergicIngredients = editTextAllergicIngredients.getText().toString();
        String mealType = spinnerMealType.getSelectedItem().toString();
        String cookingTime = editTextCookingTime.getText().toString();
        String servingSize = editTextServingSize.getText().toString();
        String dietaryPreference = spinnerDietaryPreference.getSelectedItem().toString();
        String nutritionNeeds = spinnerNutritionNeeds.getSelectedItem().toString();
        String cuisineType = spinnerCuisineType.getSelectedItem().toString();
        String skillLevel = spinnerSkillLevel.getSelectedItem().toString();

        RecipeRequest recipeRequest = new RecipeRequest(
                preferredIngredients, calorieLimit, allergicIngredients,
                mealType, cookingTime, servingSize, dietaryPreference,
                nutritionNeeds, cuisineType, skillLevel
        );

        Call<Map<String, String>> call = apiService.generateRecipe(recipeRequest);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Map<String, String> recipeDict = response.body();
                    String recipeName = recipeDict.get("Recipe Name");
                    String recipeIngredients = recipeDict.get("Recipe Ingredients");
                    String recipeInstructions = recipeDict.get("Recipe Instructions");

                    // Use the recipe details as needed in your app
                    showRecipePopup(recipeName, recipeIngredients, recipeInstructions);
                } else {
                    showErrorToast("API Error");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                showErrorToast("API Request Failed: " + t.getMessage());
            }
        });
    }



    private void showRecipePopup(String recipeName, String recipeIngredients, String recipeInstructions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(recipeName)
                .setMessage("Ingredients: " + recipeIngredients
                        + "\nInstructions: " + recipeInstructions)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

//    private void showRecipePopup(RecipeResponse recipeResponse) {
//        Log.d("Recipe", recipeResponse.toString());
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(recipeResponse.getRecipeName())
//                .setMessage("Ingredients: " + recipeResponse.getRecipeIngredients()
//                        + "\nInstructions: " + recipeResponse.getRecipeInstructions())
//                .setPositiveButton("OK", null)
//                .show();
//    }
//
//    private void showErrorToast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }
}

//import android.app.AlertDialog;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class RecipeGeneratorActivity extends AppCompatActivity {
//    private EditText editTextPreferredIngredients;
//    private EditText editTextCalorieLimit;
//    private EditText editTextAllergicIngredients;
//    private Spinner spinnerMealType;
//    private EditText editTextCookingTime;
//    private EditText editTextServingSize;
//    private Spinner spinnerDietaryPreference;
//    private Spinner spinnerNutritionNeeds;
//    private Spinner spinnerCuisineType;
//    private Spinner spinnerSkillLevel;
//    private Button buttonGenerateRecipe;
//    private RecipeApiServices apiService;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_recipe_generator);
//
//        editTextPreferredIngredients = findViewById(R.id.editTextPreferredIngredients);
//        editTextCalorieLimit = findViewById(R.id.editTextCalorieLimit);
//        editTextAllergicIngredients = findViewById(R.id.editTextAllergicIngredients);
//        spinnerMealType = findViewById(R.id.spinnerMealType);
//        editTextCookingTime = findViewById(R.id.editTextCookingTime);
//        editTextServingSize = findViewById(R.id.editTextServingSize);
//        spinnerDietaryPreference = findViewById(R.id.spinnerDietaryPreference);
//        spinnerNutritionNeeds = findViewById(R.id.spinnerNutritionNeeds);
//        spinnerCuisineType = findViewById(R.id.spinnerCuisineType);
//        spinnerSkillLevel = findViewById(R.id.spinnerSkillLevel);
//        buttonGenerateRecipe = findViewById(R.id.buttonGenerateRecipe);
//
//        ArrayAdapter<CharSequence> mealTypeAdapter = ArrayAdapter.createFromResource(
//                this, R.array.meal_types, android.R.layout.simple_spinner_item);
//        mealTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerMealType.setAdapter(mealTypeAdapter);
//
//        ArrayAdapter<CharSequence> dietaryPreferenceAdapter = ArrayAdapter.createFromResource(
//                this, R.array.dietary_preferences, android.R.layout.simple_spinner_item);
//        dietaryPreferenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerDietaryPreference.setAdapter(dietaryPreferenceAdapter);
//
//        ArrayAdapter<CharSequence> nutritionNeedsAdapter = ArrayAdapter.createFromResource(
//                this, R.array.nutrition_needs, android.R.layout.simple_spinner_item);
//        nutritionNeedsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerNutritionNeeds.setAdapter(nutritionNeedsAdapter);
//
//        ArrayAdapter<CharSequence> cuisineTypeAdapter = ArrayAdapter.createFromResource(
//                this, R.array.cuisine_types, android.R.layout.simple_spinner_item);
//        cuisineTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerCuisineType.setAdapter(cuisineTypeAdapter);
//
//        ArrayAdapter<CharSequence> skillLevelAdapter = ArrayAdapter.createFromResource(
//                this, R.array.skill_levels, android.R.layout.simple_spinner_item);
//        skillLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerSkillLevel.setAdapter(skillLevelAdapter);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://localhost:8000/")  // Replace with your API base URL
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        apiService = retrofit.create(RecipeApiServices.class);
//
//        buttonGenerateRecipe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                generateRecipe();
//            }
//        });
//    }
//
//    private void generateRecipe() {
//        String preferredIngredients = editTextPreferredIngredients.getText().toString();
//        String calorieLimit = editTextCalorieLimit.getText().toString();
//        String allergicIngredients = editTextAllergicIngredients.getText().toString();
//        String mealType = spinnerMealType.getSelectedItem().toString();
//        String cookingTime = editTextCookingTime.getText().toString();
//        String servingSize = editTextServingSize.getText().toString();
//        String dietaryPreference = spinnerDietaryPreference.getSelectedItem().toString();
//        String nutritionNeeds = spinnerNutritionNeeds.getSelectedItem().toString();
//        String cuisineType = spinnerCuisineType.getSelectedItem().toString();
//        String skillLevel = spinnerSkillLevel.getSelectedItem().toString();
//
//        String inputData = "Preferred Ingredients: " + preferredIngredients +
//                ", Calorie Limit: " + calorieLimit +
//                ", Allergic Ingredients: " + allergicIngredients +
//                ", Meal Type: " + mealType +
//                ", Cooking Time: " + cookingTime +
//                ", Serving Size: " + servingSize +
//                ", Dietary Preference: " + dietaryPreference +
//                ", Nutrition Needs: " + nutritionNeeds +
//                ", Cuisine Type: " + cuisineType +
//                ", Skill Level: " + skillLevel;
//
//        Call<RecipeResponse> call = apiService.generateRecipe(inputData);
//        call.enqueue(new Callback<RecipeResponse>() {
//            @Override
//            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
//                if (response.isSuccessful()) {
//                    RecipeResponse recipeResponse = response.body();
//                    showRecipePopup(recipeResponse);
//                } else {
//                    showErrorToast("API Error");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RecipeResponse> call, Throwable t) {
//                showErrorToast("API Request Failed: " + t.getMessage());
//            }
//        });
//    }
//
//    private void showRecipePopup(RecipeResponse recipeResponse) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(recipeResponse.getRecipeName())
//                .setMessage("Ingredients: " + recipeResponse.getRecipeIngredients()
//                        + "\nInstructions: " + recipeResponse.getRecipeInstructions())
//                .setPositiveButton("OK", null)
//                .show();
//    }
//
//    private void showErrorToast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }
//}
