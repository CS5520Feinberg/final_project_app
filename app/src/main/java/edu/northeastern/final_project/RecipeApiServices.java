package edu.northeastern.final_project;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

//public interface RecipeApiServices {
//    @POST("api/recipe-generator/")
//    Call<RecipeResponse> generateRecipe(@Body RecipeRequest request);
//}



public interface RecipeApiServices {
    @POST("api/recipe-generator/")
    Call<Map<String, String>> generateRecipe(@Body RecipeRequest request);
}