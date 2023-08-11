package edu.northeastern.final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private static final String FLAG = "IsCouldSynced";
    private static final String STEP_TABLE_NAME = "step_table";
    private static final String STEPS = "steps";
    private static final String TABLE_NAME_GOAL = "weekly_daily_goal";
    private static final String ID_COL_GOAL = "id";
    private static final String GOAL = "goal";
    private static String targetUserId = null;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public DBHandler(Context context, String userId) {
        super (context, DB_NAME + "_" + userId, null, DB_VERSION);
        targetUserId = userId;

        // Syncing steps in thread
        syncStepsFirebase stepSync = new syncStepsFirebase();
        Thread stepThread = new Thread(stepSync);
        stepThread.start();

        // Syncing intake in thread
        syncIntakeFirebase intakeSync = new syncIntakeFirebase();
        Thread intakeThread = new Thread(intakeSync);
        intakeThread.start();
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
                + MODIFIED_TIME + " TEXT, "
                + FLAG + " TEXT)";

        Log.d("Table create SQL",  "CREATE_DAILYINTAKE_TABLE");

        db.execSQL(query);

        String query_steps = "CREATE TABLE " + STEP_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STEPS + " TEXT, "
                + MODIFIED_TIME + " TEXT)";
        Log.d("Table create SQL",  "CREATE_STEP_TABLE");
        db.execSQL(query_steps);

        String queryGoal = "CREATE TABLE " + TABLE_NAME_GOAL + " ("
                + GOAL + " INTEGER)";
        Log.d("Table create SQL",  "CREATE_GOAL_TABLE");

        db.execSQL(queryGoal);

        ContentValues initialValues = new ContentValues();
        initialValues.put(GOAL, 0);
        db.insert(TABLE_NAME_GOAL, null, initialValues);

        Log.d("DB creation", "DB was created");
    }

    public Integer readWeeklyDailyGoal() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorGoal = db.rawQuery("SELECT * FROM " + TABLE_NAME_GOAL, null);

        Integer goal = null;

        if (cursorGoal.moveToFirst()) {
            goal = cursorGoal.getInt(0);
        }
        cursorGoal.close();
        return goal;
    }

    public void updateWeeklyGoal(int goal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(GOAL, goal);

        db.delete(TABLE_NAME_GOAL, null, null);

        db.insert(TABLE_NAME_GOAL, null, values);
        db.close();
    }

    //add new daily intake to sqlite db
    public void addDailyIntake (String mealType, String mealName, String calories, String protein, String carbs, String fats) {
        SQLiteDatabase db = this.getWritableDatabase();

        //create a variable for content values
        ContentValues values = new ContentValues();

        // timestamp
        ZonedDateTime gmt = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = gmt.format(formatter);

        Intake addedIntake = new Intake(mealType, mealName, calories, protein, carbs, fats, formattedTime);
        writeCustomIntake(addedIntake);
        uploadIntakeFirebase();
    }

    private void writeCustomIntake(Intake addIntake) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MEAL_TYPE, addIntake.mealType);
        values.put(MEAL_NAME, addIntake.mealName);
        values.put(CALORIES, addIntake.calories);
        values.put(PROTEIN, addIntake.protein);
        values.put(CARBS, addIntake.carbs);
        values.put(FATS, addIntake.fats);
        values.put(MODIFIED_TIME, addIntake.timestamp);

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
            totalCalories += Float.parseFloat(intake.calories);
            totalProtein += Float.parseFloat(intake.protein);
            totalCarbs += Float.parseFloat(intake.carbs);
            totalFats += Float.parseFloat(intake.fats);
        }

        HashMap<String, Float> macrosMap = new HashMap<>();
        macrosMap.put("calories", totalCalories);
        macrosMap.put("protein", totalProtein);
        macrosMap.put("carbs", totalCarbs);
        macrosMap.put("fats", totalFats);

        return macrosMap;
    }

    public ArrayList<Float> getWeeklyCalories() {
        ArrayList<Float> weeklyCalories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        for (int i = 6; i >= 0; i--) {
            ZonedDateTime zdt = ZonedDateTime.now(ZoneOffset.UTC).minusDays(i);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String targetDate = zdt.format(dateFormatter);

            String query = "SELECT " + CALORIES + " FROM " + TABLE_NAME + " WHERE SUBSTR(" + MODIFIED_TIME + ", 1, 10) = '" + targetDate + "'";

            Cursor cursor = db.rawQuery(query, null);
            float totalCalories = 0;

            if (cursor.moveToFirst()) {
                do {
                    totalCalories += Float.parseFloat(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();

            weeklyCalories.add(totalCalories);
        }

        return weeklyCalories;
    }


    // add steps to sqlite db
    public void addSteps (int numSteps) {
        ZonedDateTime gmt = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = gmt.format(formatter);
        Log.d("dbHandler", "Writing " + numSteps + " steps to DB at " + formattedTime);

        writeCustomSteps(formattedTime, numSteps);
        uploadStepsFirebase();
    }

    private void writeCustomSteps(String formattedTime, int numSteps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

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

    class syncIntakeFirebase implements Runnable {
        @Override
        public void run() {
            Log.d("syncIntakeFirebase", "Starting");
            ValueEventListener stepListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("syncIntakeFirebase", "Received Data");
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Intake iterIntake = snapshot.getValue(Intake.class);
                        // Log.d("syncStepsFirebase", "Time: " + iterIntake.timestamp + " Name: " + iterIntake.mealName);
                        writeCustomIntake(iterIntake);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    return;
                }
            };
            mDatabase.child("users").child(targetUserId).child("DailyIntake").child("DETAILS").addListenerForSingleValueEvent(stepListener);
        }
    }
    public void uploadIntakeFirebase() {
        ArrayList<Intake> intakeDataList = readIntake();

        for (Intake intakeData : intakeDataList) {
            mDatabase.child("users").child(targetUserId).child("DailyIntake").child("DETAILS").child(intakeData.timestamp).setValue(intakeData);
        }
    }

    class syncStepsFirebase implements Runnable {
        @Override
        public void run() {
            Log.d("syncStepsFirebase", "Starting");
            ValueEventListener stepListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("syncStepsFirebase", "Received Data");
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String time = snapshot.getKey();
                        long rawSteps = (long) snapshot.getValue();
                        int steps = Math.toIntExact(rawSteps);
                        // Log.d("syncStepsFirebase", "Time: " + time + " Steps: " + steps);
                        writeCustomSteps(time, steps);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    return;
                }
            };
            mDatabase.child("users").child(targetUserId).child("Steps").addListenerForSingleValueEvent(stepListener);
        }
    }

    public void uploadStepsFirebase() {
        HashMap<String, Integer> allSteps = readSteps();

        for (Map.Entry<String, Integer> allStepsEntry : allSteps.entrySet()) {
            String date = allStepsEntry.getKey();
            int curSteps = allStepsEntry.getValue();
            mDatabase.child("users").child(targetUserId).child("Steps").child(date).setValue(curSteps);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
