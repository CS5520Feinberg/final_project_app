package edu.northeastern.final_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private static final String CHANNEL_ID = "my_channel_id";



    @NonNull
    @Override
    public Result doWork() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DBHandler dbHandler = new DBHandler(getApplicationContext(), firebaseUser.getUid());
//        HashMap<String, Integer> hmap = dbHandler.getDailySteps();
        ArrayList<Float> arrayList = dbHandler.getWeeklyCalories();
        if (arrayList.size() > 0) {
//            String todaysDate = DateUtils.getTodaysDate();
//            Log.d("HEREEEE", hmap.get(todaysDate) + "dsvneono");
//            Log.d("HEREEEE", dbHandler.readWeeklyDailyGoal() + "dsvneono");

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference("users");
            DatabaseReference userRef = databaseReference.child(firebaseUser.getUid());

            float currDailyCalories = arrayList.get(arrayList.size() - 1);

            userRef.child("isGoalReached").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean isGoalReached = dataSnapshot.getValue(Boolean.class);

                    if (currDailyCalories < dbHandler.readWeeklyDailyGoal()) {
                        // set isGoalReached to false
                        userRef.child("isGoalReached").setValue(false);
                    }

                    if (dbHandler.readWeeklyDailyGoal() > 0 && currDailyCalories >= dbHandler.readWeeklyDailyGoal() && isGoalReached != null && !isGoalReached) {
                        // update isGoalReached to true in the cloud
                        userRef.child("isGoalReached").setValue(true);
                        createNotificationChannel(getApplicationContext());
                        showBasicNotification(getApplicationContext());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }


        return Result.success();
    }


    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "context.getString(R.string.channel_name)";
            String description = "context.getString(R.string.channel_description)";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showBasicNotification(Context context) {
        Intent intent = new Intent(context, NotificationDetailsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("My Notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        int notificationId = 1; // A unique ID for the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }
}

