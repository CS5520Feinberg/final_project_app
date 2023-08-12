package edu.northeastern.final_project.backgroundThreadClass;
import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import edu.northeastern.final_project.R;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;

public class SearchPhoneNumberThread extends GenericAsyncClassThreads<Void,Void,Contact>{
    Context context;
    String search_input;
    Set<String> registered_user;
    RealTimeDbConnectionService dbService;

    public SearchPhoneNumberThread(Context context, String search_input) {
        this.context = context;
        this.search_input = search_input;
        this.dbService= new RealTimeDbConnectionService();
        this.registered_user = new HashSet<>();
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
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.search_result_dialog);
            dialog.show();
        }
    }
}

