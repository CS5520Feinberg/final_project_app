package edu.northeastern.final_project.backgroundThreadClass;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import edu.northeastern.final_project.R;
import edu.northeastern.final_project.activity.SocialMediaActivity;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.interfaces.UserDataFetchedCallback;

public class GetUserSocialDataThread extends GenericAsyncClassThreads<Void, Void, Void> {
    Context context;
    TextView profileName;
    TextView followers_number;
    TextView following_number;
    ImageView imageView;


    public GetUserSocialDataThread(SocialMediaActivity context, TextView profileName, TextView following_number, TextView followers_number, ImageView imageView) {
        this.context = context;
        this.profileName = profileName;
        this.following_number = following_number;
        this.followers_number = followers_number;
        this.imageView = imageView;

    }

    @Override
    protected Void doInBackground(Void... voids) {


        new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
            @Override
            public void onSuccess(Contact contact) {
                ((SocialMediaActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profileName.setText(contact.getName());
                        if (contact.getImage_uri() == null) {
                            imageView.setImageResource(R.drawable.nophotoimage);
                        } else {
                            new DownloadImageThread(contact.getImage_uri(), imageView).execute();
                        }
                        DatabaseReference followingRef = new RealTimeDbConnectionService().getConnection()
                                .getReference("socialmedia").child("" + contact.getPhone_number()).child("following");
                        followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Log.d("List", snapshot.getKey());
                                    GenericTypeIndicator<List<String>> typeIndicator = new GenericTypeIndicator<List<String>>() {
                                    };
                                    following_number.setText("" + snapshot.getValue(typeIndicator).size());
                                } else {
                                    following_number.setText("" + 0);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                following_number.setText("" + 0);
                            }
                        });


                        DatabaseReference followerRef = new RealTimeDbConnectionService().getConnection()
                                .getReference("socialmedia").child("" + contact.getPhone_number()).child("follower");
                        followerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Log.d("List", snapshot.getKey());

                                    GenericTypeIndicator<List<String>> typeIndicator = new GenericTypeIndicator<List<String>>() {
                                    };
                                    followers_number.setText("" + snapshot.getValue(typeIndicator).size());
                                } else {
                                    followers_number.setText("" + 0);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                followers_number.setText("" + 0);
                            }
                        });


                    }
                });


            }

            @Override
            public void onError(String message) {

            }
        });


        return null;

    }

}
