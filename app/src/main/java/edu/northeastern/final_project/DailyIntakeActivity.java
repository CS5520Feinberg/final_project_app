package edu.northeastern.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DailyIntakeActivity extends AppCompatActivity {

    /*** TODO: maybe change into spinner - string variable has the options***/
    private TextInputLayout mealTypeLayout, mealNameLayout, caloriesLayout, proteinLayout, carbsLayout, macrosLayout;
    private TextInputEditText mealNameEt, caloriesEt, proteinEt, carbsEt, macrosEt;
    private AutoCompleteTextView mealTypeEt;
    private Button addDailyIntakeBtn;
    private DBHandler dbHandler;
    public ArrayList<Intake> intakeData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_intake);

        mealTypeLayout = findViewById(R.id.mealTypeLayout);
        mealNameLayout = findViewById(R.id.mealNameLayout);
        caloriesLayout = findViewById(R.id.caloriesLayout);
        proteinLayout = findViewById(R.id.proteinsLayout);
        carbsLayout = findViewById(R.id.carbsLayout);
        macrosLayout = findViewById(R.id.macrosLayout);

        mealTypeEt = findViewById(R.id.mealTypeACTV);
        mealNameEt = findViewById(R.id.mealNameET);
        caloriesEt = findViewById(R.id.calories);
        proteinEt = findViewById(R.id.proteins);
        carbsEt = findViewById(R.id.carbs);
        macrosEt = findViewById(R.id.macros);

        addDailyIntakeBtn = findViewById(R.id.AddDailyBtn);

        dbHandler = new DBHandler(DailyIntakeActivity.this);


        Executor executor = Executors.newSingleThreadExecutor();
        mealTypeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();

                executor.execute(() -> {
                    FDAKeywordQuery fdaKeywordQuery = new FDAKeywordQuery(input);
                    ArrayList<FoodData> searchResult = fdaKeywordQuery.search();
                    if (searchResult != null) {
                        List<String> foodNames = new ArrayList<>();
                        int count = 0;
                        for (FoodData foodData : searchResult) {
                            if (count >= 5) break;
                            foodNames.add(foodData.getName());
                            count++;
                        }

                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(DailyIntakeActivity.this, android.R.layout.simple_dropdown_item_1line, foodNames);
                            mealTypeEt.setAdapter(adapter);
                        });
                    }
                });
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
                String macros = macrosEt.getText().toString();

                if  (mealType.isEmpty() && mealName.isEmpty() && calories.isEmpty() && protein.isEmpty() && calories.isEmpty() && carbs.isEmpty() && macros.isEmpty()) {
                    Toast.makeText(DailyIntakeActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHandler.addDailyIntake(mealType, mealName, calories, protein, carbs, macros);


                //add toast and set empty strings
                Toast.makeText(DailyIntakeActivity.this, "Daily intake has been added", Toast.LENGTH_LONG).show();
                mealNameEt.setText("");
                caloriesEt.setText("");
                proteinEt.setText("");
                carbsEt.setText("");
                macrosEt.setText("");

                //read intake from DB and push the data into the firebase
                intakeData = dbHandler.readIntake();

                //push the data into firebase
//                if (isOnLine() && intakeData.size()>0) {
//                    dbHandler.pushFirebase(intakeData);
//                }
                dbHandler.pushFirebase(intakeData);
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
    };

}