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
import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.interfaces.PhoneNumberFetchedCallback;

public class UploadImageToFirebase extends GenericAsyncClassThreads<Void,Void,Boolean> {
    Contact user;
    Context context;
    Uri imageUri;


    public UploadImageToFirebase(Uri imageUri,Context context,Contact user ) {
        this.context = context;
        this.imageUri = imageUri;
        this.user = user;

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference profileImageRef = storageRef.child("user_profiles/" + user.getPhone_number() + "/profile_image.jpg");
                    UploadTask uploadTask = profileImageRef.putFile(imageUri);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        // Get the download URL
                        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Store the image URL in the Realtime Database or Firestore
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("socialmedia").child(user.getPhone_number());
                            userRef.child("image_uri").setValue(imageUrl);
                            onPostExecute(true);
                        });
                    }).addOnFailureListener(e -> {
                        Log.d("Error",e.getMessage());
                        onPostExecute(false);
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

