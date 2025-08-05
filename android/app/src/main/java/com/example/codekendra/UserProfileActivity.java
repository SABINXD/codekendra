package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import de.hdodenhof.circleimageview.CircleImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = "UserProfileActivity";

    // UI Components
    private Toolbar toolbar;
    private CircleImageView profileImage;
    private ImageView profileImageRegular; // Fallback for regular ImageView
    private TextView profileName, profileUsername, profileBio, tvPostCount, tvFollowers, tvFollowing;
    private Button btnFollow;
    private RecyclerView recyclerPosts;
    private LinearLayout emptyPostsLayout;

    // User data
    private int targetUserId;
    private String username;
    private boolean isBlocked = false;
    private boolean isFollowing = false;
    private boolean isFollowRequestPending = false;
    private boolean isOwnProfile = false;

    // Session and server
    private SessionManager sessionManager;
    private String serverIp;
    private String PROFILE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initializeComponents();
        setupToolbar();
        setupClickListeners();
        loadUserProfile();

        // Only check follow/block status for other users, not own profile
        if (!isOwnProfile) {
            checkFollowStatus();
            checkBlockStatus();
        }
    }

    private void initializeComponents() {
        sessionManager = new SessionManager(this);
        serverIp = getString(R.string.server_ip);

        // Get user data from intent
        targetUserId = getIntent().getIntExtra("user_id", -1);
        username = getIntent().getStringExtra("username");

        // Check if this is the user's own profile
        int currentUserId = sessionManager.getUserId();
        isOwnProfile = (targetUserId == currentUserId);

        Log.d(TAG, "Current user ID: " + currentUserId + ", Target user ID: " + targetUserId + ", Own profile: " + isOwnProfile);

        PROFILE_URL = "http://" + serverIp + "/codekendra/api/get_user_profile.php";

        // Find views
        toolbar = findViewById(R.id.toolbar);

        // FIXED: Handle both CircleImageView and regular ImageView
        View profileImageView = findViewById(R.id.profile_image);
        try {
            if (profileImageView instanceof de.hdodenhof.circleimageview.CircleImageView) {
                profileImage = (CircleImageView) profileImageView;
                profileImageRegular = null;
            } else {
                profileImage = null;
                profileImageRegular = (ImageView) profileImageView;
            }
        } catch (ClassCastException e) {
            Log.w(TAG, "Profile image casting issue, using regular ImageView");
            profileImage = null;
            profileImageRegular = (ImageView) profileImageView;
        }

        profileName = findViewById(R.id.profile_name);
        profileUsername = findViewById(R.id.profile_username);
        profileBio = findViewById(R.id.profile_bio);
        tvPostCount = findViewById(R.id.tv_post_count);
        tvFollowers = findViewById(R.id.tv_followers);
        tvFollowing = findViewById(R.id.tv_following);
        btnFollow = findViewById(R.id.btn_follow);
        recyclerPosts = findViewById(R.id.recycler_posts);
        emptyPostsLayout = findViewById(R.id.empty_posts_layout);

        // Hide follow button immediately if it's own profile
        if (isOwnProfile) {
            btnFollow.setVisibility(View.GONE);
            Log.d(TAG, "Hidden follow button for own profile");
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("@" + username);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        btnFollow.setOnClickListener(v -> {
            if (isOwnProfile) {
                Toast.makeText(this, "❌ Cannot follow yourself", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isBlocked) {
                Toast.makeText(this, "❌ Cannot follow blocked user", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isFollowing) {
                unfollowUser();
            } else if (isFollowRequestPending) {
                Toast.makeText(this, "⏳ Follow request already sent", Toast.LENGTH_SHORT).show();
            } else {
                followUser();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isOwnProfile) {
            getMenuInflater().inflate(R.menu.menu_user_profile_own, menu);
        } else if (isBlocked) {
            getMenuInflater().inflate(R.menu.menu_user_profile_blocked, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_user_profile_normal, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_message) {
            Toast.makeText(this, "💬 Messaging feature coming soon", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_block) {
            blockUser();
            return true;
        } else if (id == R.id.menu_unblock) {
            unblockUser();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserProfile() {
        if (targetUserId == -1) {
            Toast.makeText(this, "❌ Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String url = PROFILE_URL + "?user_id=" + targetUserId;
        Log.d(TAG, "Loading profile from: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "User profile response: " + response.toString());
                    parseUserProfileResponse(response);
                },
                error -> {
                    Log.e(TAG, "Error loading user profile: " + error.toString());
                    Toast.makeText(this, "❌ Failed to load profile", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void parseUserProfileResponse(JSONObject response) {
        try {
            if ("success".equals(response.getString("status"))) {
                JSONObject user = response.getJSONObject("user");

                String firstName = user.optString("first_name", "");
                String lastName = user.optString("last_name", "");
                String displayName = firstName + " " + lastName;
                String username = user.optString("username", "");
                String bio = user.optString("bio", "No bio available");
                String profilePic = user.optString("profile_pic", "default_profile.jpg");

                int postCount = user.optInt("post_count", 0);
                int followers = user.optInt("followers", 0);
                int following = user.optInt("following", 0);

                // Update UI
                profileName.setText(displayName.trim().isEmpty() ? username : displayName);
                profileUsername.setText("@" + username);
                profileBio.setText(bio);
                tvPostCount.setText(String.valueOf(postCount));
                tvFollowers.setText(String.valueOf(followers));
                tvFollowing.setText(String.valueOf(following));

                // FIXED: Load profile image for both CircleImageView and regular ImageView
                if (!isBlocked) {
                    String imageUrl = "http://" + serverIp + "/codekendra/web/assets/img/profile/" + profilePic;

                    if (profileImage != null) {
                        // Use CircleImageView
                        Glide.with(this)
                                .load(imageUrl)
                                .circleCrop()
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.default_profile)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(profileImage);
                    } else if (profileImageRegular != null) {
                        // Use regular ImageView
                        Glide.with(this)
                                .load(imageUrl)
                                .circleCrop()
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.default_profile)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(profileImageRegular);
                    }
                } else {
                    // Set default image for blocked users
                    if (profileImage != null) {
                        profileImage.setImageResource(R.drawable.default_profile);
                    } else if (profileImageRegular != null) {
                        profileImageRegular.setImageResource(R.drawable.default_profile);
                    }
                }

            } else {
                String errorMsg = response.optString("message", "Unknown error");
                Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing user profile response", e);
            Toast.makeText(this, "❌ Error parsing profile data", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkFollowStatus() {
        if (isOwnProfile) {
            Log.d(TAG, "Skipping follow status check for own profile");
            return;
        }

        String url = "http://" + serverIp + "/codekendra/api/check_follow_status.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Follow status response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("status")) {
                            String followStatus = jsonResponse.optString("follow_status", "none");
                            updateFollowButton(followStatus);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing follow status", e);
                    }
                },
                error -> Log.e(TAG, "Error checking follow status: " + error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("follower_id", String.valueOf(sessionManager.getUserId()));
                params.put("following_id", String.valueOf(targetUserId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void checkBlockStatus() {
        if (isOwnProfile) {
            Log.d(TAG, "Skipping block status check for own profile");
            return;
        }

        String url = "http://" + serverIp + "/codekendra/api/check_block_status.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Block status response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("status")) {
                            isBlocked = jsonResponse.getBoolean("is_blocked");
                            updateUIForBlockStatus();
                            invalidateOptionsMenu();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing block status", e);
                    }
                },
                error -> Log.e(TAG, "Error checking block status: " + error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("blocker_id", String.valueOf(sessionManager.getUserId()));
                params.put("blocked_id", String.valueOf(targetUserId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void updateFollowButton(String followStatus) {
        if (isOwnProfile) {
            btnFollow.setVisibility(View.GONE);
            Log.d(TAG, "Follow button hidden for own profile");
            return;
        }

        switch (followStatus) {
            case "accepted":
                isFollowing = true;
                isFollowRequestPending = false;
                btnFollow.setText("Following");
                btnFollow.setBackgroundResource(R.drawable.button_following);
                btnFollow.setEnabled(true);
                btnFollow.setVisibility(View.VISIBLE);
                break;
            case "pending":
                isFollowing = false;
                isFollowRequestPending = true;
                btnFollow.setText("Requested");
                btnFollow.setBackgroundResource(R.drawable.button_follow);
                btnFollow.setEnabled(false);
                btnFollow.setVisibility(View.VISIBLE);
                break;
            default:
                isFollowing = false;
                isFollowRequestPending = false;
                btnFollow.setText("Follow");
                btnFollow.setBackgroundResource(R.drawable.button_follow);
                btnFollow.setEnabled(true);
                btnFollow.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateUIForBlockStatus() {
        if (isBlocked) {
            profileName.setText("Blocked User");
            profileBio.setText("This user has been blocked");

            if (profileImage != null) {
                profileImage.setImageResource(R.drawable.default_profile);
            } else if (profileImageRegular != null) {
                profileImageRegular.setImageResource(R.drawable.default_profile);
            }

            btnFollow.setVisibility(View.GONE);
            recyclerPosts.setVisibility(View.GONE);
            emptyPostsLayout.setVisibility(View.VISIBLE);
        } else {
            // Only show follow button if not own profile
            if (!isOwnProfile) {
                btnFollow.setVisibility(View.VISIBLE);
            }
            recyclerPosts.setVisibility(View.VISIBLE);
            emptyPostsLayout.setVisibility(View.GONE);
        }
    }

    private void followUser() {
        if (isOwnProfile) {
            Toast.makeText(this, "❌ Cannot follow yourself", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://" + serverIp + "/codekendra/api/follow_user.php";
        btnFollow.setEnabled(false);
        btnFollow.setText("Sending...");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Follow user response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("status")) {
                            Toast.makeText(this, "✅ Follow request sent", Toast.LENGTH_SHORT).show();
                            isFollowRequestPending = true;
                            btnFollow.setText("Requested");
                            btnFollow.setEnabled(false);
                        } else {
                            String errorMsg = jsonResponse.optString("error", "Unknown error");
                            Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_SHORT).show();
                            btnFollow.setEnabled(true);
                            btnFollow.setText("Follow");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing follow response", e);
                        btnFollow.setEnabled(true);
                        btnFollow.setText("Follow");
                    }
                },
                error -> {
                    Log.e(TAG, "Error following user: " + error.toString());
                    Toast.makeText(this, "❌ Network error", Toast.LENGTH_SHORT).show();
                    btnFollow.setEnabled(true);
                    btnFollow.setText("Follow");
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("follower_id", String.valueOf(sessionManager.getUserId()));
                params.put("following_id", String.valueOf(targetUserId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void unfollowUser() {
        String url = "http://" + serverIp + "/codekendra/api/unfollow_user.php";
        btnFollow.setEnabled(false);
        btnFollow.setText("Unfollowing...");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Unfollow user response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("status")) {
                            Toast.makeText(this, "✅ Unfollowed successfully", Toast.LENGTH_SHORT).show();
                            isFollowing = false;
                            btnFollow.setText("Follow");
                            btnFollow.setEnabled(true);
                            btnFollow.setBackgroundResource(R.drawable.button_follow);
                        } else {
                            String errorMsg = jsonResponse.optString("error", "Unknown error");
                            Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_SHORT).show();
                            btnFollow.setEnabled(true);
                            btnFollow.setText("Following");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing unfollow response", e);
                        btnFollow.setEnabled(true);
                        btnFollow.setText("Following");
                    }
                },
                error -> {
                    Log.e(TAG, "Error unfollowing user: " + error.toString());
                    Toast.makeText(this, "❌ Network error", Toast.LENGTH_SHORT).show();
                    btnFollow.setEnabled(true);
                    btnFollow.setText("Following");
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("follower_id", String.valueOf(sessionManager.getUserId()));
                params.put("following_id", String.valueOf(targetUserId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void blockUser() {
        String url = "http://" + serverIp + "/codekendra/api/block_user.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Block user response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("status")) {
                            Toast.makeText(this, "✅ User blocked successfully", Toast.LENGTH_SHORT).show();
                            isBlocked = true;
                            isFollowing = false;
                            updateUIForBlockStatus();
                            invalidateOptionsMenu();
                        } else {
                            String errorMsg = jsonResponse.optString("error", "Unknown error");
                            Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing block response", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Error blocking user: " + error.toString());
                    Toast.makeText(this, "❌ Network error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("blocker_id", String.valueOf(sessionManager.getUserId()));
                params.put("blocked_id", String.valueOf(targetUserId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void unblockUser() {
        String url = "http://" + serverIp + "/codekendra/api/unblock_user.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Unblock user response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("status")) {
                            Toast.makeText(this, "✅ User unblocked successfully", Toast.LENGTH_SHORT).show();
                            isBlocked = false;
                            updateUIForBlockStatus();
                            invalidateOptionsMenu();
                            loadUserProfile();
                            checkFollowStatus();
                        } else {
                            String errorMsg = jsonResponse.optString("error", "Unknown error");
                            Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing unblock response", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Error unblocking user: " + error.toString());
                    Toast.makeText(this, "❌ Network error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("blocker_id", String.valueOf(sessionManager.getUserId()));
                params.put("blocked_id", String.valueOf(targetUserId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
