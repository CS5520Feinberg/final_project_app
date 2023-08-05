package edu.northeastern.final_project.activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



import edu.northeastern.final_project.R;
import edu.northeastern.final_project.adapter.ContactsAdapter;
import edu.northeastern.final_project.backgroundThreadClass.GetContactsThread;
import edu.northeastern.final_project.entity.Contact;

public class AddFriendsActivity extends AppCompatActivity {
    RecyclerView contactsRV;
    private ContactsAdapter adapter;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;
    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_add_friends);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    PERMISSION_REQUEST_READ_CONTACTS);
        } else {
            // Permission already granted, retrieve contacts
//            List<Contact> contacts = new ArrayList<>();


//            Log.d("ListSize",""+contacts.size());
            contactsRV = findViewById(R.id.recycler_view_invite_friends);
            contactsRV.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ContactsAdapter(new ArrayList<>(), this);
            contactsRV.setAdapter(adapter);
            new GetContactsThread(this,contactsRV,adapter).execute();
//
        }


    }


//    protected List<Contact> getContacts(){
//        ArrayList<Contact> contactList = new ArrayList<>();
//        // projecting which column is needed from address book. Taken help from documentation
//        String[] projection = {
//                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//                ContactsContract.CommonDataKinds.Phone.NUMBER
//        };
//        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
//        String selection = ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + " > 0";
//        ContentResolver contentResolver = getContentResolver();
//        Cursor cursor = contentResolver.query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                projection,
//                selection,
//                null,
//                sortOrder);
//        if (cursor != null && cursor.getCount() > 0) {
//
//            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//            int phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//
//            while (cursor.moveToNext()) {
//                // Check if the column index is valid
//                if (nameIndex != -1 && phoneNumberIndex != -1) {
//                    String contactName = cursor.getString(nameIndex);
//                    String phoneNumber = cursor.getString(phoneNumberIndex);
//                    Log.d("Contact",""+contactName+" "+phoneNumber);
//                    // Create a new Contact instance and add it to the list
//                    Contact contact = new Contact(contactName, phoneNumber);
//                    contactList.add(contact);
//                }
//            }
//
//            cursor.close();
//        }
//            return contactList;
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retrieve contacts
//                List<Contact> contacts = getContacts();
                adapter = new ContactsAdapter(new ArrayList<>(), this);
                contactsRV = findViewById(R.id.recycler_view_invite_friends);
                contactsRV.setLayoutManager(new LinearLayoutManager(this));
                new GetContactsThread(this,contactsRV,adapter).execute();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied. Cannot retrieve contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
