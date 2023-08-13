package edu.northeastern.final_project.backgroundThreadClass;
import android.app.Dialog;
import android.content.Context;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;


import edu.northeastern.final_project.R;
import edu.northeastern.final_project.adapter.ContactsAdapter;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.interfaces.UserDataFetchedCallback;

public class SearchPhoneNumberThread extends GenericAsyncClassThreads<Void,Void,Contact>{
    Context context;
    String search_input;
    Set<String> registered_user;
    RealTimeDbConnectionService dbService;
    ContactsAdapter adapter;

    public SearchPhoneNumberThread(Context context, String search_input, ContactsAdapter adapter) {
        this.context = context;
        this.search_input = search_input;
        this.dbService= new RealTimeDbConnectionService();
        this.registered_user = new HashSet<>();
        this.adapter = adapter;
    }

    @Override
    protected Contact doInBackground(Void... voids) {


        CountDownLatch latch = new CountDownLatch(1);
        dbService.getRegisteredContacts(latch,registered_user);
        Contact contact = null;
        try{
            latch.await();
            if (registered_user.contains(search_input)) {
                contact = dbService.fetchContactDetails(search_input);

            }
        }catch (InterruptedException ex){

        }


        return contact;
    }


    @Override
    protected void onPostExecute(Contact contact) {
        super.onPostExecute(contact);
        if(contact==null){
            Toast.makeText(context,"No such contact exist",Toast.LENGTH_SHORT).show();
        }else{
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
                        @Override
                        public void onSuccess(Contact user) {

                            Handler handler = new Handler(context.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.search_result_dialog);
                                    Button close = dialog.findViewById(R.id.button_close);
                                    close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();

                                        }
                                    });


                                    TextView textView = dialog.findViewById(R.id.text_view_name_dialog);
                                    textView.setText(contact.getName());
                                    ImageView imageView = dialog.findViewById(R.id.imageView_avatar_search);
                                    if (contact.getImage_uri() != null) {
                                        new DownloadImageThread(contact.getImage_uri(), imageView).execute();
                                    } else {
                                        imageView.setImageResource(R.drawable.default_face_image_contacts);
                                    }

                                    if(!search_input.equals(user.getPhone_number()) && (user.getFollowing()==null || !user.getFollowing().contains(search_input))) {
                                        Button follow = dialog.findViewById(R.id.button_follow);
                                        follow.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                boolean flag = false;

                                                dialog.dismiss();
                                                // Update your local data source here
                                                for(Contact contact: adapter.getContacts()){
                                                    if(contact.getPhone_number().equals(search_input)){
                                                        flag = true;
                                                        int position = adapter.getContacts().indexOf(contact);
                                                        adapter.deletePosition(position);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }
                                                if(!flag){
                                                    new AddFollowingDataToFirebase(search_input).execute();
                                                }

                                            }
                                        });

                                        dialog.show();
                                    }else if(user.getFollowing()!=null && user.getFollowing().contains(search_input)) {
                                        Button follow_button = dialog.findViewById(R.id.button_follow);
                                        follow_button.setText("Following");
                                        dialog.show();
                                    }else if(user.getFollowing()!=null && !user.getFollowing().contains(search_input)){
                                        Button follow = dialog.findViewById(R.id.button_follow);
                                        follow.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //new AddFollowingDataToFirebase(search_input).execute(); will be va
                                                boolean flag = false;

                                                dialog.dismiss();
                                                // Update your local data source here
                                                for(Contact contact: adapter.getContacts()){
                                                    if(contact.getPhone_number().equals(search_input)){
                                                        flag = true;
                                                        int position = adapter.getContacts().indexOf(contact);
                                                        adapter.deletePosition(position);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }
                                                if(!flag){
                                                    new AddFollowingDataToFirebase(search_input).execute();
                                                }

                                            }
                                        });
                                        dialog.show();
                                    }
                                    else{
                                        Toast.makeText(context,"Can not search your own contact",Toast.LENGTH_SHORT);
                                        dialog.dismiss();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onError(String message) {

                        }
                    });
                }
            });
            thread.start();


        }
    }
}

