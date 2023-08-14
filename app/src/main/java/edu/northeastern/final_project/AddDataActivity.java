package edu.northeastern.final_project;

import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AddDataActivity extends AppCompatActivity {
    String TABLE_NAME = "daily_intake";
    String csvFile = "nutrition_data.csv";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DBHandler dbHandler = new DBHandler(AddDataActivity.this, mAuth.getUid());
    String ID_COL = "id";
    String MEAL_TYPE = "meal_type";
    String MEAL_NAME = "meal_name";
    String CALORIES = "calories";
    String PROTEINS = "proteins";
    String CARBS = "carbs";
    String FATS = "fats";
    String MODIFIED_TIME = "modified_time";
    String FLAG = "IsCloudSynced";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        SQLiteDatabase db = dbHandler.getWritableDatabase();

        String drop_query = "DROP TABLE IF EXISTS " + TABLE_NAME;

        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MEAL_TYPE + " TEXT, "
                + MEAL_NAME + " TEXT, "
                + CALORIES + " TEXT, "
                + PROTEINS + " TEXT, "
                + CARBS + " TEXT, "
                + FATS + " TEXT, "
                + MODIFIED_TIME + " TEXT,"
                + FLAG + " TEXT)";

        db.execSQL(drop_query);
        db.execSQL(query);

        Log.d("DB", "new db was created");

        String csvFile = "nutrition_data.csv";
        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(csvFile);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: Load nutrition_data failed", Toast.LENGTH_SHORT).show();
        }

        Log.d("Tag", "Load csv file");

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String columns = "id, meal_type, meal_name, calories, proteins, carbs, fats, modified_time, IsCloudSynced";
        String str1 = "INSERT INTO " + TABLE_NAME + " (" + columns + ") values(";
        String str2 = ")";
        String str3 = ";";

        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {

                String[] str = line.split(",");
                StringBuilder sb = new StringBuilder(str1);
                sb.append("'" + str[0] + "',");
                sb.append("'" + str[1] + "',");
                sb.append("'" + str[2] + "',");
                sb.append("'" + str[3] + "',");
                sb.append("'" + str[4] + "',");
                sb.append("'" + str[5] + "',");
                sb.append("'" + str[6] + "',");
                sb.append("'" + str[7] + "',");
                sb.append("'" + str[8] + "'");
                sb.append(str2);
                db.execSQL(sb.toString());
                sb.append(str3);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: Load csv data to sqlite failed", Toast.LENGTH_SHORT).show();
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        Log.d("Tag", "Load csv file to sqlite");
    }
}