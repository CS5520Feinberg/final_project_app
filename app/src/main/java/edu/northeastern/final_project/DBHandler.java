package edu.northeastern.final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    private static final String MACROS = "macros";
    private static final String MODIFIED_TIME = "modified_time";

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
                + MACROS + " TEXT)" ;
                //+ MODIFIED_TIME + " TEXT)" ;
        db.execSQL(query);
    }

    //add new daily intake to sqlite db
    public void addDailyIntake (String mealType, String mealName, String calories, String protein, String carbs, String macros) {
        SQLiteDatabase db = this.getWritableDatabase();

        //create a variable for content values
        ContentValues values = new ContentValues();

        values.put(MEAL_TYPE, mealType);
        values.put(MEAL_NAME, mealName);
        values.put(CALORIES, calories);
        values.put(PROTEIN, protein);
        values.put(CARBS, carbs);
        values.put(MACROS, macros);
        /*** TODO: how to do time? for modified_time column***/

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
