package edu.northeastern.final_project.backgroundThreadClass;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.interfaces.UserDataFetchedCallback;

public class AddFollowingDataToFirebase extends GenericAsyncClassThreads<Void,Void,Void>{
    String followed_contact_number;

    public AddFollowingDataToFirebase(String following_contact_number) {
        this.followed_contact_number = following_contact_number;
    }

    @Override
    protected Void doInBackground(Void... voids) {
            List<String> followed_friend_follower_list = new RealTimeDbConnectionService().getFollowersList(followed_contact_number);// get user data to update following list
            //get user data
            new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
                @Override
                public void onSuccess(Contact contact) {
                    doWork(contact,followed_friend_follower_list);
                }

                @Override
                public void onError(String message) {
                    Log.d("Error",message);
                }
            });


        return null;
    }
    public void doWork(Contact current_user, List<String> followed_friend_follower_list){
        List<String> following_list_user = current_user.getFollowing();
        if(following_list_user==null){
            following_list_user = new ArrayList<>();
            following_list_user.add(followed_contact_number);
        }
        if(followed_friend_follower_list==null){
            followed_friend_follower_list = new ArrayList<>();
            followed_friend_follower_list.add(current_user.getPhone_number());
        }
        DatabaseReference followedUserDbRef = new RealTimeDbConnectionService().getConnection().getReference("socialmedia").child(followed_contact_number);
        followedUserDbRef.child("follower").setValue(followed_friend_follower_list).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Data", "added successfully for following the user in followed contact");
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failure", e.getMessage());
            }
        });

        DatabaseReference current_user_df_ref = new RealTimeDbConnectionService().getConnection().getReference("socialmedia").child(current_user.getPhone_number());
        current_user_df_ref.child("following").setValue(following_list_user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("Success","updated list successfully");
                }else{
                    Log.d("Failure","not able to update list");
                }
            }
        });
    }
}
