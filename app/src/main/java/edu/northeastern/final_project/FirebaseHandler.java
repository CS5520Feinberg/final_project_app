package edu.northeastern.final_project;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseHandler {

    private DBHandler dbHandler;
    private ArrayList<Intake> intakeData;

    //check whether the device is online
    public boolean isOnLine() {

        ConnectivityManager connectivityManager = (ConnectivityManager) Context.getSystemService(DailyIntakeActivity.this);

        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
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

        //if the sqlite updates, push the data into firebase after the click button on add data intake
        // read data from sqlite
        dbHandler = new DBHandler(DailyIntakeActivity.this);
        intakeData = dbHandler.readIntake();

        //update sqlite if data was pushed
        pushFirebase(intakeData);

        //

    }

    //push intake into firebase - sync the data
    public void pushFirebase(ArrayList<Intake> dataList) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DailyIntake").child("DETAILS");
        for(Intake d : dataList){
            ref.push().setValue(d);
        }
    }

}


