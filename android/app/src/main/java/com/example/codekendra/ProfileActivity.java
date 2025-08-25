package com.example.codekendra;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.navigation.NavigationView;
import com.canhub.cropper.CropImageView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    // UI Components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView profileName, tvFollowers, tvFollowing, profileBio, profileUsername, postsLabel;
    private Button btnEditProfile, btnSaveCrop, btnCancelCrop;
    private ImageView profileImage;
    private RecyclerView profilePostsRecyclerView;
    private LinearLayout cropButtonsLayout, emptyPostsLayout;
    private CropImageView cropImageView;
    // Data
    private SessionManager sessionManager;
    private String serverIp;
    private String URL_PROFILE;
    private String URL_UPLOAD;
    private String URL_FOLLOW_STATS;
    private String URL_USER_POSTS;
    // Adapter
    private PostAdapter adapter;
    private List<Post> userPosts = new ArrayList<>();
    // Image handling
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeComponents();
        setupToolbarAndDrawer();
        setupClickListeners();
        setupProfilePostsRecyclerView();
        // Check session validity before fetching profile
        if (sessionManager.isSessionValid()) {
            fetchProfileDetails();
            fetchFollowStats();
            fetchUserPosts();
        } else {
            Log.e(TAG, "Invalid session - redirecting to login");
            redirectToLogin();
        }
    }

    private void initializeComponents() {
        sessionManager = new SessionManager(this);
        // Debug session data
        sessionManager.debugSession();
        serverIp = getString(R.string.server_ip);
        URL_PROFILE = "http://" + serverIp + "/codekendra/api/get_profile_info.php";
        URL_UPLOAD = "http://" + serverIp + "/codekendra/api/upload_profile_pic.php";
        URL_FOLLOW_STATS = "http://" + serverIp + "/codekendra/api/get_follow_stats.php";
        URL_USER_POSTS = "http://" + serverIp + "/codekendra/api/get_posts.php"; // Using same API as feed

        Log.d(TAG, "Profile URL: " + URL_PROFILE);
        Log.d(TAG, "Upload URL: " + URL_UPLOAD);
        Log.d(TAG, "Follow Stats URL: " + URL_FOLLOW_STATS);
        Log.d(TAG, "User Posts URL: " + URL_USER_POSTS);

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
        profilePostsRecyclerView = findViewById(R.id.profile_posts_recycler_view);
        postsLabel = findViewById(R.id.posts_label);
        emptyPostsLayout = findViewById(R.id.empty_posts_layout);
        cropImageView = findViewById(R.id.cropImageView);
        cropButtonsLayout = findViewById(R.id.cropButtonsLayout);
        btnSaveCrop = findViewById(R.id.btn_save_crop);
        btnCancelCrop = findViewById(R.id.btn_cancel_crop);

        // Add null check for RecyclerView
        if (profilePostsRecyclerView == null) {
            Log.e(TAG, "profilePostsRecyclerView is null! Check layout file.");
            Toast.makeText(this, "Error initializing posts view", Toast.LENGTH_SHORT).show();
        }
        if (emptyPostsLayout == null) {
            Log.e(TAG, "emptyPostsLayout is null! Check layout file.");
        }

        // Initially hide posts section until we load data
        if (postsLabel != null) postsLabel.setVisibility(View.GONE);
        if (profilePostsRecyclerView != null) profilePostsRecyclerView.setVisibility(View.GONE);
        if (emptyPostsLayout != null) emptyPostsLayout.setVisibility(View.GONE);
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
        if (btnSaveCrop != null) {
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
        }
        if (btnCancelCrop != null) {
            btnCancelCrop.setOnClickListener(v -> hideCropView());
        }
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handlePickedImage
        );
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });
    }

    private void setupProfilePostsRecyclerView() {
        if (profilePostsRecyclerView != null) {
            Log.d(TAG, "Setting up RecyclerView");
            // Use GridLayoutManager for 3 columns
            profilePostsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            // Use the same PostAdapter as in feed/homepage
            adapter = new PostAdapter(this, userPosts, serverIp, sessionManager.getUserId());
            profilePostsRecyclerView.setAdapter(adapter);
            // Set background color to make it visible
            profilePostsRecyclerView.setBackgroundColor(getResources().getColor(android.R.color.white));
            Log.d(TAG, "RecyclerView setup complete");
        } else {
            Log.e(TAG, "Cannot setup RecyclerView - it's null");
        }
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
        if (cropButtonsLayout != null) cropButtonsLayout.setVisibility(View.VISIBLE);
        profileImage.setVisibility(View.GONE);
    }

    private void hideCropView() {
        cropImageView.clearImage();
        cropImageView.setVisibility(View.GONE);
        if (cropButtonsLayout != null) cropButtonsLayout.setVisibility(View.GONE);
        profileImage.setVisibility(View.VISIBLE);
    }

    private void fetchProfileDetails() {
        String userIdString = sessionManager.getUserIdAsString();
        if (userIdString == null) {
            Log.e(TAG, "Cannot fetch profile - invalid user ID");
            Toast.makeText(this, "❌ Session error - please login again", Toast.LENGTH_LONG).show();
            redirectToLogin();
            return;
        }
        Log.d(TAG, "Fetching profile for user ID: " + userIdString);
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
                            if (message.contains("Missing or invalid user_id") ||
                                    message.contains("User not found")) {
                                Toast.makeText(this, "❌ Session expired - please login again", Toast.LENGTH_LONG).show();
                                redirectToLogin();
                            } else {
                                Toast.makeText(this, "❌ " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parse error", e);
                        Toast.makeText(this, "❌ Error parsing profile data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error", error);
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error status code: " + error.networkResponse.statusCode);
                        String errorBody = new String(error.networkResponse.data);
                        Log.e(TAG, "Error response: " + errorBody);
                    }
                    Toast.makeText(this, "❌ Network error loading profile", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String userId = sessionManager.getUserIdAsString();
                params.put("uid", userId);  // PHP expects 'uid' parameter
                Log.d(TAG, "Sending UID parameter: " + userId);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void fetchFollowStats() {
        String userIdString = sessionManager.getUserIdAsString();
        if (userIdString == null) {
            Log.e(TAG, "Cannot fetch follow stats - invalid user ID");
            return;
        }
        Log.d(TAG, "Fetching follow stats for user ID: " + userIdString);
        StringRequest request = new StringRequest(Request.Method.POST, URL_FOLLOW_STATS,
                response -> {
                    try {
                        Log.d(TAG, "Follow stats response: " + response);
                        JSONObject obj = new JSONObject(response);
                        if ("success".equals(obj.optString("status"))) {
                            int followers = obj.optInt("followers", 0);
                            int following = obj.optInt("following", 0);
                            tvFollowers.setText(followers + " Followers");
                            tvFollowing.setText(following + " Following");
                            Log.d(TAG, "Updated follow stats: " + followers + " followers, " + following + " following");
                        } else {
                            String message = obj.optString("message", "Unknown error");
                            Log.e(TAG, "Follow stats fetch failed: " + message);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parse error for follow stats", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Network error fetching follow stats", error);
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error status code: " + error.networkResponse.statusCode);
                        String errorBody = new String(error.networkResponse.data);
                        Log.e(TAG, "Error response: " + errorBody);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", userIdString);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void fetchUserPosts() {
        String userIdString = sessionManager.getUserIdAsString();
        if (userIdString == null) {
            Log.e(TAG, "Cannot fetch user posts - invalid user ID");
            return;
        }

        // Use the same API as feed but with current user ID
        String url = URL_USER_POSTS + "?user_id=" + userIdString;

        Log.d(TAG, "Fetching user posts for user ID: " + userIdString);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d(TAG, "User posts response: " + response);
                        if ("success".equals(response.getString("status"))) {
                            JSONArray postsArray = response.getJSONArray("posts");
                            userPosts.clear();
                            if (postsArray != null) {
                                Log.d(TAG, "Found " + postsArray.length() + " posts");
                                for (int i = 0; i < postsArray.length(); i++) {
                                    JSONObject postObj = postsArray.getJSONObject(i);
                                    Post post = createPostFromJson(postObj);
                                    userPosts.add(post);
                                    Log.d(TAG, "Added post " + i + ": " + post.getPostImage());
                                }
                            } else {
                                Log.d(TAG, "No posts found");
                            }
                            // Update UI based on posts
                            updatePostsUI();
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, "Updated adapter with " + userPosts.size() + " posts");
                            } else {
                                Log.e(TAG, "adapter is null");
                            }
                        } else {
                            String message = response.optString("message", "Unknown error");
                            Log.e(TAG, "User posts fetch failed: " + message);
                            Toast.makeText(this, "Failed to load posts: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parse error for user posts", e);
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading posts", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error fetching user posts", error);
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error status code: " + error.networkResponse.statusCode);
                        try {
                            String errorBody = new String(error.networkResponse.data);
                            Log.e(TAG, "Error response: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Could not parse error response", e);
                        }
                    }
                    Toast.makeText(this, "Network error loading posts", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void updatePostsUI() {
        if (userPosts.size() > 0) {
            // Show posts section
            if (postsLabel != null) postsLabel.setVisibility(View.VISIBLE);
            if (profilePostsRecyclerView != null) profilePostsRecyclerView.setVisibility(View.VISIBLE);
            if (emptyPostsLayout != null) emptyPostsLayout.setVisibility(View.GONE);
            Log.d(TAG, "Showing posts section with " + userPosts.size() + " posts");
        } else {
            // Show empty state
            if (postsLabel != null) postsLabel.setVisibility(View.VISIBLE);
            if (profilePostsRecyclerView != null) profilePostsRecyclerView.setVisibility(View.GONE);
            if (emptyPostsLayout != null) emptyPostsLayout.setVisibility(View.VISIBLE);
            Log.d(TAG, "Showing empty posts state");
        }
    }

    private Post createPostFromJson(JSONObject obj) throws Exception {
        Post post = new Post();
        post.setId(obj.getInt("id"));
        post.setUserId(obj.getInt("user_id"));
        // Handle post image
        String postImage = obj.optString("post_image", "");
        if (postImage != null && !postImage.isEmpty() && !postImage.equals("null")) {
            post.setPostImage(postImage);
        }
        // Handle post description
        String postDescription = obj.optString("post_description", "");
        if (postDescription != null && !postDescription.isEmpty() && !postDescription.equals("null")) {
            post.setPostDescription(postDescription);
        }
        // Handle code content
        String codeContent = obj.optString("code_content", "");
        if (codeContent != null && !codeContent.isEmpty() && !codeContent.equals("null")) {
            post.setCodeContent(codeContent);
        }
        // Handle code language
        String codeLanguage = obj.optString("code_language", "");
        if (codeLanguage != null && !codeLanguage.isEmpty() && !codeLanguage.equals("null")) {
            post.setCodeLanguage(codeLanguage);
        }
        post.setCreatedAt(obj.optString("created_at", ""));
        Log.d(TAG, "Created post: ID=" + post.getId() +
                ", Image=" + post.getPostImage() +
                ", Description=" + post.getPostDescription() +
                ", HasCode=" + post.hasCode());
        return post;
    }

    private void updateProfileUI(JSONObject user) {
        try {
            // Set text fields
            String displayName = user.optString("display_name", "Unknown User");
            String username = user.optString("username", "");
            String bio = user.optString("bio", "No bio available");
            profileName.setText(displayName);
            profileUsername.setText("@" + username);
            profileBio.setText(bio);
            // Load profile image
            String profilePic = user.optString("profile_pic", "default_profile.jpg");
            String imageUrl = "http://" + serverIp + "/codekendra/web/assets/img/profile/" + profilePic;
            Log.d(TAG, "Loading image from: " + imageUrl);
            // Load image with Glide
            Glide.with(this)
                    .load(imageUrl)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(profileImage);
            // Update session with new profile pic if different
            String currentProfilePic = sessionManager.getProfilePic();
            if (!profilePic.equals(currentProfilePic)) {
                sessionManager.updateProfilePic(profilePic);
                Log.d(TAG, "Updated session profile pic: " + profilePic);
            }
            Log.d(TAG, "✅ Profile UI updated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error updating profile UI", e);
        }
    }

    private void uploadProfileImage(Bitmap bitmap) {
        String userIdString = sessionManager.getUserIdAsString();
        if (userIdString == null) {
            Toast.makeText(this, "❌ Session error - cannot upload", Toast.LENGTH_LONG).show();
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        final byte[] imageBytes = byteArrayOutputStream.toByteArray();
        Log.d(TAG, "Starting image upload. Image size: " + imageBytes.length + " bytes");
        Log.d(TAG, "Upload URL: " + URL_UPLOAD);
        Log.d(TAG, "User ID: " + userIdString);
        MultipartRequest multipartRequest = new MultipartRequest(URL_UPLOAD,
                response -> {
                    Log.d(TAG, "Raw upload response: " + response);
                    try {
                        JSONObject res = new JSONObject(response);
                        if ("success".equals(res.getString("status"))) {
                            Toast.makeText(this, "✅ Profile photo updated!", Toast.LENGTH_SHORT).show();
                            fetchProfileDetails(); // Refresh profile
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
                String userId = sessionManager.getUserIdAsString();
                params.put("uid", userId);  // PHP expects 'uid' parameter
                Log.d(TAG, "Adding UID parameter: " + userId);
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

    private void redirectToLogin() {
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
        if (sessionManager.isSessionValid()) {
            fetchProfileDetails();
            fetchFollowStats();
            fetchUserPosts();
        } else {
            redirectToLogin();
        }
    }
}