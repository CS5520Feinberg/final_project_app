package edu.northeastern.final_project.backgroundThreadClass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.CountDownLatch;

public class DownloadImageThread extends GenericAsyncClassThreads<Void,Void, Bitmap> {
    String imageUri;
    ImageView imageView;

    public DownloadImageThread(String imageUri, ImageView imageView) {
        this.imageUri = imageUri;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(imageUri);

            final CountDownLatch latch = new CountDownLatch(1);

            // Download the image from Firebase Storage
            storageRef.getBytes(Long.MAX_VALUE)
                    .addOnSuccessListener(bytes -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        onPostExecute(bitmap); // Call onPostExecute with the downloaded bitmap
                        latch.countDown(); // Signal that the task is complete
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        onPostExecute(null); // Call onPostExecute with null to indicate failure
                        latch.countDown(); // Signal that the task is complete
                    });

            // Wait for the latch to count down
            latch.await();

            // Return null, as the result is already handled in the callbacks
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null && imageView != null) {
            // Set the downloaded image to the ImageView
            imageView.setImageBitmap(bitmap);
        }else{
            Log.d("Error",""+"ImageNotObtained");
        }
    }
}
