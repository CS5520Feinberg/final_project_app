package edu.northeastern.final_project.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


import edu.northeastern.final_project.R;
import edu.northeastern.final_project.adapter.ContactsAdapter;

import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.interfaces.ContactFetchListener;

import edu.northeastern.final_project.interfaces.UserDataFetchedCallback;


public class FollowersFragment extends Fragment {
    RecyclerView followers_Rv;
    ContactsAdapter followersAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followers_rv, container, false);
        followers_Rv = view.findViewById(R.id.recycler_view_fragment_followers);
        followers_Rv.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Contact> test = new ArrayList<>();

        followersAdapter = new ContactsAdapter(test,getContext(),"Followers");
        followers_Rv.setAdapter(followersAdapter);

        Log.d("FollowersFragment", "Starting thread to fetch follower data...");
        fetchFollowerData();


        return view;
    }

    private void fetchFollowerData() {
        Log.d("Fetch_follower_data","Starting");
        new Thread(new Runnable() {
            @Override
            public void run() {
                new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
                    @Override
                    public void onSuccess(Contact contact) {
                        List<String> followers = contact.getFollower();
                        if (followers != null && !followers.isEmpty()) {
                            Log.d("Fetching contact details"," "+followers.size());
                            fetchContactsForFollowers(followers);
                        } else {
                            // Handle case when no followers
                        }
                    }

                    @Override
                    public void onError(String message) {
                        // Handle error
                    }
                });
            }
        }).start();
    }
    private void fetchContactsForFollowers(List<String> followers) {

        List<Contact> followers_contact_data = new CopyOnWriteArrayList<>();

            for (String contact_number : followers) {

                        fetchContactDetails(contact_number, new ContactFetchListener() {
                            @Override
                            public void onContactFetched(Contact contact) {
                                Log.d("Got data",contact.toString());
                                synchronized (followers_contact_data) {
                                    followers_contact_data.add(contact);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("Setting Adapter", "" + followers_contact_data);
                                            followersAdapter.setContacts(followers_contact_data);
                                            followersAdapter.notifyDataSetChanged();
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onError(String errorMessage) {

                            }
                            });


        }
            Log.d("Data"," "+followers_contact_data.size());
//            synchronized (followersAdapter) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("Setting Adapter", "" + followers_contact_data);
//                        followersAdapter.setContacts(followers_contact_data);
//                        followersAdapter.notifyDataSetChanged();
//                    }
//                });
//            }



    }

    protected void fetchContactDetails(String searchInput, ContactFetchListener listener) {
        FirebaseDatabase dbConnection = new RealTimeDbConnectionService().getConnection();
        DatabaseReference userRef = dbConnection.getReference("socialmedia").child(searchInput);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Contact contact = snapshot.getValue(Contact.class);
                    listener.onContactFetched(contact);
                } else {
                    listener.onError("Contact not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.getMessage());
            }
        });
    }

}
