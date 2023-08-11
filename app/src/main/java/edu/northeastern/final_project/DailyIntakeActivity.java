package edu.northeastern.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DailyIntakeActivity extends AppCompatActivity {

    /*** TODO: maybe change into spinner - string variable has the options***/
    private TextInputLayout mealTypeLayout, mealNameLayout, mealPortionLayout, caloriesLayout, proteinLayout, carbsLayout, macrosLayout;
    private TextInputEditText mealNameEt, mealPortionEt, caloriesEt, proteinEt, carbsEt, fatsEt;
    private TextView mealPortionTv;
    private AutoCompleteTextView mealTypeEt;
    private Button addDailyIntakeBtn;
    private DBHandler dbHandler;
    public ArrayList<Intake> intakeData;
    public float originalPortion, originalCalories, originalProtein, originalCarbs, originalFats;

    // Testing
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final long DELAY = 300;
    private boolean itemSelected = false;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_intake);

        mealTypeLayout = findViewById(R.id.mealTypeLayout);
        mealNameLayout = findViewById(R.id.mealNameLayout);
        mealPortionLayout = findViewById(R.id.mealPortionLayout);
        caloriesLayout = findViewById(R.id.caloriesLayout);
        proteinLayout = findViewById(R.id.proteinsLayout);
        carbsLayout = findViewById(R.id.carbsLayout);
        macrosLayout = findViewById(R.id.macrosLayout);

        mealTypeEt = findViewById(R.id.mealTypeACTV);
        mealNameEt = findViewById(R.id.mealNameET);
        mealPortionEt = findViewById(R.id.mealPortionET);
        caloriesEt = findViewById(R.id.calories);
        proteinEt = findViewById(R.id.proteins);
        carbsEt = findViewById(R.id.carbs);
        fatsEt = findViewById(R.id.fats);
        mealPortionTv = findViewById(R.id.mealPortionTv);

        addDailyIntakeBtn = findViewById(R.id.AddDailyBtn);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e("DailyIntakeActivity", "No user found!");
        }

        String uid = currentUser.getUid();
        dbHandler = new DBHandler(DailyIntakeActivity.this, uid);


        Executor executor = Executors.newSingleThreadExecutor();
        mealTypeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                itemSelected = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();

                handler.removeCallbacksAndMessages(null);

                handler.postDelayed(() -> {
                    if (!itemSelected) {
                        executor.execute(() -> {
                            FDAKeywordQuery fdaKeywordQuery = new FDAKeywordQuery(input);
                            ArrayList<FoodData> searchResult = fdaKeywordQuery.search();
                            if (searchResult != null) {
                                List<String> foodNames = new ArrayList<>();
                                int dataCount = 0;
                                for (FoodData foodData : searchResult) {
                                    if (dataCount >= 5) break;
                                    @SuppressLint("DefaultLocale") String name = String.format("%s %s - %2dP/%2dC/%2dF", foodData.getName(), foodData.getServingSize(), (int) foodData.getProtein(), (int) foodData.getCarbs(), (int) foodData.getFats());
                                    foodNames.add(name);
                                    dataCount++;
                                }

                                runOnUiThread(() -> {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DailyIntakeActivity.this, android.R.layout.simple_dropdown_item_1line, foodNames);
                                    mealTypeEt.setAdapter(adapter);
                                    mealTypeEt.showDropDown();
                                    mealTypeEt.setOnItemClickListener((parent, view, position, id) -> {
                                        itemSelected = true;
                                        FoodData selectedFood = searchResult.get(position);
                                        mealNameEt.setText(selectedFood.getName());
                                        mealPortionEt.setText(String.valueOf(selectedFood.getServingSize()));
                                        mealPortionTv.setText(selectedFood.getPortionUnit());
                                        caloriesEt.setText(String.valueOf(selectedFood.getCals()));
                                        proteinEt.setText(String.valueOf(selectedFood.getProtein()));
                                        carbsEt.setText(String.valueOf(selectedFood.getCarbs()));
                                        fatsEt.setText(String.valueOf(selectedFood.getFats()));

                                        originalPortion = Float.parseFloat(selectedFood.getServingSize());
                                        originalCalories = selectedFood.getCals();
                                        originalProtein = selectedFood.getProtein();
                                        originalCarbs = selectedFood.getCarbs();
                                        originalFats = selectedFood.getFats();

                                        mealTypeEt.dismissDropDown();
                                        mealTypeEt.clearFocus();
                                    });
                                });
                            }
                        });
                    }
                }, DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mealPortionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    float newPortion = Float.parseFloat(s.toString());
                    if (originalPortion != 0) {
                        float ratio = newPortion / originalPortion;
                        caloriesEt.setText(String.valueOf(originalCalories * ratio));
                        proteinEt.setText(String.valueOf(originalProtein * ratio));
                        carbsEt.setText(String.valueOf(originalCarbs * ratio));
                        fatsEt.setText(String.valueOf(originalFats * ratio));
                    }
                } catch (NumberFormatException nfe) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addDailyIntakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mealType = mealTypeEt.getText().toString();
                String mealName = mealNameEt.getText().toString();
                String calories = caloriesEt.getText().toString();
                String protein = proteinEt.getText().toString();
                String carbs = carbsEt.getText().toString();
                String macros = fatsEt.getText().toString();

                if (mealType.isEmpty() && mealName.isEmpty() && calories.isEmpty() && protein.isEmpty() && calories.isEmpty() && carbs.isEmpty() && macros.isEmpty()) {
                    Toast.makeText(DailyIntakeActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHandler.addDailyIntake(mealType, mealName, calories, protein, carbs, macros);


                //add toast and set empty strings
/*                Toast.makeText(DailyIntakeActivity.this, "Daily intake has been added", Toast.LENGTH_LONG).show();
                mealNameEt.setText("");
                caloriesEt.setText("");
                proteinEt.setText("");
                carbsEt.setText("");
                fatsEt.setText("");*/

                //read intake from DB and push the data into the firebase
                intakeData = dbHandler.readIntake();

                //push the data into firebase
//                if (isOnLine() && intakeData.size()>0) {
//                    dbHandler.pushFirebase(intakeData);
//                }
                //dbHandler.pushFirebase(intakeData);

                Intent intent = new Intent(DailyIntakeActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                Toast.makeText(DailyIntakeActivity.this, "Daily intake has been added", Toast.LENGTH_LONG).show();
            }

            //check whether the device is online
            public boolean isOnLine() {

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(ConnectivityManager.class);
                //SDK version 29 and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

                    if (networkCapabilities == null) {
                        Log.d("Network Unavail", "Device offline");
                        return false;
                    } else {
                        Log.d("Network Avail", "Device online");
                        return true;
                    }

                } else {
                    /*** TODO: deal with SDK before 29 for network info***/

                }
                return false;
            }

        });
    }

    ;

}