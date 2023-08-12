package edu.northeastern.final_project.backgroundThreadClass;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.interfaces.UserDataFetchedCallback;
import edu.northeastern.final_project.viewHolder.ContactsViewHolder;

public class AddFollowingDataToFirebase extends GenericAsyncClassThreads<Void,Void,Boolean> {
    String followed_contact_number;
    RecyclerView.Adapter<ContactsViewHolder> adapter;
    List<Contact> contacts;
    int position;
    Context context;

    public AddFollowingDataToFirebase(String following_contact_number, RecyclerView.Adapter<ContactsViewHolder> adapter, List<Contact> contacts, int position, Context context) {
        this.followed_contact_number = following_contact_number;
        this.adapter = adapter;
        this.contacts =contacts;
        this.position =position;
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(Void... voids) {
        // Get user data asynchronously
        new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
            @Override
            public void onSuccess(Contact current_user) {
                if (current_user != null) {
                    updateDatabase(current_user);
                    onPostExecute(true);
                } else {
                    Log.d("Error", "User data is null");
                    onPostExecute(false);
                }
            }

            @Override
            public void onError(String message) {
                Log.d("Error", message);
                onPostExecute(false);
            }
        });

        return false;
    }

    private void updateDatabase(Contact current_user) {
        List<String> following_list_user = current_user.getFollowing();
        if (following_list_user == null) {
            following_list_user = new ArrayList<>();
            following_list_user.add(followed_contact_number);
        }

        List<String> followed_friend_follower_list = new RealTimeDbConnectionService().getFollowersList(followed_contact_number);
        if (followed_friend_follower_list == null) {
            followed_friend_follower_list = new ArrayList<>();
            followed_friend_follower_list.add(current_user.getPhone_number());
        }

        DatabaseReference followedUserDbRef = new RealTimeDbConnectionService().getConnection().getReference("socialmedia").child(followed_contact_number);
        followedUserDbRef.child("follower").setValue(followed_friend_follower_list)
                .addOnSuccessListener(unused -> Log.d("Data", "Added successfully for following the user in followed contact"))
                .addOnFailureListener(e -> Log.d("Failure", e.getMessage()));

        DatabaseReference current_user_df_ref = new RealTimeDbConnectionService().getConnection().getReference("socialmedia").child(current_user.getPhone_number());
        current_user_df_ref.child("following").setValue(following_list_user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Success", "Updated list successfully");
                    } else {
                        Log.d("Failure", "Not able to update list");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Failure", e.getMessage());
                });
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean){
            contacts.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(context,"Started Following",Toast.LENGTH_SHORT).show();
        }
    }
}


