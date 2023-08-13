package edu.northeastern.final_project.backgroundThreadClass;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import edu.northeastern.final_project.Constants;
import edu.northeastern.final_project.R;
import edu.northeastern.final_project.adapter.ContactsAdapter;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;


public class SearchPhoneNumberThread extends GenericAsyncClassThreads<Void, Void, Void> {
    Context context;
    String search_input;
    Set<String> registered_user;
    RealTimeDbConnectionService dbService;
    ContactsAdapter adapter;

    public SearchPhoneNumberThread(Context context, String search_input, ContactsAdapter adapter) {
        this.context = context;
        this.search_input = search_input;
        this.dbService = new RealTimeDbConnectionService();
        this.registered_user = new HashSet<>();
        this.adapter = adapter;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //check whether user exists or not
        DatabaseReference databaseReference = new RealTimeDbConnectionService().getConnection()
                .getReference("socialmedia").child(search_input);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("User", "Exists");
                    do_it(snapshot.getValue(Contact.class));
                } else {
                    //no such user
                    Toast.makeText(context, "User does not exists", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error fetching in data", Toast.LENGTH_SHORT).show();
            }
        });

        //get current user phone number

        return null;
    }

    void do_it(Contact searched_user_data) {
        Log.d("Inside-do-it", "");
        DatabaseReference databaseReference = new RealTimeDbConnectionService().getConnection()
                .getReference("metaData").child(new Constants().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String current_user_phone_number = snapshot.getValue(String.class);
                    Log.d("Current_user_phone_number", current_user_phone_number);
                    if (current_user_phone_number.equals(search_input)) {
                        Toast.makeText(context, "Can not search yourself", Toast.LENGTH_SHORT).show();
                    } else {
                        //getting current user following list
                        new RealTimeDbConnectionService().getConnection().getReference("socialmedia")
                                .child(current_user_phone_number).child("following").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            GenericTypeIndicator<List<String>> typeIndicator = new GenericTypeIndicator<List<String>>() {
                                            };
                                            List<String> following_list = snapshot.getValue(typeIndicator);
                                            Log.d("Following list", "" + following_list);
                                            if (following_list != null && following_list.contains(search_input)) {
                                                Toast.makeText(context, "Already Following", Toast.LENGTH_SHORT).show();
                                            } else {
                                                showDialog();
                                            }
                                        }else {
                                            showDialog();
                                        }

                                    }

                                    private void showDialog() {
                                        Dialog dialog = new Dialog(context);
                                        dialog.setContentView(R.layout.search_result_dialog);
                                        Button close = dialog.findViewById(R.id.button_close);
                                        close.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();

                                            }
                                        });
                                        TextView textView = dialog.findViewById(R.id.text_view_name_dialog);
                                        textView.setText(searched_user_data.getName());
                                        ImageView imageView = dialog.findViewById(R.id.imageView_avatar_search);
                                        if (searched_user_data.getImage_uri() != null) {
                                            new DownloadImageThread(searched_user_data.getImage_uri(), imageView).execute();
                                        } else {
                                            imageView.setImageResource(R.drawable.default_face_image_contacts);
                                        }
                                        Button follow = dialog.findViewById(R.id.button_follow);
                                        follow.setOnClickListener(new View.OnClickListener() {
                                            @SuppressLint("NotifyDataSetChanged")
                                            @Override
                                            public void onClick(View v) {
                                                boolean flag = false;

                                                dialog.dismiss();
                                                // Update your local data source here
//                                                for (Contact contact : adapter.getContacts()) {
//                                                    if (contact.getPhone_number().equals(search_input)) {
//                                                        flag = true;
//                                                        int position = adapter.getContacts().indexOf(contact);
//                                                        adapter.deletePosition(position);
//                                                        adapter.notifyDataSetChanged();
//                                                    }
//                                                }
                                                List<Integer> positionsToDelete = new ArrayList<>();
                                                for (Contact contact : adapter.getContacts()) {
                                                    if (contact.getPhone_number().equals(search_input)) {
                                                        positionsToDelete.add(adapter.getContacts().indexOf(contact));
                                                    }
                                                }

                                                for (Integer position : positionsToDelete) {
                                                    adapter.deletePosition(position);
                                                }

                                                adapter.notifyDataSetChanged();

                                                if (!flag) {
                                                    new AddFollowingDataToFirebase(search_input).execute();
                                                }

                                            }
                                        });

                                        dialog.show();
                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }
                } else {

                    Toast.makeText(context, "In-to-do-it-User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}