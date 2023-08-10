package edu.northeastern.final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DBHandler extends SQLiteOpenHelper{

    private static final String DB_NAME = "appdb";
    private static final String TABLE_NAME = "daily_intake";
    private static final int DB_VERSION = 1;
    private static final String ID_COL = "id";
    private static final String MEAL_TYPE = "meal_type";
    private static final String MEAL_NAME = "meal_name";
    private static final String CALORIES = "calories";
    private static final String PROTEIN = "protein";
    private static final String CARBS = "carbs";
    private static final String FATS = "fats";
    private static final String MODIFIED_TIME = "modified_time";
    private static final String STEP_TABLE_NAME = "step_table";
    private static final String STEPS = "steps";

    public DBHandler(Context context) {
        super (context, DB_NAME, null, DB_VERSION);
    }

    // create a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create a query for data
        String query = "CREATE TABLE " + TABLE_NAME+ " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MEAL_TYPE + " TEXT, "
                + MEAL_NAME + " TEXT, "
                + CALORIES + " TEXT, "
                + PROTEIN + " TEXT, "
                + CARBS + " TEXT, "
                + FATS + " TEXT, "
                + MODIFIED_TIME + " TEXT)";

        Log.d("Table create SQL",  "CREATE_DAILYINTAKE_TABLE");

        db.execSQL(query);

        String query_steps = "CREATE TABLE " + STEP_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STEPS + " TEXT, "
                + MODIFIED_TIME + " TEXT)";
        Log.d("Table create SQL",  "CREATE_STEP_TABLE");
        db.execSQL(query_steps);

        Log.d("DB creation", "DB was created");
    }

    //add new daily intake to sqlite db
    public void addDailyIntake (String mealType, String mealName, String calories, String protein, String carbs, String macros) {
        SQLiteDatabase db = this.getWritableDatabase();

        //create a variable for content values
        ContentValues values = new ContentValues();

        // timestamp
        ZonedDateTime gmt = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = gmt.format(formatter);


        values.put(MEAL_TYPE, mealType);
        values.put(MEAL_NAME, mealName);
        values.put(CALORIES, calories);
        values.put(PROTEIN, protein);
        values.put(CARBS, carbs);
        values.put(FATS, macros);
        values.put(MODIFIED_TIME, formattedTime);

        /*** TODO: how to do time? for modified_time column***/

        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    //read data from sqlite
    public ArrayList<Intake> readIntake() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorIntake = db.rawQuery("SELECT * FROM "+ TABLE_NAME, null);

        ArrayList<Intake> intakeArrayList = new ArrayList<>();

        if (cursorIntake.moveToFirst()) {
            do {
                    intakeArrayList.add (new Intake(cursorIntake.getString(1),
                            cursorIntake.getString(2),
                            cursorIntake.getString(3),
                            cursorIntake.getString(4),
                            cursorIntake.getString(5),
                            cursorIntake.getString(6),
                            cursorIntake.getString(7))) ;

                } while (cursorIntake.moveToNext());
        }

        cursorIntake.close();
        return intakeArrayList;
    }

    //read daily intake data
    public ArrayList<Intake> readDailyIntake() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorIntake = db.rawQuery("SELECT * FROM "+ TABLE_NAME, null);

        ArrayList<Intake> intakeArrayList = new ArrayList<>();

        ZonedDateTime currentGmt = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String currentDate = currentGmt.format(dateFormatter);

        if (cursorIntake.moveToFirst()) {
            do {
                String timestampString = cursorIntake.getString(7);
                String datePart = timestampString.split(" ")[0];

                if (currentDate.equals(datePart)) {
                    intakeArrayList.add(new Intake(cursorIntake.getString(1),
                            cursorIntake.getString(2),
                            cursorIntake.getString(3),
                            cursorIntake.getString(4),
                            cursorIntake.getString(5),
                            cursorIntake.getString(6),
                            cursorIntake.getString(7)));
                }
            } while (cursorIntake.moveToNext());
        }
        return intakeArrayList;
    }

    public HashMap<String, Float> getDailyMacros(ArrayList<Intake> dailyIntake) {
        float totalCalories = 0;
        float totalProtein = 0;
        float totalCarbs = 0;
        float totalFats = 0;

        for (Intake intake : dailyIntake) {
            totalCalories += intake.getCal();
            totalProtein += intake.getProtein();
            totalCarbs += intake.getCarbs();
            totalFats += intake.getFats();
        }

        HashMap<String, Float> macrosMap = new HashMap<>();
        macrosMap.put("calories", totalCalories);
        macrosMap.put("protein", totalProtein);
        macrosMap.put("carbs", totalCarbs);
        macrosMap.put("fats", totalFats);

        return macrosMap;
    }

    // add steps to sqlite db
    public void addSteps (int numSteps) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        ZonedDateTime gmt = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = gmt.format(formatter);
        Log.d("dbHandler", "Writing " + numSteps + " steps to DB at " + formattedTime);

        values.put(STEPS, numSteps);
        values.put(MODIFIED_TIME, formattedTime);

        db.insert(STEP_TABLE_NAME, null, values);
        db.close();
    }

    public HashMap<String, Integer> readSteps() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorIntake = db.rawQuery("SELECT * FROM "+ STEP_TABLE_NAME, null);
        HashMap<String, Integer> steps = new HashMap<>();

        if (cursorIntake.moveToFirst()) {
            do {
                int curSteps = cursorIntake.getInt(1);
                String timestampString = cursorIntake.getString(2);

                steps.put(timestampString, curSteps);
            } while (cursorIntake.moveToNext());
        }
        return steps;
    }

    public HashMap<String, Integer> getDailySteps() {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, Integer> dailySteps = new HashMap<>();
        HashMap<String, Integer> allSteps = readSteps();

        for (Map.Entry<String, Integer> allStepsEntry : allSteps.entrySet()) {
            String date = allStepsEntry.getKey();
            int curSteps = allStepsEntry.getValue();
            // Log.d("getDailySteps", "DB Steps: (" + date + ": " + curSteps + ")");
            String datePart = date.split(" ")[0];

            if (dailySteps.containsKey(datePart)) {
                int daySteps = dailySteps.get(datePart);
                Log.d("getDailySteps", datePart + " Loaded: " + daySteps);
                daySteps += curSteps;
                Log.d("getDailySteps", datePart + " Updated: " + daySteps);
                dailySteps.put(datePart, daySteps);
            } else {
                dailySteps.put(datePart, curSteps);
            }

        }

        return dailySteps;
    }

    //push intake into firebase
    public void pushFirebase(ArrayList<Intake> dataList) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DailyIntake").child("DETAILS");
        for(Intake d : dataList){
            ref.push().setValue(d);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
