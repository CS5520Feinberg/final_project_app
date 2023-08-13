package edu.northeastern.final_project.backgroundThreadClass;

import android.util.Log;
import android.widget.Toast;

import com.google.android.play.integrity.internal.c;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.northeastern.final_project.adapter.ContactsAdapter;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.interfaces.ContactFetchedCallBack;
import edu.northeastern.final_project.interfaces.UserDataFetchedCallback;

public class GetContactSocialData extends GenericAsyncClassThreads<Void, Void, List<Contact>> {
    ContactsAdapter contactsAdapter;
    public GetContactSocialData(ContactsAdapter followersAdapter) {
       this.contactsAdapter = followersAdapter;
    }
    @Override
    protected List<Contact> doInBackground(Void... voids) {
        List<Contact> followers_contact_data = new ArrayList<>();

        new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
            @Override
            public void onSuccess(Contact contact) {
                Log.d("User Data", "Fetched user data successfully" + contact.getFollower());
                List<String> followers = contact.getFollower();
                if (followers != null && followers.size() > 0) {
                    CountDownLatch latch = new CountDownLatch(followers.size());
                    Log.d("Starting threads", "followers data from db");

                    Log.d("Thread", "Inside inner thread");
                    for (String contact_number : followers) {
                        Log.d("Fetching Data for", contact_number);
                        new RealTimeDbConnectionService().fetchContactDetails(latch, contact_number, new ContactFetchedCallBack() {
                            @Override
                            public void contactFetched(Contact contact) {
                                followers_contact_data.add(contact);
                                Log.d("Fetched Data", contact_number);
                                // Don't count down here, only count down in the fetchContactDetails callback
                            }

                            @Override
                            public void errorFetched(String errorMessage) {
                                Log.d("Error", errorMessage);
                                // Don't count down here, only count down in the fetchContactDetails callback
                            }

                            @Override
                            public void noDataFound() {
                                Log.d("404", "NO DATA FOUND");
                                // Don't count down here, only count down in the fetchContactDetails callback
                            }
                        });
                    }

                    try {
                        latch.await();
                    } catch (InterruptedException ex) {
                        Log.d("Error", ex.getMessage());
                    }
                }
            }

            @Override
            public void onError(String message) {
                Log.d("Error", message);
            }
        });

        return followers_contact_data;
    }




//
//    @Override
//    protected List<Contact> doInBackground(Void... voids) {
//        new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
//            @Override
//            public void onSuccess(Contact contact) {
//                Log.d("User Data", "Fetched user data successfully" + contact.getFollower());
//                List<String> followers = contact.getFollower();
//                if (followers != null && followers.size() > 0) {
//                    List<Contact> followers_contact_data = new ArrayList<>();
//                    CountDownLatch latch = new CountDownLatch(followers.size());
//                    Log.d("Starting threads", "followers data from db");
//
//
//                    Log.d("Thread", "Inside inner thread");
//                    for (String contact_number : followers) {
//                        Log.d("Fetching Data for", contact_number);
//                        CountDownLatch latch1 = new CountDownLatch(1);
//                        new RealTimeDbConnectionService().fetchContactDetails(latch1, contact_number, new ContactFetchedCallBack() {
//                            @Override
//                            public void contactFetched(Contact contact) {
//                                followers_contact_data.add(contact);
//                                Log.d("Fetched Data", contact_number);
//                                 latch.countDown();
//                            }
//
//                            @Override
//                            public void errorFetched(String errorMessage) {
//                                Log.d("Error", errorMessage);
//                                latch.countDown();
//                            }
//
//                            @Override
//                            public void noDataFound() {
//                                latch.countDown();
//                                Log.d("404", "NO DATA FOUND");
//                            }
//                        });
//                    }
//                    try{
//                        latch.await();
//                        onPostExecute(followers_contact_data);
//
//                    } catch (InterruptedException ex){
//
//                    }
//
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onError(String message) {
//
//            }
//
//
//        });
//        return null;
//    }

    @Override
    protected void onPostExecute(List<Contact> contacts) {
        if(contacts!=null && contacts.size()!=0){
            contactsAdapter.setContacts(contacts);
            contactsAdapter.notifyDataSetChanged();
        }else{
            Log.d("NUll","Getting null contact");
        }


    }
}
// new Thread(new Runnable(){
//@Override
//public void run(){
//
//        new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback(){
//@Override
//public void onSuccess(Contact contact){
//        Log.d("User Data","Fetched user data successfully"+contact.getFollower());
//        List<String> followers=contact.getFollower();
//        if(followers!=null&&followers.size()>0){
//        List<Contact> followers_contact_data=new ArrayList<>();
//        // CountDownLatch latch = new CountDownLatch(followers.size());
//        Log.d("Starting threads","followers data from db");
//        CountDownLatch latch_2=new CountDownLatch(1);
//
//        Log.d("Thread"+getContext(),"Inside inner thread");
//        for(String contact_number:followers){
//        Log.d("Fetching Data for",contact_number);
//
//
//        new RealTimeDbConnectionService().fetchContactDetails(latch_2,contact_number,new ContactFetchedCallBack(){
//@Override
//public void contactFetched(Contact contact){
//        followers_contact_data.add(contact);
//        Log.d("Fetched Data",contact_number);
//        //   latch.countDown();
//        }
//
//@Override
//public void errorFetched(String errorMessage){
//        Log.d("Error",errorMessage);
//        }
//
//@Override
//public void noDataFound(){
//        Log.d("404","NO DATA FOUND");
//        }
//        });
//
//        }
//
//        //   Log.d("Thread-completed", followers_contact_data.get(0).getName());
//
//        try{
//        latch_2.await();
//        Log.d("Fetched Data","Now setting Rv");
//        getActivity().runOnUiThread(new Runnable(){
//@Override
//public void run(){
//        followersAdapter.setContacts(followers_contact_data);
//        followersAdapter.notifyDataSetChanged();
//        }
//        });
//
//        }catch(InterruptedException ex){
//        Log.d("Error",ex.getMessage());
//        }
//        }
//        }
//
//@Override
//public void onError(String message){
//        Log.d("Error",message);
//        }
//        });
//        }
//        }).start();
