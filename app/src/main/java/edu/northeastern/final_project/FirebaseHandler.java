package edu.northeastern.final_project;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseHandler {

    private DBHandler dbHandler;
    private ArrayList<Intake> intakeData;

    // read data from sqlite
    intakeData = readIntake();
    //update sqlite if data was pushed
    if (isOnLine() && intakeData.size()>0) {
            pushFirebase(intakeData);
    }

    // check whether the device is online
    public boolean isOnLine() {

        ConnectivityManager connectivityManager = (ConnectivityManager) Context.getSystemService(DailyIntakeActivity.this);

        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                                                               @Override
                                                               public void onAvailable(Network network) {
                                                                   Variables.isNetworkConnected = true; // Global Static Variable
                                                                   Log.d("Network Connection", "success");
                                                               }

            @Override
                                                               public void onLost(Network network) {
                                                                   Variables.isNetworkConnected = false; // Global Static Variable
                                                                   Log.d("Network Connection", "fail");
                                                               }
                                                           }

        );

        if (Variables.isNetworkConnected) {
            return true;
        } else {
            return false;
        }
    }


    //read data from database
    public ArrayList<Intake> readIntake() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorIntake = db.rawQuery("SELECT * FROM appdb", null);

        ArrayList<Intake> intakeArrayList = new ArrayList<>();

        if (cursorIntake.moveToFirst()) {
            do {
                intakeArrayList.add (new Intake(cursorIntake.getString(1),
                        cursorIntake.getString(2),
                        cursorIntake.getString(3),
                        cursorIntake.getString(4),
                        cursorIntake.getString(5),
                        cursorIntake.getString(6))) ;

            } while (cursorIntake.moveToNext());
        }

        cursorIntake.close();
        return intakeArrayList;
    }

    //push intake into firebase - sync the data
    public void pushFirebase(ArrayList<Intake> dataList) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DailyIntake").child("DETAILS");
        for(Intake d : dataList){
            ref.push().setValue(d);
        }
    }

}


