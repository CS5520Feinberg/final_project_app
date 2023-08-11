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

import edu.northeastern.final_project.Constants;
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
        DatabaseReference userRef = dbConnection.getReference("socialmedia");
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
        Contact[] contacts = {null};
        FirebaseDatabase dbConnection = getConnection();
        DatabaseReference userRef = dbConnection.getReference("socialmedia").child(search_input);
        CountDownLatch latch = new CountDownLatch(1);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.d("Snapshot-key",snapshot.getKey());
                     contacts[0] = snapshot.getValue(Contact.class);
                     latch.countDown();
                }else{
                    latch.countDown();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error",error.getMessage());
                latch.countDown();
            }
        });
        try{
            latch.await();
            Log.d("Complete Call","");
        }catch (InterruptedException ex){
            Log.d("Error","");
        }
        return contacts[0];
    }

    public void saveUserDataToSocialMediaDatabase(String uid,String name, String phoneNumber,String email) {
        saveMetaDataUidAndPhoneNumberLink(uid,name,phoneNumber,email);
        DatabaseReference usersRef = getConnection().getReference("socialmedia");
        DatabaseReference userRef = usersRef.child(phoneNumber);


        Contact contact = new Contact(name,phoneNumber,email);

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

    private void saveMetaDataUidAndPhoneNumberLink(String uid, String name, String phoneNumber, String email) {
        DatabaseReference usersRef = getConnection().getReference("metaData");
        DatabaseReference userRef = usersRef.child(new Constants().getUid());
        userRef.setValue(phoneNumber).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("SaveUserMetaData", "User data saved to database");
            } else {
                Log.e("SaveUserMetaData", "Failed to save user data: " + task.getException());
            }
        });
    }


    public Contact getUserProfileData() {
        String currentUid = new Constants().getUid();
        String phoneNumber = getPhoneNumberFromDatabase(currentUid);
        if(phoneNumber==null || phoneNumber.isEmpty()){
            return null;
        }else{
            final Contact[] profileData = {null};
            Log.d("Phone_number", ""+phoneNumber);
            DatabaseReference socialusersRef = getConnection().getReference("socialmedia")
                    .child(phoneNumber);
            CountDownLatch latch = new CountDownLatch(1);

            socialusersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Log.d("Data","User data exists");
                        Contact data = snapshot.getValue(Contact.class);
                        profileData[0] = data;
                        latch.countDown();
                    }else{
                        latch.countDown();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    latch.countDown();
                }
            });
            try{
                latch.await();
            }catch (InterruptedException ex){
                Log.d("ERRO",ex.getMessage());
            }
            return profileData[0];
        }

    }

    public String getPhoneNumberFromDatabase(String uid) {
        Log.d("Metadata Call","Calling metadata for user profile");
        DatabaseReference metadataRef = getConnection().getReference("metaData").child(uid);
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] phone_number = {null};

        metadataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("Phone number ",""+snapshot.getValue(String.class));
                    phone_number[0] = snapshot.getValue(String.class);
                }
                latch.countDown(); // Signal that the operation is complete
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                latch.countDown(); // Signal that the operation is complete, even if it failed
            }
        });

        try {
            latch.await(); // Wait for the operation to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return phone_number[0];
    }


}
