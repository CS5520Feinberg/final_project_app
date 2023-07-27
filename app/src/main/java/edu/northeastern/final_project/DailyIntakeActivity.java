package edu.northeastern.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class DailyIntakeActivity extends AppCompatActivity {

    /*** TODO: maybe change into spinner - string variable has the options***/
    private EditText mealTypeEt;
    private EditText mealNameEt, caloriesEt, proteinEt, carbsEt, macrosEt;
    private Button addDailyIntakeBtn;
    private DBHandler dbHandler;

    public ArrayList<Intake> intakeData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_intake);

        mealTypeEt = findViewById(R.id.mealTypeET);
        mealNameEt = findViewById(R.id.mealNameET);
        caloriesEt = findViewById(R.id.calories);
        proteinEt = findViewById(R.id.proteins);
        carbsEt = findViewById(R.id.carbs);
        macrosEt = findViewById(R.id.macros);
        addDailyIntakeBtn = findViewById(R.id.AddDailyBtn);

        dbHandler = new DBHandler(DailyIntakeActivity.this);

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
                if (isOnLine() && intakeData.size()>0) {
                    dbHandler.pushFirebase(intakeData);
                }
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