package edu.northeastern.final_project.dbConnectionHelpers;

import com.google.firebase.database.FirebaseDatabase;

import edu.northeastern.final_project.interfaces.RealTimeFireBaseDBInterface;

public class RealTimeDbConnectionService implements RealTimeFireBaseDBInterface {

    private  FirebaseDatabase database;
    @Override
    public  FirebaseDatabase getConnection() {
        database = FirebaseDatabase.getInstance();
        return database;
    }
}
