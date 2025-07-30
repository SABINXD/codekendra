package com.example.codekendra;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

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

    
    final String URL_PROFILE = "http://192.168.1.5/codekendra/api/get_profile_info.php";
    final String URL_UPLOAD  = "http://192.168.1.5/codekendra/api/update_profile.php";

    ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();

        toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.profile_nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            if (item.getItemId() == R.id.nav_logout) showLogoutDialog();
            else if (item.getItemId() == R.id.nav_account_center)
                startActivity(new Intent(this, AccountCenterActivity.class));
            return true;
        });
        profileName     = findViewById(R.id.profile_name);
        profileUsername = findViewById(R.id.profile_username);
        tvFollowers     = findViewById(R.id.tv_followers);
        tvFollowing     = findViewById(R.id.tv_following);
        profileBio      = findViewById(R.id.profile_bio);
        btnEditProfile  = findViewById(R.id.btn_follow_or_edit);
        profileImage    = findViewById(R.id.profile_image);
        recyclerPosts   = findViewById(R.id.recycler_posts);
        cropImageView       = findViewById(R.id.cropImageView);
        cropButtonsLayout   = findViewById(R.id.cropButtonsLayout);
        btnSaveCrop         = findViewById(R.id.btnSaveCrop);
        btnCancelCrop       = findViewById(R.id.btnCancelCrop);

        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileInfoActivity.class)));

        btnSaveCrop.setOnClickListener(v -> {
            Bitmap croppedBitmap = cropImageView.getCroppedImage();
            if (croppedBitmap != null) {
                Uri croppedUri = saveBitmapToCache(croppedBitmap);
                profileImage.setImageURI(croppedUri);
                uploadProfileImage(croppedUri);
                hideCropView();
            } else {
                Toast.makeText(this, "Failed to crop image", Toast.LENGTH_SHORT).show();
                Log.e("CROP", "Bitmap is null");
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

        fetchProfileDetails();
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

    private Uri saveBitmapToCache(Bitmap bitmap) {
        File cacheDir = getCacheDir();
        File file = new File(cacheDir, "cropped_profile.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (Exception e) {
            Log.e("CROP_SAVE", "Failed to save image", e);
        }
        return Uri.fromFile(file);
    }

    private void fetchProfileDetails() {
        StringRequest request = new StringRequest(Request.Method.POST, URL_PROFILE,
                response -> {
                    try {
                        Log.d("PROFILE_RESPONSE", response);
                        JSONObject obj = new JSONObject(response);

                        if (obj.optString("status").equalsIgnoreCase("success")) {
                            JSONObject user = obj.optJSONObject("user");
                            if (user != null) {
                                // Safely fetch and set text details
                                profileName.setText(user.optString("display_name", "Unnamed"));
                                profileUsername.setText("@" + user.optString("username", ""));
                                profileBio.setText(user.optString("bio", ""));
                                tvFollowers.setText(user.optString("followers", "0") + " Followers");
                                tvFollowing.setText(user.optString("following", "0") + " Following");

                                // Build image URL
                                String profileImageFilename = user.optString("profile_pic", "default_profile.jpg");
                                String imageUrl = "http://" + getString(R.string.server_ip) + "/codekendra/web/assets/img/profile/"
                                        + profileImageFilename + "?t=" + System.currentTimeMillis();

                                // Load circular profile image with cache busting
                                Glide.with(this)
                                        .load(imageUrl)
                                        .circleCrop()
                                        .placeholder(R.drawable.profile_placeholder)
                                        .error(R.drawable.profile_placeholder)
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .into(profileImage);
                            } else {
                                Log.e("PROFILE", "User object missing");
                            }
                        } else {
                            Log.e("PROFILE", "Backend response was not successful");
                        }
                    } catch (Exception e) {
                        Log.e("PROFILE", "JSON parse error", e);
                    }
                },
                error -> Log.e("PROFILE", "Network request failed", error)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", String.valueOf(currentUserId));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void uploadProfileImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            inputStream.close();
            byte[] imageBytes = buffer.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

            StringRequest request = new StringRequest(Request.Method.POST, URL_UPLOAD,
                    response -> {
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getString("status").equalsIgnoreCase("success")) {
                                String newFile = res.getString("filename");
                                String imageUrl = "http://" + getString(R.string.server_ip) + "/codekendra/web/assets/img/profile/" + newFile;

                                Glide.with(this)
                                        .load(imageUrl + "?ts=" + System.currentTimeMillis())
                                        .circleCrop()
                                        .placeholder(R.drawable.default_profile)
                                        .error(R.drawable.default_profile)
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .into(profileImage);

                                Toast.makeText(this, "Profile photo updated 🚀", Toast.LENGTH_SHORT).show();
                                fetchProfileDetails(); // Also refresh all profile info
                            } else {
                                Toast.makeText(this, "Upload failed ❌", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("UPLOAD", "Parse error", e);
                            Toast.makeText(this, "Upload response error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("UPLOAD", "Volley error", error);
                        Toast.makeText(this, "Network error during upload", Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("uid", String.valueOf(currentUserId));
                    params.put("profile_img", encodedImage);
                    return params;
                }
            };


            Volley.newRequestQueue(this).add(request);

        } catch (Exception e) {
            Log.e("UPLOAD", "Image encoding error", e);
            Toast.makeText(this, "Encoding error", Toast.LENGTH_SHORT).show();
        }
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
        fetchProfileDetails(); // Refreshes profile when coming back from edit screen
    }
    

}