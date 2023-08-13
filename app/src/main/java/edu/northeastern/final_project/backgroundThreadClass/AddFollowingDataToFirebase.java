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
    List<String> followed_friend_follower_list = null;
    List<String> following_list_user = null;

    public AddFollowingDataToFirebase(String following_contact_number) {
        this.followed_contact_number = following_contact_number;
    }

    @Override
    protected Void doInBackground(Void... voids) {

           new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
               @Override
               public void onSuccess(Contact contact) {
                   followed_friend_follower_list = contact.getFollower();
                   new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
                       @Override
                       public void onSuccess(Contact contact) {
                           following_list_user = contact.getFollowing();

                           doWork(contact,following_list_user);
                       }

                       @Override
                       public void onError(String message) {
                           Log.d("Error",message);
                       }
                   });
               }

               @Override
               public void onError(String message) {

               }
           });


        return null;
    }
    public void doWork(Contact current_user, List<String> following_list_user){

        Log.d("DoWork","do work is being called");

        Log.d("Following",""+following_list_user);
        if(following_list_user==null){
            Log.d("Following_list_user",""+following_list_user);
            following_list_user = new ArrayList<>();
            following_list_user.add(followed_contact_number);
            Log.d("Following_list_user",""+following_list_user);
        }else{
            following_list_user.add(followed_contact_number);
        }
        if(followed_friend_follower_list==null){
            followed_friend_follower_list = new ArrayList<>();
            followed_friend_follower_list.add(current_user.getPhone_number());
        }else{
            followed_friend_follower_list.add(current_user.getPhone_number());
        }

        DatabaseReference followedUserDbRef = new RealTimeDbConnectionService().getConnection().getReference("socialmedia").child(followed_contact_number);
        List<String> finalFollowing_list_user = following_list_user;
        followedUserDbRef.child("follower").setValue(followed_friend_follower_list).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Data", "added successfully for following the user in followed contact");
                DatabaseReference current_user_df_ref = new RealTimeDbConnectionService().getConnection().getReference("socialmedia").child(current_user.getPhone_number());

                current_user_df_ref.child("following").setValue(finalFollowing_list_user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Success", "updated list successfully");
                        } else {
                            Log.d("Failure", "not able to update list");
                        }
                    }
                });
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failure", e.getMessage());
            }
        });


    }
}
