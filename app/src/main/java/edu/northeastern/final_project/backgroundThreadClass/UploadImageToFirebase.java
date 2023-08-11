package edu.northeastern.final_project.backgroundThreadClass;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import edu.northeastern.final_project.Constants;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;

public class UploadImageToFirebase extends GenericAsyncClassThreads<Void,Void,Boolean> {
    Context context;
    Uri imageUri;


    public UploadImageToFirebase(Uri imageUri,Context context) {
        this.context = context;
        this.imageUri = imageUri;

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            String phoneNumber = new RealTimeDbConnectionService().getPhoneNumberFromDatabase(new Constants().getUid());
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
                });
            }).addOnFailureListener(e -> {
                // Handle upload failure
            });

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (isSuccess) {
            // Image upload was successful
            Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Image upload failed
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show();
        }
    }
}

