package com.example.codekendra;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.ActionBarDrawerToggle;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.navigation.NavigationView;
import com.canhub.cropper.CropImageView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView profileName, tvFollowers, tvFollowing, profileBio, profileUsername;
    Button btnEditProfile, btnSaveCrop, btnCancelCrop;
    ImageView profileImage;
    RecyclerView recyclerPosts;
    LinearLayout cropButtonsLayout;
    CropImageView cropImageView;

    SessionManager sessionManager;
    int currentUserId;
    String serverIp;
    String URL_PROFILE;
    String URL_UPLOAD;

    ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeComponents();
        setupToolbarAndDrawer();
        setupClickListeners();
        fetchProfileDetails();
    }

    private void initializeComponents() {
        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();
        Log.d("ProfileActivity", "Current User ID: " + currentUserId);

        serverIp = getString(R.string.server_ip);
        URL_PROFILE = "http://" + serverIp + "/codekendra/api/get_profile_info.php";
        URL_UPLOAD = "http://" + serverIp + "/codekendra/api/upload_profile_pic.php";

        Log.d(TAG, "Upload URL: " + URL_UPLOAD);

        // Find views
        toolbar = findViewById(R.id.profile_toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.profile_nav_view);
        profileName = findViewById(R.id.profile_name);
        profileUsername = findViewById(R.id.profile_username);
        tvFollowers = findViewById(R.id.tv_followers);
        tvFollowing = findViewById(R.id.tv_following);
        profileBio = findViewById(R.id.profile_bio);
        btnEditProfile = findViewById(R.id.btn_follow_or_edit);
        profileImage = findViewById(R.id.profile_image);
        recyclerPosts = findViewById(R.id.recycler_posts);
        cropImageView = findViewById(R.id.cropImageView);
        cropButtonsLayout = findViewById(R.id.cropButtonsLayout);
        btnSaveCrop = findViewById(R.id.btnSaveCrop);
        btnCancelCrop = findViewById(R.id.btnCancelCrop);
    }

    private void setupToolbarAndDrawer() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            if (item.getItemId() == R.id.nav_logout) {
                showLogoutDialog();
            } else if (item.getItemId() == R.id.nav_account_center) {
                startActivity(new Intent(this, AccountCenterActivity.class));
            }
            return true;
        });
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileInfoActivity.class)));

        btnSaveCrop.setOnClickListener(v -> {
            Bitmap croppedBitmap = cropImageView.getCroppedImage();
            if (croppedBitmap != null) {
                uploadProfileImage(croppedBitmap);
                hideCropView();
            } else {
                Toast.makeText(this, "Failed to crop image", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Cropped bitmap is null");
            }
        });

        btnCancelCrop.setOnClickListener(v -> hideCropView());

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handlePickedImage
        );

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });
    }

    private void handlePickedImage(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri pickedImageUri = result.getData().getData();
            showCropView(pickedImageUri);
        }
    }

    private void showCropView(Uri imageUri) {
        cropImageView.setImageUriAsync(imageUri);
        cropImageView.setVisibility(View.VISIBLE);
        cropButtonsLayout.setVisibility(View.VISIBLE);
        profileImage.setVisibility(View.GONE);
    }

    private void hideCropView() {
        cropImageView.clearImage();
        cropImageView.setVisibility(View.GONE);
        cropButtonsLayout.setVisibility(View.GONE);
        profileImage.setVisibility(View.VISIBLE);
    }

    private void fetchProfileDetails() {
        Log.d("ProfileActivity", "Fetching profile for user ID: " + currentUserId);
        Log.d(TAG, "Fetching profile for user ID: " + currentUserId);

        StringRequest request = new StringRequest(Request.Method.POST, URL_PROFILE,
                response -> {
                    try {
                        Log.d(TAG, "Profile response: " + response);
                        JSONObject obj = new JSONObject(response);
                        if ("success".equals(obj.optString("status"))) {
                            JSONObject user = obj.optJSONObject("user");
                            if (user != null) {
                                updateProfileUI(user);
                            } else {
                                Log.e(TAG, "User object is null");
                                Toast.makeText(this, "❌ User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String message = obj.optString("message", "Unknown error");
                            Log.e(TAG, "Profile fetch failed: " + message);
                            Toast.makeText(this, "❌ " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parse error", e);
                        Toast.makeText(this, "❌ Error parsing profile data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error", error);
                    Toast.makeText(this, "❌ Network error loading profile", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", String.valueOf(currentUserId));
                Log.d(TAG, "Sending UID: " + currentUserId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void updateProfileUI(JSONObject user) {
        try {
            // Set text fields
            String displayName = user.optString("display_name", "Unknown User");
            String username = user.optString("username", "");
            String bio = user.optString("bio", "No bio available");
            String followers = user.optString("followers", "0");
            String following = user.optString("following", "0");

            profileName.setText(displayName);
            profileUsername.setText("@" + username);
            profileBio.setText(bio);
            tvFollowers.setText(followers + " Followers");
            tvFollowing.setText(following + " Following");

            // Load profile image - FIXED VERSION
            String profilePic = user.optString("profile_pic", "default_profile.jpg");
            String imageUrl = "http://" + serverIp + "/codekendra/web/assets/img/profile/" + profilePic;
            Log.d(TAG, "Loading image from: " + imageUrl);

            // Load image with proper caching and no visible placeholder
            Glide.with(this)
                    .load(imageUrl)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .error(R.drawable.default_profile)
                    .into(profileImage);

            Log.d(TAG, "✅ Profile UI updated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error updating profile UI", e);
        }
    }

    private void uploadProfileImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        final byte[] imageBytes = byteArrayOutputStream.toByteArray();

        Log.d(TAG, "Starting image upload. Image size: " + imageBytes.length + " bytes");
        Log.d(TAG, "Upload URL: " + URL_UPLOAD);
        Log.d(TAG, "User ID: " + currentUserId);

        MultipartRequest multipartRequest = new MultipartRequest(URL_UPLOAD,
                response -> {
                    Log.d(TAG, "Raw upload response: " + response);

                    try {
                        JSONObject res = new JSONObject(response);
                        if ("success".equals(res.getString("status"))) {
                            Toast.makeText(this, "✅ Profile photo updated!", Toast.LENGTH_SHORT).show();
                            fetchProfileDetails();
                        } else {
                            String message = res.optString("message", "Upload failed");
                            Log.e(TAG, "Upload failed: " + message);
                            Toast.makeText(this, "❌ " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Upload response parse error", e);
                        Log.e(TAG, "Response was: " + response);
                        Toast.makeText(this, "❌ Upload response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Upload network error", error);
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error status code: " + error.networkResponse.statusCode);
                        try {
                            String errorBody = new String(error.networkResponse.data, "UTF-8");
                            Log.e(TAG, "Error response body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Could not parse error response", e);
                        }
                    }
                    Toast.makeText(this, "❌ Network error during upload", Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", String.valueOf(currentUserId));
                Log.d(TAG, "Adding UID parameter: " + currentUserId);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                String fileName = "profile_pic_" + System.currentTimeMillis() + ".jpg";
                params.put("profile_pic", new DataPart(fileName, imageBytes, "image/jpeg"));
                Log.d(TAG, "Adding file parameter: " + fileName + " (" + imageBytes.length + " bytes)");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(multipartRequest);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logging Out")
                .setMessage("Disconnecting from Code Kendra. See you on the next commit 🚀")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProfileDetails();
    }
}