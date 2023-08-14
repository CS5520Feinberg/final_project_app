package edu.northeastern.final_project.backgroundThreadClass;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.validation.GenericStringValidation;

public abstract class GenericAsyncClassThreads<Input, Progress, Result> extends AsyncTask<Input, Progress, Result> {
    protected List<Contact> getAddressBookContacts(Context context) {
        ArrayList<Contact> contactList = new ArrayList<>();
        // projecting which column is needed from address book. Taken help from documentation
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        String selection = ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + " > 0";
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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

                    if (new GenericStringValidation<>("^[1-9]{1}[0-9]{9}").validateString(phoneNumber)) {

                        Log.d("Contact", "" + contactName + " " + phoneNumber);

                        Contact contact = new Contact(contactName, phoneNumber);
                        contactList.add(contact);

                    }


                }
            }

            cursor.close();
        }
        return contactList;
    }


}
