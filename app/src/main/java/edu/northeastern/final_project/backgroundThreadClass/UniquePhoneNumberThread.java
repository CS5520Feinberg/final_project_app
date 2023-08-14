package edu.northeastern.final_project.backgroundThreadClass;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.final_project.Constants;
import edu.northeastern.final_project.ProfileActivity;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;


public class UniquePhoneNumberThread extends GenericAsyncClassThreads<Void, Void, List<Boolean>> {
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    Context context;
    String phoneNumber;
    List<Boolean> flags_list;
    String email;
    String password;
    String name;

    public UniquePhoneNumberThread(FirebaseDatabase database, Context context, String phoneNumber, FirebaseAuth mAuth, String email, String password, String name) {
        this.database = database;
        this.context = context;
        this.mAuth = mAuth;
        this.phoneNumber = phoneNumber;
        this.flags_list = new ArrayList<>();
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Override
    protected List<Boolean> doInBackground(Void... voids) {

        DatabaseReference socialMediaRef = database.getReference("socialmedia").child(phoneNumber);
        socialMediaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(context, "Sorry phone number already exist", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Registering user", "" + email);
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // User created successfully
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // TODO: 8/10/23 @everyone look into it
                                    new RealTimeDbConnectionService().saveUserDataToSocialMediaDatabase(new Constants().getUid(), name, phoneNumber, email);
                                    Intent intent = new Intent(context, ProfileActivity.class);
                                    context.startActivity(intent);

                                    Toast.makeText(context, "Sign Up Successfully!", Toast.LENGTH_SHORT).show();
//
                                } else {
                                    // Check if the error is due to an existing account
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(context, "An account with this email already exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Handle other error cases
                                        System.out.println(task.getException());
                                        Toast.makeText(context, "Account creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //could not check doc because of error
                Toast.makeText(context, "Sorry can not access db right now", Toast.LENGTH_SHORT).show();
                Log.d("Error not able to check doc", error.getMessage());

            }
        });

        //access the social media collection or else could not create one
        return flags_list;
    }


}
