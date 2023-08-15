package edu.northeastern.final_project.backgroundThreadClass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.ExecutionException;

public class DownloadImageThread extends GenericAsyncClassThreads<Void, Void, Bitmap> {
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

            // Download the image from Firebase Storage
            Task<byte[]> downloadTask = storageRef.getBytes(Long.MAX_VALUE);

            try {
                byte[] bytes = Tasks.await(downloadTask); // Wait for the download task to complete
                if (bytes != null && bytes.length > 0) {
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        Log.d("Main Thread",getClass().getName());
                        // Code is running on the main thread
                        // You can safely update UI components here
                    } else {
                        Log.d("Background Thread",getClass().getName());
                        // Code is running on a background thread
                        // You should not update UI components directly from here
                    }

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    return bitmap;
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null in case of failure
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null && imageView != null) {
            // Set the downloaded image to the ImageView
            imageView.setImageBitmap(bitmap);
        } else {
            Log.d("Error", "" + "ImageNotObtained");
        }
    }
}
