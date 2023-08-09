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
import java.util.List;

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

    // Test
    private static final String TABLE_NAME_GOAL = "weekly_daily_goal";
    private static final String ID_COL_GOAL = "id";
    private static final String GOAL = "goal";

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
                + MODIFIED_TIME + " TEXT)" ;

        String queryGoal = "CREATE TABLE " + TABLE_NAME_GOAL + " ("
                + GOAL + " INTEGER)";

        Log.d("Table create SQL",  "CREATE_DAILYINTAKE_TABLE");

        db.execSQL(query);
        db.execSQL(queryGoal);

        ContentValues initialValues = new ContentValues();
        initialValues.put(GOAL, 0);
        db.insert(TABLE_NAME_GOAL, null, initialValues);

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

    //add weekly daily goal to sqlite db
    public void updateWeeklyGoal(int goal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(GOAL, goal);

        db.delete(TABLE_NAME_GOAL, null, null);

        db.insert(TABLE_NAME_GOAL, null, values);
        db.close();
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
