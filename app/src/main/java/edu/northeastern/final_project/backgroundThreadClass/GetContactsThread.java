package edu.northeastern.final_project.backgroundThreadClass;





import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import edu.northeastern.final_project.adapter.ContactsAdapter;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;


public class GetContactsThread extends AsyncTask<Void, Void, List<List<Contact>>> {
    Context context;
    private RecyclerView contactsRV;
    ContactsAdapter contactsAdapter;
    RecyclerView    add_friends_RV;
    ContactsAdapter add_friends_adapter;
    Set<String> registered_user;
    List<Contact> contacts_not_registered;
    List<List<Contact>> filtered_lists;


    public GetContactsThread(Context context, RecyclerView contactsRv, RecyclerView add_friends_RV,ContactsAdapter contactsAdapter, ContactsAdapter add_friends_adapter) {
        this.context = context;
        this.contactsRV = contactsRv;
        this.contactsAdapter = contactsAdapter;
        this.add_friends_RV = add_friends_RV;
        this.add_friends_adapter = add_friends_adapter;
        this.registered_user = new HashSet<>();
        this.contacts_not_registered = new ArrayList<>();
        this.filtered_lists = new ArrayList<>();
    }

    @Override
    protected List<List<Contact>> doInBackground(Void... voids) {
        List<Contact> contacts =  getContacts();
        Log.d("ListSize", "" + contacts.size());
        //call db to get contacts registered under FoodFit
        final CountDownLatch latch = new CountDownLatch(1);
        FirebaseDatabase dbConnection = new RealTimeDbConnectionService().getConnection();
        DatabaseReference userRef = dbConnection.getReference("users");
         userRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.exists()){
                     for(DataSnapshot userSnapshot : snapshot.getChildren() ){
                         String userPhoneNumber = userSnapshot.getKey();
                         registered_user.add(userPhoneNumber);
                         Log.d("ContactAdded",""+userPhoneNumber);

                     }
                     Log.d("Registered_User",""+registered_user);
                     List<Contact> add_friends_list = filter_contacts(contacts,registered_user);

                     filtered_lists.add(contacts_not_registered);
                     filtered_lists.add(add_friends_list);

                 }else{
                     Log.d("No data","No Data Fetched");
                 }
                 // Signal the CountDownLatch to count down
                 latch.countDown();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {
                 System.err.println("Error fetching users: " + error.getMessage());
                 // Signal the CountDownLatch to count down
                 latch.countDown();
             }
         });
        // Wait for the CountDownLatch to reach zero, i.e., until onDataChange is complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return filtered_lists;

    }

    private List<Contact> filter_contacts(List<Contact> contacts, Set<String> registered_user) {
        Log.d("FilterContactsMethod",""+registered_user);
       List<Contact> add_friends_list = new ArrayList<>();

       for(Contact contact : contacts){
           if(registered_user.contains(contact.getPhone_number())){
               add_friends_list.add(contact);
           }else{
               contacts_not_registered.add(contact);
           }
       }
        return add_friends_list;
    }

    protected List<Contact> getContacts(){
        ArrayList<Contact> contactList = new ArrayList<>();
        // projecting which column is needed from address book. Taken help from documentation
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        String selection = ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + " > 0";
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder);
        if (cursor != null && cursor.getCount() > 0) {

            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                // Check if the column index is valid
                if (nameIndex != -1 && phoneNumberIndex != -1) {
                    String contactName = cursor.getString(nameIndex);
                    String phoneNumber = cursor.getString(phoneNumberIndex);
                    Log.d("Contact",""+contactName+" "+phoneNumber);
                    String parsed_phoneNumber = parsePhoneNumber(phoneNumber);
                    Log.d("Phone_Number_Parsed",parsed_phoneNumber);
                    // Create a new Contact instance and add it to the list
                    Contact contact = new Contact(contactName, parsed_phoneNumber);
                    contactList.add(contact);
                }
            }

            cursor.close();
        }
        return contactList;
    }

    private String parsePhoneNumber(String phoneNumber) {
        String parsed_number ="";
        for(char c : phoneNumber.toCharArray()){

            if(c>='0' && c<='9'){
                parsed_number+=c;
            }
        }
        return parsed_number;
    }

    protected void onPostExecute(List<List<Contact>> contacts) {
        super.onPostExecute(contacts);

        Log.d("Before Adapter Set", "" + "Before Adapter SEt in post execute");

        contactsAdapter.setContacts(contacts.get(0));
        contactsAdapter.notifyDataSetChanged();
        add_friends_adapter.setContacts(contacts.get(1));
        add_friends_adapter.notifyDataSetChanged();
    }
}
