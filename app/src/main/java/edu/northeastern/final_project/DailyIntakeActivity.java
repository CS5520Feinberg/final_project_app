package edu.northeastern.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DailyIntakeActivity extends AppCompatActivity {

    /*** TODO: maybe change into spinner - string variable has the options***/
    private EditText mealTypeEt;
    private EditText mealNameEt, caloriesEt, proteinEt, carbsEt, macrosEt;
    private Button addDailyIntakeBtn;
    private DBHandler dbHandler;


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
            }
        });
    }
}