package edu.northeastern.final_project.backgroundThreadClass;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

public class SocialMediaCollectionCheckThread extends GenericAsyncClassThreads<Void,Void,Boolean>{
    FirebaseDatabase database;
    CountDownLatch latch;

    Boolean flag;

    public SocialMediaCollectionCheckThread(FirebaseDatabase database, CountDownLatch latch, Boolean flag) {
        this.database = database;
        this.latch = latch;
        this.flag = flag;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        DatabaseReference socialMediaRef = database.getReference("socialmedia");

        socialMediaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // "socialmedia" collection doesn't exist, create it
                    socialMediaRef.setValue("").addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Created 'socialmedia' collection");
                            flag = true;
                            latch.countDown();
                            // Now you can proceed with your original logic
                        } else {
                            latch.countDown();
                            flag = false;
                            Log.d("TAG", "Failed to create 'socialmedia' collection: " + task.getException());
                        }
                    });
                } else {
                    flag = true;
                    latch.countDown();

                    // "socialmedia" collection exists, proceed with your original logic
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                flag = false;
                Log.d("TAG", "Database error: " + error.getMessage());
                latch.countDown();
            }
        });
    try{
        latch.await();

    }catch (InterruptedException ex){
        Log.d("Error",ex.getMessage());
    }
        return flag;
    }
}
