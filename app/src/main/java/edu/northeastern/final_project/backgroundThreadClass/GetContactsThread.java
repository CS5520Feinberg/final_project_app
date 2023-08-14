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
    List<List<Contact>> filtered_lists;


    public GetContactsThread(Context context, RecyclerView contactsRv, RecyclerView add_friends_RV, ContactsAdapter contactsAdapter, ContactsAdapter add_friends_adapter) {
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
        List<Contact> contacts = getAddressBookContacts(context);
        Log.d("ListSize", "" + contacts.size());
        //call db to get contacts registered under FoodFit
        final CountDownLatch latch = new CountDownLatch(1);
        new RealTimeDbConnectionService().getRegisteredContacts(latch, registered_user);
        try {
            latch.await();
            Log.d("Registered_User", "" + registered_user);
            List<Contact> add_friends_list = filter_contacts(contacts, registered_user);
            filtered_lists.add(contacts_not_registered);
            filtered_lists.add(add_friends_list);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return filtered_lists;

    }

    private List<Contact> filter_contacts(List<Contact> contacts, Set<String> registered_user) {
        Log.d("FilterContactsMethod", "" + registered_user);
        List<Contact> add_friends_list = new ArrayList<>();


        // Use a separate latch for synchronization
        CountDownLatch latch = new CountDownLatch(1);

        new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
            @Override
            public void onSuccess(Contact contact) {
                List<String> following = contact.getFollowing();
                Log.d("Got_User_DATA", "" + contact);
                if (following != null) {
                    synchronized (registered_user) {
                        registered_user.removeAll(following);
                        registered_user.remove(contact.getPhone_number());
                    }
                    synchronized (contacts) {
                        contacts.removeAll(following);
                        contacts.remove(contact.getPhone_number());
                    }
                } else {
                    synchronized (registered_user) {

                        registered_user.remove(contact.getPhone_number());
                    }
                    synchronized (contacts) {
                        contacts.remove(contact.getPhone_number());
                    }
                }

                latch.countDown();
            }

            @Override
            public void onError(String message) {
                Log.d("Error", message);
                latch.countDown();
            }
        });

        try {
            latch.await();
            synchronized (add_friends_list) {
                for (Contact contact : contacts) {
                    if (registered_user.contains(contact.getPhone_number())) {
                        Log.d("Inside", "inside-log");
                        CountDownLatch latch1 = new CountDownLatch(1);
                        new RealTimeDbConnectionService().fetchContactDetails(latch1, contact.getPhone_number(), new ContactFetchedCallBack() {
                            @Override
                            public void contactFetched(Contact contact) {
                                Log.d("onFetched", "adding friend to list");
                                add_friends_list.add(contact);
                            }

                            @Override
                            public void errorFetched(String errorMessage) {
                                Log.d("Error", errorMessage);
                            }

                            @Override
                            public void noDataFound() {
                                Log.d("No Data", "");
                            }
                        });
                        try {
                            latch1.await();
                        } catch (InterruptedException ex) {
                            ex.getMessage();
                        }
                    } else {
                        contacts_not_registered.add(contact);
                    }
                }

            }
        } catch (InterruptedException ex) {
            // Handle the exception appropriately
        }

        // Return a copy of the filtered list to prevent modification during iteration
        synchronized (add_friends_list) {
            return new ArrayList<>(add_friends_list);
        }
    }
//

    protected void onPostExecute(List<List<Contact>> contacts) {
        super.onPostExecute(contacts);

        Log.d("Before Adapter Set", "" + "Before Adapter SEt in post execute");

        contactsAdapter.setContacts(contacts.get(0));
        contactsAdapter.notifyDataSetChanged();
        add_friends_adapter.setContacts(contacts.get(1));
        add_friends_adapter.notifyDataSetChanged();
    }
}
