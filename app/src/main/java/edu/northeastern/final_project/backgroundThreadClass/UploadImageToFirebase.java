package edu.northeastern.final_project.backgroundThreadClass;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import edu.northeastern.final_project.Constants;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.interfaces.PhoneNumberFetchedCallback;

public class UploadImageToFirebase extends GenericAsyncClassThreads<Void,Void,Boolean> {
    Context context;
    Uri imageUri;


    public UploadImageToFirebase(Uri imageUri,Context context) {
        this.context = context;
        this.imageUri = imageUri;

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

             new RealTimeDbConnectionService().getPhoneNumberFromDatabase(new Constants().getUid(), new PhoneNumberFetchedCallback() {
                @Override
                public void onPhoneNumberFetched(String phoneNumber) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference profileImageRef = storageRef.child("user_profiles/" + phoneNumber + "/profile_image.jpg");
                    UploadTask uploadTask = profileImageRef.putFile(imageUri);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        // Get the download URL
                        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Store the image URL in the Realtime Database or Firestore
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("socialmedia").child(phoneNumber);
                            userRef.child("image_uri").setValue(imageUrl);
                            onPostExecute(true);
                        });
                    }).addOnFailureListener(e -> {
                        Log.d("Error",e.getMessage());
                        onPostExecute(false);
                    });
                }

                @Override
                public void onError(Exception ex) {
                    Log.d("Error",ex.getMessage());
                    onPostExecute(false);
                }
            });
             return null;

    }



    protected void onPostExecute(Boolean isSuccess) {
        if (isSuccess) {
            // Image upload was successful
            Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Image upload failed
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show();
        }
    }
}

