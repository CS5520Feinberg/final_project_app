package edu.northeastern.final_project.backgroundThreadClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.northeastern.final_project.MainActivity;
import edu.northeastern.final_project.ProfileActivity;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;


public class UniquePhoneNumberThread extends GenericAsyncClassThreads<Void,Void, List<Boolean>>{
    FirebaseDatabase database;
   FirebaseAuth mAuth;
    Context context;
    String phoneNumber;
    List<Boolean> flags_list;
    String email;
    String password;

    public UniquePhoneNumberThread(FirebaseDatabase database, Context context, String phoneNumber, FirebaseAuth mAuth, String email, String password) {
        this.database = database;
        this.context = context;
        this.mAuth = mAuth;
        this.phoneNumber = phoneNumber;
        this.flags_list = new ArrayList<>();
        this.email = email;
        this.password = password;
    }

    @Override
    protected List<Boolean> doInBackground(Void... voids) {

        //check social media
            CountDownLatch latch_for_child_thread = new CountDownLatch(1);
            Boolean bool= new SocialMediaCollectionCheckThread(database,latch_for_child_thread,false).doInBackground();
            try {
                latch_for_child_thread.await();
                flags_list.add(bool);
                if(bool){
                    DatabaseReference socialMediaRef = database.getReference("socialmedia").child(phoneNumber);
                    CountDownLatch latch = new CountDownLatch(1);
                    flags_list.add(true); // phonenumber already exists
                    socialMediaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                latch.countDown();
                            }else{
                                flags_list.set(1,false);
                                latch.countDown();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //could not check doc because of error

                            Log.d("Error not able to check doc",error.getMessage());
                            latch.countDown();
                        }
                    });

                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    //access the social media collection or else could not create one
                }
            }catch (InterruptedException ex){

                Log.d("Error",ex.getMessage());
            }
            return  flags_list;

    }


    @Override
    protected void onPostExecute(List<Boolean> booleans) {
        super.onPostExecute(booleans);
        if(booleans.isEmpty()){
            Toast.makeText(context,"Sorry can not access db right now",Toast.LENGTH_SHORT).show();
        }else if(booleans.size()==1 && booleans.get(0)==false){
            Toast.makeText(context,"Sorry can not access social media feature right now",Toast.LENGTH_SHORT).show();
        }else if(booleans.size()==2 && booleans.get(1) == true){
            Toast.makeText(context,"Sorry phone number already exist",Toast.LENGTH_SHORT).show();
        }else{
            Log.d("Registering user",""+email);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // User created successfully
                            FirebaseUser user = mAuth.getCurrentUser();
                            // TODO: 8/10/23 @everyone look into it 
                            //new RealTimeDbConnectionService().saveUserDataToSocialMediaDatabase("Test",phoneNumber,email);
                            Toast.makeText(context, "Sign Up Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, ProfileActivity.class);
                            context.startActivity(intent);
                        } else {
                            // Failed to create user
                            new RealTimeDbConnectionService().saveUserDataToSocialMediaDatabase("Test",phoneNumber,email);
                            Toast.makeText(context, "Account already exists", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
