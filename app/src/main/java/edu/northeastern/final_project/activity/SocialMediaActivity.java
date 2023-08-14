package edu.northeastern.final_project.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.northeastern.final_project.R;
import edu.northeastern.final_project.backgroundThreadClass.GetUserSocialDataThread;
import edu.northeastern.final_project.backgroundThreadClass.UploadImageToFirebase;
import edu.northeastern.final_project.dbConnectionHelpers.RealTimeDbConnectionService;
import edu.northeastern.final_project.entity.Contact;
import edu.northeastern.final_project.fragments.FollowersFragment;
import edu.northeastern.final_project.interfaces.UserDataFetchedCallback;

public class SocialMediaActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1;
    ActivityResultLauncher<Intent> imagePickerLauncher;

    String user_phoneNumber;


    @Override
    protected void onCreate(Bundle SavedInstancesState) {
        super.onCreate(SavedInstancesState);
        setContentView(R.layout.social_media_activity);

        setupActivityData();
        ImageView imageView = findViewById(R.id.imageView);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Uri imageUri = data.getData();
                    // Use the imageUri to do further processing (e.g., display the selected image).
                    imageView.setImageURI(imageUri);
                    //upload image to firebase storage
                    new UploadImageToFirebase(imageUri, this).execute();
                }
            }
        });
        FloatingActionButton floatingButton = findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SocialMediaActivity.this, AddFriendsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupActivityData() {
        ConstraintLayout socialFragment = findViewById(R.id.user_social_data_layout)
                .findViewById(R.id.user_social_data_constraint_layout);
        TextView profileName = socialFragment
                .findViewById(R.id.text_view_profile_name);
        TextView followers_number = socialFragment.findViewById(R.id.text_view_followers_number);
        TextView following_number = socialFragment.findViewById(R.id.text_view_following_number);
        ImageView imageView = socialFragment.findViewById(R.id.imageView);
        socialFragment.findViewById(R.id.tex_view_followers_text).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (getSupportFragmentManager().findFragmentByTag(FollowersFragment.class.getName()) == null) {
                        FollowersFragment followersFragment = new FollowersFragment();
                        Bundle args = new Bundle();
                        args.putString("type", "follower"); // Put any data you want to pass
                        followersFragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view_rv, followersFragment, FollowersFragment.class.getName())
                                .commit();
                    }
                    return true; // Consume the touch event
                }
                return false; // Don't consume the touch event
            }
        });

        socialFragment.findViewById(R.id.tex_view_following_text).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (getSupportFragmentManager().findFragmentByTag(FollowersFragment.class.getName()) == null) {
                        FollowersFragment followingFragment = new FollowersFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "following");
                        followingFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view_rv, followingFragment, FollowersFragment.class.getName())
                                .commit();
                    }
                    return true; // Consume the touch event
                }
                return false; // Don't consume the touch event
            }
        });


        getUserProfileData(profileName, following_number, followers_number, imageView);

    }

    private void getUserProfileData(TextView profileName, TextView following_number, TextView followers_number, ImageView imageView) {

        new GetUserSocialDataThread(this, profileName, following_number, followers_number, imageView).execute();
    }

    public void upload_image(View view) {
        if (hasPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        } else {
            requestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void requestPermission(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_PERMISSION_CODE);
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed to open the image picker.
                upload_image(null); // You can pass null as the argument if you're not using the view parameter.
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user).
                Toast.makeText(this, "Permission denied. Cannot pick an image.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        ConstraintLayout socialFragment = findViewById(R.id.user_social_data_layout)
                .findViewById(R.id.user_social_data_constraint_layout);

        TextView followers_number = socialFragment.findViewById(R.id.text_view_followers_number);
        TextView following_number = socialFragment.findViewById(R.id.text_view_following_number);
        new RealTimeDbConnectionService().getUserProfileData(new UserDataFetchedCallback() {
            @Override
            public void onSuccess(Contact contact) {
                if (contact.getFollower() == null) {
                    followers_number.setText("0");
                } else {
                    followers_number.setText(String.valueOf(contact.getFollower().size()));
                }
                if (contact.getFollowing() == null) {
                    following_number.setText("0");
                } else {
                    following_number.setText(String.valueOf(contact.getFollowing().size()));
                }

            }

            @Override
            public void onError(String message) {

            }
        });
    }

}
