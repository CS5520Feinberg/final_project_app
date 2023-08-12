package edu.northeastern.final_project.activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import edu.northeastern.final_project.R;
import edu.northeastern.final_project.adapter.ContactsAdapter;
import edu.northeastern.final_project.backgroundThreadClass.GetContactsThread;

import edu.northeastern.final_project.fragments.SearchBoxFragment;
import edu.northeastern.final_project.validation.GenericStringValidation;

public class AddFriendsActivity extends AppCompatActivity {
    RecyclerView contactsRV;
    RecyclerView add_friends_RV;
    private ContactsAdapter contactsAdapter;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;
    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_add_friends);
        if (savedInstancesState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_view, new SearchBoxFragment())
                    .commit();
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    PERMISSION_REQUEST_READ_CONTACTS);
        } else {

            setView();
        }


    }

    private void setView() {
        add_friends_RV = findViewById(R.id.recycler_view_add_friends);
        contactsRV = findViewById(R.id.recycler_view_invite_friends);
        add_friends_RV.setLayoutManager(new LinearLayoutManager(this));

        contactsRV.setLayoutManager(new LinearLayoutManager(this));

        contactsAdapter = new ContactsAdapter(new ArrayList<>(), this,"Invite");
        ContactsAdapter add_friends_adapter = new ContactsAdapter(new ArrayList<>(),this,"Follow");
        contactsRV.setAdapter(contactsAdapter);
        add_friends_RV.setAdapter(add_friends_adapter);
        new GetContactsThread(this,contactsRV, add_friends_RV,contactsAdapter,add_friends_adapter).execute();
//
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retrieve contacts
//                List<Contact> contacts = getContacts();
                setView();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied. Cannot retrieve contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (contactsAdapter != null) {
            contactsAdapter.notifyDataSetChanged(); // Or use specific notify methods like notifyItemInserted, notifyItemChanged, etc.
        }
    }
}
