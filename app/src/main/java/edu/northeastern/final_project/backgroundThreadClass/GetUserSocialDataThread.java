package edu.northeastern.final_project.backgroundThreadClass;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import edu.northeastern.final_project.R;
import edu.northeastern.final_project.activity.SocialMediaActivity;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;

public class GetUserSocialDataThread extends GenericAsyncClassThreads<Void,Void,Contact>{
    Context context;
    TextView profileName;
    TextView followers_number ;
    TextView following_number;
    ImageView imageView;



    public GetUserSocialDataThread(SocialMediaActivity context, TextView profileName, TextView following_number, TextView followers_number, ImageView imageView) {
        this.context = context;
        this.profileName = profileName;
        this.following_number = following_number;
        this.followers_number = followers_number;
        this.imageView = imageView;
    }

    @Override
    protected Contact doInBackground(Void... voids) {
        try {
            Contact profileData = new RealTimeDbConnectionService().getUserProfileData();
            return profileData;
        }catch (Exception ex){
            Log.d("Error",ex.getMessage());
            return null;
        }



    }
    @Override
    protected void onPostExecute(Contact contact) {
        super.onPostExecute(contact);
        if(contact == null){
            Log.d("ERROR","User social media data does not exist");
        }else{
            String profile_name_string = contact.getName();
            if(contact.getFollower()==null){
                followers_number.setText("0");

            }else{
                followers_number.setText(contact.getFollower().size());
            }
            if(contact.getFollowing()==null){
                following_number.setText("0");

            }else{
                following_number.setText(contact.getFollowing().size());
            }

            profileName.setText(profile_name_string);
            if(contact.getImage_uri()==null){
                imageView.setImageResource(R.drawable.nophotoimage);
            }else{
                new DownloadImageThread(contact.getImage_uri(),imageView).execute();
            }

        }

    }
}