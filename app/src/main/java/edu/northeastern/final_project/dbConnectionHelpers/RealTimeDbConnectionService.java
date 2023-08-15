package edu.northeastern.final_project.dbConnectionHelpers;

import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import edu.northeastern.final_project.Constants;
import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.interfaces.ContactFetchListener;
import edu.northeastern.final_project.interfaces.ContactFetchedCallBack;
import edu.northeastern.final_project.interfaces.PhoneNumberFetchedCallback;
import edu.northeastern.final_project.interfaces.RealTimeFireBaseDBInterface;
import edu.northeastern.final_project.interfaces.UserDataFetchedCallback;

public class RealTimeDbConnectionService implements RealTimeFireBaseDBInterface {

    private FirebaseDatabase database;

    @Override
    public FirebaseDatabase getConnection() {
        database = FirebaseDatabase.getInstance();
        return database;
    }

    //apply callback
    public void getRegisteredContacts(CountDownLatch latch, Set<String> registered_user) {
        FirebaseDatabase dbConnection = getConnection();
        DatabaseReference userRef = dbConnection.getReference("socialmedia");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String userPhoneNumber = userSnapshot.getKey();
                        registered_user.add(userPhoneNumber);
                        Log.d("ContactAdded", "" + userPhoneNumber);
                    }
                    latch.countDown();

                } else {
                    Log.d("No data", "No Data Fetched");
                    latch.countDown();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error fetching users: " + error.getMessage());
                latch.countDown();

            }
        });
        try {
            latch.await();
            return;

        } catch (InterruptedException ex) {
            Log.d("Error", ex.getMessage());
        }
    }




    public void saveUserDataToSocialMediaDatabase(String uid, String name, String phoneNumber, String email) {
        saveMetaDataUidAndPhoneNumberLink(uid, name, phoneNumber, email);
        DatabaseReference usersRef = getConnection().getReference("socialmedia");
        DatabaseReference userRef = usersRef.child(phoneNumber);


        Contact contact = new Contact(name, phoneNumber, email);

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

        DatabaseReference userRefUid = getConnection().getReference("metaDataUid").child(phoneNumber);
        userRefUid.setValue(uid).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("SaveUserMetaData", "User data saved to database");
            } else {
                Log.e("SaveUserMetaData", "Failed to save user data: " + task.getException());
            }
        });

    }


    public void getUserProfileData(UserDataFetchedCallback userDataFetchedCallback) {
        String currentUid = new Constants().getUid();
        getPhoneNumberFromDatabase(currentUid, new PhoneNumberFetchedCallback() {
            @Override
            public void onPhoneNumberFetched(String phone_number) {
                Log.d("Phone_number", "" + phone_number);
                DatabaseReference socialusersRef = getConnection().getReference("socialmedia")
                        .child(phone_number);


                socialusersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Log.d("Data", "User data exists");
                            Contact data = snapshot.getValue(Contact.class);
                            Log.d("Data", "" + data.getName());
                            Log.d("Data", "" + data.getFollower());
                            Log.d("Data", "" + data.getFollowing());

                            userDataFetchedCallback.onSuccess(data);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userDataFetchedCallback.onError(error.getMessage());
                    }
                });
            }

            @Override
            public void onError(Exception ex) {
                userDataFetchedCallback.onError(ex.getMessage());
            }
        });


    }

    public void getPhoneNumberFromDatabase(String uid, PhoneNumberFetchedCallback callback) {
        Log.d("Metadata Call", "Calling metadata for user profile");
        DatabaseReference metadataRef = getConnection().getReference("metaData").child(uid);


        metadataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.onPhoneNumberFetched(snapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void fetchMultipleUserData(List<String> phone_number_list, ContactFetchedCallBack contactFetchedCallBack){
        List<Contact> data_of_phone_numbers = new ArrayList<>();
        fetchRecursive(data_of_phone_numbers,0,phone_number_list,contactFetchedCallBack);
    }

    private void fetchRecursive(List<Contact> data_of_phone_numbers, int index, List<String> phone_number_list, ContactFetchedCallBack contactFetchedCallBack) {
        if(index >= phone_number_list.size()){
            contactFetchedCallBack.onMultipleContactFetched(data_of_phone_numbers);
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.d("Main Thread",getClass().getName());
            // Code is running on the main thread
            // You can safely update UI components here
        } else {
            Log.d("Background Thread",getClass().getName());
            // Code is running on a background thread
            // You should not update UI components directly from here
        }
        DatabaseReference dbReference =  getConnection().getReference("socialmedia").child(phone_number_list.get(index));
        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   Contact contact = snapshot.getValue(Contact.class);
                   data_of_phone_numbers.add(contact);
               }
               fetchRecursive(data_of_phone_numbers,index+1,phone_number_list,contactFetchedCallBack);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                contactFetchedCallBack.errorFetched(error.getMessage());
            }
        });

    }

}
