package edu.northeastern.final_project.backgroundThreadClass;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import edu.northeastern.final_project.adapter.ContactsAdapter;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;


public class GetContactsThread  extends GenericAsyncClassThreads<Void,Void,List<List<Contact>>> {

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
        List<Contact> contacts =  getAddressBookContacts(context);
        Log.d("ListSize", "" + contacts.size());
        //call db to get contacts registered under FoodFit
        final CountDownLatch latch = new CountDownLatch(1);
        new RealTimeDbConnectionService().getRegisteredContacts(latch,registered_user);
        try {
            latch.await();
            Log.d("Registered_User",""+registered_user);
            CountDownLatch latch2 = new CountDownLatch(1);
            List<Contact> add_friends_list = filter_contacts(latch2,contacts,registered_user);
            try{
                latch2.await();
                filtered_lists.add(contacts_not_registered);
                filtered_lists.add(add_friends_list);
            }catch(InterruptedException ex){
                Log.d("Error", ex.getMessage());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return filtered_lists;

    }

    private List<Contact> filter_contacts(CountDownLatch latch,List<Contact> contacts, Set<String> registered_user) {
        Log.d("FilterContactsMethod",""+registered_user);
       List<Contact> add_friends_list = new ArrayList<>();

       for(Contact contact : contacts){
           if(registered_user.contains(contact.getPhone_number())){

               //get contact details from firebase db
               add_friends_list.add(new RealTimeDbConnectionService().fetchContactDetails(contact.getPhone_number()));

           }else{
               contacts_not_registered.add(contact);
           }
       }
       latch.countDown();
        return add_friends_list;
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
