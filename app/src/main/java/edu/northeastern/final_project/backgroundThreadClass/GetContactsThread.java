package edu.northeastern.final_project.backgroundThreadClass;

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

import edu.northeastern.final_project.interfaces.ContactFetchedCallBack;
import edu.northeastern.final_project.interfaces.UserDataFetchedCallback;


public class GetContactsThread extends GenericAsyncClassThreads<Void, Void, List<List<Contact>>> {
    Context context;
    private RecyclerView contactsRV;
    ContactsAdapter contactsAdapter;
    RecyclerView add_friends_RV;
    ContactsAdapter add_friends_adapter;
    Set<String> registered_user;
    List<Contact> contacts_not_registered;
    List<List<Contact>> adapterDataLists;


    public GetContactsThread(Context context, RecyclerView contactsRv, RecyclerView add_friends_RV, ContactsAdapter contactsAdapter, ContactsAdapter add_friends_adapter) {
        this.context = context;
        this.contactsRV = contactsRv;
        this.contactsAdapter = contactsAdapter;
        this.add_friends_RV = add_friends_RV;
        this.add_friends_adapter = add_friends_adapter;
        this.registered_user = new HashSet<>();
        this.contacts_not_registered = new ArrayList<>();
        this.adapterDataLists = new ArrayList<>();
    }

    @Override
    protected List<List<Contact>> doInBackground(Void... voids) {
        List<Contact> contacts = getAddressBookContacts(context);
        Log.d("ListSize", "" + contacts.size());


        //call db to get contacts registered under FoodFit
        CountDownLatch latch2 = new CountDownLatch(1);
        new RealTimeDbConnectionService().getRegisteredContacts(latch2, registered_user);
        try {
            latch2.await();
            Log.d("Registered_User", "" + registered_user);
            List<Contact> add_friends_list = filter_contacts(contacts, registered_user);
            Log.d("Add friends list",""+add_friends_list);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }

    private List<Contact> filter_contacts(List<Contact> contacts, Set<String> registered_user) {
        Log.d("FilterContactsMethod", "" + registered_user);
        final List<Contact>[] add_friends_list = new List[]{new ArrayList<>()};




        new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {

            Object lock = new Object(); // Create a lock object for synchronization

            @Override
            public void onSuccess(Contact userData) {
                List<String> following = userData.getFollowing();
                Log.d("Got_User_DATA", "" + userData);
                if (following != null) {
                    Log.d("Remove followed user","Remove followed user");
                        registered_user.removeAll(following);
                        registered_user.remove(userData.getPhone_number());
                        List<Contact> contacts_to_remove = new ArrayList<>();
                        for(Contact contact : contacts){
                            if(following.contains(contact.getPhone_number()) || contact.getPhone_number().equals(userData.getPhone_number())){
                                contacts_to_remove.add(contact);
                            }
                        }
                        contacts.removeAll(contacts_to_remove);
                } else {
                        registered_user.remove(userData.getPhone_number());
                    Contact contactToRemove = null;
                    for(Contact contact : contacts){
                        if( contact.getPhone_number().equals(userData.getPhone_number())){
                            contactToRemove = contact;
                            break;
                        }
                    }
                    contacts.remove(contactToRemove);
                }
                Log.d("Registered user Not followed",""+registered_user);
                Log.d("Contacts not followed",""+contacts);

//                contacts.removeAll(registered_user);
//
//                contacts_not_registered.addAll(contacts);
                List<String> all_contacts_registered_not_followed_in_contact_book = new ArrayList<>();
                for (Contact contact : contacts) {
                    if (registered_user.contains(contact.getPhone_number())) {
                        all_contacts_registered_not_followed_in_contact_book.add(contact.getPhone_number());
                    } else {

                        contacts_not_registered.add(contact);
                    }
                }
                Log.d("No of Contacts not registered",""+contacts_not_registered.size());
                adapterDataLists.add(contacts_not_registered);
                new RealTimeDbConnectionService().fetchMultipleUserData(all_contacts_registered_not_followed_in_contact_book, new ContactFetchedCallBack() {
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
                        Log.d("Sending Data",""+contacts);
                        add_friends_list[0] = contacts;
                        adapterDataLists.add(add_friends_list[0]);
                        onPostExecute(adapterDataLists);
                    }
                });



            }


            @Override
            public void onError(String message) {
                Log.d("Error", message);

            }
        });
        synchronized (add_friends_list[0]) {
            return new ArrayList<>(add_friends_list[0]);
        }
    }
//

    protected void onPostExecute(List<List<Contact>> contacts) {
        super.onPostExecute(contacts);
        if(contacts == null){

        }else{
            Log.d("Before Adapter Set", "" + "Before Adapter SEt in post execute");

            contactsAdapter.setContacts(contacts.get(0));
            contactsAdapter.notifyDataSetChanged();
            add_friends_adapter.setContacts(contacts.get(1));
            add_friends_adapter.notifyDataSetChanged();
        }


    }
}
