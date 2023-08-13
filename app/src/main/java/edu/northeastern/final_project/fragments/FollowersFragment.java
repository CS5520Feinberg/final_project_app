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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.northeastern.final_project.R;
import edu.northeastern.final_project.adapter.ContactsAdapter;
import edu.northeastern.final_project.backgroundThreadClass.GetContactSocialData;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.interfaces.ContactFetchedCallBack;
import edu.northeastern.final_project.interfaces.UserDataFetchedCallback;
import edu.northeastern.final_project.viewHolder.ContactsViewHolder;

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
        test.add(new Contact("Test","9810145604","garg.com"));
        followersAdapter = new ContactsAdapter(test,getContext(),"Followers");
        followers_Rv.setAdapter(followersAdapter);
        new GetContactSocialData(followersAdapter).execute();
        Log.d("FollowersFragment", "Starting thread to fetch follower data...");
        //fetchFollowerData();
//        //get array list
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
//                    @Override
//                    public void onSuccess(Contact contact) {
//                        Log.d("User Data", "Fetched user data successfully" + contact.getFollower());
//                        List<String> followers = contact.getFollower();
//                        if (followers != null && followers.size() > 0) {
//                            List<Contact> followers_contact_data = new ArrayList<>();
//                           // CountDownLatch latch = new CountDownLatch(followers.size());
//                            Log.d("Starting threads", "followers data from db");
//                            CountDownLatch latch_2 = new CountDownLatch(1);
//
//                            Log.d("Thread" + getContext(), "Inside inner thread");
//                            for (String contact_number : followers) {
//                                Log.d("Fetching Data for", contact_number);
//
//
//                                new RealTimeDbConnectionService().fetchContactDetails(latch_2,contact_number, new ContactFetchedCallBack() {
//                                    @Override
//                                    public void contactFetched(Contact contact) {
//                                        followers_contact_data.add(contact);
//                                        Log.d("Fetched Data", contact_number);
//                                     //   latch.countDown();
//                                    }
//
//                                    @Override
//                                    public void errorFetched(String errorMessage) {
//                                        Log.d("Error", errorMessage);
//                                    }
//
//                                    @Override
//                                    public void noDataFound() {
//                                        Log.d("404","NO DATA FOUND");
//                                    }
//                                });
//
//                            }
//
//                         //   Log.d("Thread-completed", followers_contact_data.get(0).getName());
//
//                            try {
//                                latch_2.await();
//                                Log.d("Fetched Data", "Now setting Rv");
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        followersAdapter.setContacts(followers_contact_data);
//                                        followersAdapter.notifyDataSetChanged();
//                                    }
//                                });
//
//                            } catch (InterruptedException ex) {
//                                Log.d("Error", ex.getMessage());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(String message) {
//                        Log.d("Error", message);
//                    }
//                });
//            }
//        }).start();
        return view;
    }

    private void fetchFollowerData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
                    @Override
                    public void onSuccess(Contact contact) {
                        List<String> followers = contact.getFollower();
                        if (followers != null && !followers.isEmpty()) {
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
        List<Contact> followers_contact_data = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(followers.size());

        for (String contact_number : followers) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new RealTimeDbConnectionService().fetchContactDetails(latch, contact_number, new ContactFetchedCallBack() {
                        @Override
                        public void contactFetched(Contact contact) {
                            synchronized (followers_contact_data) {
                                followers_contact_data.add(contact);
                            }
                            latch.countDown();
                        }

                        @Override
                        public void errorFetched(String errorMessage) {
                            latch.countDown();
                        }

                        @Override
                        public void noDataFound() {
                            latch.countDown();
                        }
                    });
                }
            }).start();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                    // Update UI with followers_contact_data (on the main thread)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            followersAdapter.setContacts(followers_contact_data);
                            followersAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (InterruptedException ex) {
                    // Handle exception
                    Log.d("Error", ex.getMessage());
                }
            }
        }).start();
    }


}
