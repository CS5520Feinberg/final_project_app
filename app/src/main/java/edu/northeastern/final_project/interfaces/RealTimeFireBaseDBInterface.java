package edu.northeastern.final_project.interfaces;


import com.google.firebase.database.FirebaseDatabase;

public interface RealTimeFireBaseDBInterface {
    FirebaseDatabase getConnection();
}
