package edu.northeastern.final_project.backgroundThreadClass;



import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.final_project.adapter.ContactsAdapter;
import edu.northeastern.final_project.adapter.CustomAdapter;
import edu.northeastern.final_project.entity.Contact;

public class GetContactsThread extends AsyncTask<Void, Void, List<Contact>> {
    Context context;
    private RecyclerView contactsRV;
    ContactsAdapter adapter;


    public GetContactsThread(Context context, RecyclerView contactsRv, ContactsAdapter adapter) {
        this.context = context;
        this.contactsRV = contactsRv;
        this.adapter = adapter;
    }

    @Override
    protected List<Contact> doInBackground(Void... voids) {
        List<Contact> contacts =  getContacts();
        Log.d("ListSize", "" + contacts.size());


        return contacts;
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
                    // Create a new Contact instance and add it to the list
                    Contact contact = new Contact(contactName, phoneNumber);
                    contactList.add(contact);
                }
            }

            cursor.close();
        }
        return contactList;
    }
    protected void onPostExecute(List<Contact> contacts) {
        super.onPostExecute(contacts);

        Log.d("Before Adapter Set", "" + "Before Adapter SEt in post execute");

        adapter.setContacts(contacts);
        adapter.notifyDataSetChanged();
    }
}
