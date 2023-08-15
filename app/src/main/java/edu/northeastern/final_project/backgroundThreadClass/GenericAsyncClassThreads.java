package edu.northeastern.final_project.backgroundThreadClass;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
                    Log.d("Contact",""+contactName+" "+phoneNumber);
                    String parsed_phoneNumber = parsePhoneNumber(phoneNumber);
                    Log.d("Phone_Number_Parsed",parsed_phoneNumber);
                    // Create a new Contact instance and add it to the list

                    String pattern_regex = "^[1-9]{1}[0-9]{9}";
                    Pattern pattern = Pattern.compile(pattern_regex);

                    if (new GenericStringValidation<Pattern>(pattern).validateString(parsed_phoneNumber)) {
                        Log.d("Contact", "" + contactName + " " + parsed_phoneNumber);

                        Contact contact = new Contact(contactName, parsed_phoneNumber);
                        contactList.add(contact);
                    }


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





}
