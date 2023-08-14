package edu.northeastern.final_project;

import com.google.firebase.auth.FirebaseAuth;

public class Constants {
    private static FirebaseAuth firebaseAuth;

    public Constants() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public String getUid() {
        return firebaseAuth.getUid();
    }
}
