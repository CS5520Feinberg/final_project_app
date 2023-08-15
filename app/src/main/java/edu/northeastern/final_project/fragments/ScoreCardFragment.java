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

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;


import edu.northeastern.final_project.R;
import edu.northeastern.final_project.adapter.ScoreCardAdapter;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.entity.ScoreCard;
import edu.northeastern.final_project.interfaces.ContactFetchedCallBack;

public class ScoreCardFragment extends Fragment {


    RealTimeDbConnectionService realTimeDbConnectionService;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followers_rv,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_fragment_followers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ScoreCardAdapter adapter = new ScoreCardAdapter(new ArrayList<>(),getContext());
        recyclerView.setAdapter(adapter);
        view.findViewById(R.id.imageButton_cross).setVisibility(View.INVISIBLE);
        Set<String> registered_user = new HashSet<>();
        realTimeDbConnectionService = new RealTimeDbConnectionService();

        Log.d("Inside Score Card Fragment","Fragment");
        realTimeDbConnectionService.getConnection()
                .getReference("socialmedia")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String userPhoneNumber = userSnapshot.getKey();
                                registered_user.add(userPhoneNumber);
                                Log.d("ContactAdded", "" + userPhoneNumber);
                            }
                            realTimeDbConnectionService.fetchMultipleUserData(new ArrayList<>(registered_user), new ContactFetchedCallBack() {
                                @Override
                                public void contactFetched(Contact contact) {

                                }

                                @Override
                                public void errorFetched(String errorMessage) {

                                }

                                @Override
                                public void noDataFound() {

                                }

                                @Override
                                public void onMultipleContactFetched(List<Contact> contacts) {
                                    List<ScoreCard> scoreCards = new ArrayList<>();
                                    int i = 0;
                                    CountDownLatch latch = new CountDownLatch(1);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            processContacts(contacts,scoreCards,latch,0);
                                        }
                                    }).start();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                latch.await(); // Wait until data processing is complete
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            Collections.sort(scoreCards, new Comparator<ScoreCard>() {
                                                @Override
                                                public int compare(ScoreCard o1, ScoreCard o2) {
                                                    return Integer.compare(o2.getStepCount(), o1.getStepCount());
                                                }
                                            });
                                            if(getActivity()!=null){
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Log.d("Setting adapter",scoreCards.size()+"");
                                                        adapter.setScoreCards(scoreCards);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                });
                                            }

                                        }
                                    }).start();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        return view;
    }


    private void processContacts(List<Contact> contacts, List<ScoreCard> scoreCards, CountDownLatch latch, int currentIndex) {
        if (currentIndex >= contacts.size()) {
            // All contacts processed, count down the latch
            latch.countDown();
            return;
        }

        Contact contact = contacts.get(currentIndex);
        realTimeDbConnectionService.getConnection().getReference("metaDataUid")
                .child(contact.getPhone_number()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String uid = snapshot.getValue(String.class);
                            realTimeDbConnectionService.getConnection().getReference("users")
                                    .child(uid).child("TotalSteps")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Log.d("SNAPSHOT EXISTS", "Step counts retrieved");
                                            if (snapshot.exists()) {
                                                int stepCount = snapshot.getValue(Integer.class);
                                                ScoreCard card = new ScoreCard(contact, stepCount);
                                                scoreCards.add(card);
                                            }

                                            // Process the next contact recursively
                                            processContacts(contacts, scoreCards, latch, currentIndex + 1);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Process the next contact recursively even if there's an error
                                            processContacts(contacts, scoreCards, latch, currentIndex + 1);
                                        }
                                    });
                        } else {
                            // Process the next contact recursively if metadata not found
                            processContacts(contacts, scoreCards, latch, currentIndex + 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Process the next contact recursively even if there's an error
                        processContacts(contacts, scoreCards, latch, currentIndex + 1);
                    }
                });
    }
}


