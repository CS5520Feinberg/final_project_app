package edu.northeastern.final_project.dbConnectionHelpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.interfaces.RealTimeFireBaseDBInterface;

public class RealTimeDbConnectionService implements RealTimeFireBaseDBInterface {

    private  FirebaseDatabase database;
    @Override
    public  FirebaseDatabase getConnection() {
        database = FirebaseDatabase.getInstance();
        return database;
    }


    public void getRegisteredContacts(CountDownLatch latch, Set<String> registered_user) {
        FirebaseDatabase dbConnection = getConnection();
        DatabaseReference userRef = dbConnection.getReference("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot userSnapshot : snapshot.getChildren() ){
                        String userPhoneNumber = userSnapshot.getKey();
                        registered_user.add(userPhoneNumber);
                        Log.d("ContactAdded",""+userPhoneNumber);
                    }


                }else{
                    Log.d("No data","No Data Fetched");
                }
                // Signal the CountDownLatch to count down
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error fetching users: " + error.getMessage());
                // Signal the CountDownLatch to count down
                latch.countDown();
            }
        });
    }

    public Contact fetchContactDetails(String search_input) {
        FirebaseDatabase dbConnection = getConnection();
        DatabaseReference userRef = dbConnection.getReference("users");
        userRef.child(search_input);
        return null;
    }

    public void saveUserDataToSocialMediaDatabase(String name, String phoneNumber,String email) {
        DatabaseReference usersRef = getConnection().getReference("socialmedia");

        DatabaseReference userRef = usersRef.child(phoneNumber);

        // Create a User object or a Map to represent user data
        Contact contact = new Contact(name,phoneNumber,email); // Customize this based on your user data structure

        // Save the user data to the database
        userRef.setValue(contact)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("SaveUserData", "User data saved to database");
                    } else {
                        Log.e("SaveUserData", "Failed to save user data: " + task.getException());
                    }
                });
    }

}
