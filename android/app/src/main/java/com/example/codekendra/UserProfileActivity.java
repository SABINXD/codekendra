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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = "UserProfileActivity";
    // UI Components
    private Toolbar toolbar;
    private ImageView profileImage;
    private TextView profileName, profileUsername, profileBio, tvPostCount, tvFollowers, tvFollowing;
    private Button btnFollow, btnMessage;
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
    private String USER_POSTS_URL;
    // Posts data
    private List<Post> userPosts = new ArrayList<>();
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initializeComponents();
        setupToolbar();
        setupRecyclerView();
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
        USER_POSTS_URL = "http://" + serverIp + "/codekendra/api/get_posts.php"; // Using same API as feed

        // Find views
        toolbar = findViewById(R.id.toolbar);
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        profileUsername = findViewById(R.id.profile_username);
        profileBio = findViewById(R.id.profile_bio);
        tvPostCount = findViewById(R.id.tv_post_count);
        tvFollowers = findViewById(R.id.tv_followers);
        tvFollowing = findViewById(R.id.tv_following);
        btnFollow = findViewById(R.id.btn_follow);
        btnMessage = findViewById(R.id.btn_message);
        recyclerPosts = findViewById(R.id.recycler_posts);
        emptyPostsLayout = findViewById(R.id.empty_posts_layout);

        // Hide buttons immediately if it's own profile
        if (isOwnProfile) {
            btnFollow.setVisibility(View.GONE);
            btnMessage.setVisibility(View.GONE);
            Log.d(TAG, "Hidden buttons for own profile");
        }
    }

    private void setupRecyclerView() {
        // Set up RecyclerView with GridLayoutManager for 3 columns
        recyclerPosts.setLayoutManager(new GridLayoutManager(this, 3));
        // Use the same PostAdapter as in feed/homepage
        adapter = new PostAdapter(this, userPosts, serverIp, sessionManager.getUserId());
        recyclerPosts.setAdapter(adapter);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("@" + username);
        }
        // Set up the back button click listener
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
        btnMessage.setOnClickListener(v -> {
            // Open chat with this user
            Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
            intent.putExtra("RECIPIENT_ID", targetUserId); // Pass the target user ID
            startActivity(intent);
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
        if (id == R.id.menu_block) {
            blockUser();
            return true;
        } else if (id == R.id.menu_unblock) {
            unblockUser();
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
                    // Load user posts after profile is loaded
                    loadUserPosts();
                },
                error -> {
                    Log.e(TAG, "Error loading user profile: " + error.toString());
                    Toast.makeText(this, "❌ Failed to load profile", Toast.LENGTH_SHORT).show();
                });
        Volley.newRequestQueue(this).add(request);
    }

    private void loadUserPosts() {
        if (isBlocked) {
            // Don't load posts if user is blocked
            return;
        }

        // Use the same API as feed but with target user ID
        String url = USER_POSTS_URL + "?user_id=" + targetUserId;

        Log.d(TAG, "Loading posts for user ID: " + targetUserId);
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
                            }
                        } else {
                            String message = response.optString("message", "Unknown error");
                            Log.e(TAG, "User posts fetch failed: " + message);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parse error for user posts", e);
                        e.printStackTrace();
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
                });

        Volley.newRequestQueue(this).add(request);
    }

    private Post createPostFromJson(JSONObject obj) throws Exception {
        Post post = new Post();
        post.setId(obj.getInt("id"));
        post.setUserId(obj.getInt("user_id"));
        post.setUserName(obj.optString("user_name", ""));
        post.setProfilePic(obj.optString("profile_pic", ""));
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

    private void updatePostsUI() {
        if (userPosts.size() > 0) {
            // Show posts section
            recyclerPosts.setVisibility(View.VISIBLE);
            emptyPostsLayout.setVisibility(View.GONE);
            Log.d(TAG, "Showing posts section with " + userPosts.size() + " posts");
        } else {
            // Show empty state
            recyclerPosts.setVisibility(View.GONE);
            emptyPostsLayout.setVisibility(View.VISIBLE);
            Log.d(TAG, "Showing empty posts state");
        }
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
                // Load profile image
                if (!isBlocked) {
                    String imageUrl = "http://" + serverIp + "/codekendra/web/assets/img/profile/" + profilePic;
                    Glide.with(this)
                            .load(imageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.default_profile)
                            .error(R.drawable.default_profile)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(profileImage);
                } else {
                    // Set default image for blocked users
                    profileImage.setImageResource(R.drawable.default_profile);
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
                btnMessage.setVisibility(View.VISIBLE);
                break;
            case "pending":
                isFollowing = false;
                isFollowRequestPending = true;
                btnFollow.setText("Requested");
                btnFollow.setBackgroundResource(R.drawable.button_follow);
                btnFollow.setEnabled(false);
                btnFollow.setVisibility(View.VISIBLE);
                btnMessage.setVisibility(View.GONE);
                break;
            default:
                isFollowing = false;
                isFollowRequestPending = false;
                btnFollow.setText("Follow");
                btnFollow.setBackgroundResource(R.drawable.button_follow);
                btnFollow.setEnabled(true);
                btnFollow.setVisibility(View.VISIBLE);
                btnMessage.setVisibility(View.GONE);
                break;
        }
    }

    private void updateUIForBlockStatus() {
        if (isBlocked) {
            profileName.setText("Blocked User");
            profileBio.setText("This user has been blocked");
            profileImage.setImageResource(R.drawable.default_profile);
            btnFollow.setVisibility(View.GONE);
            btnMessage.setVisibility(View.GONE);
            recyclerPosts.setVisibility(View.GONE);
            emptyPostsLayout.setVisibility(View.VISIBLE);
        } else {
            // Only show buttons if not own profile
            if (!isOwnProfile) {
                btnFollow.setVisibility(View.VISIBLE);
                if (isFollowing) {
                    btnMessage.setVisibility(View.VISIBLE);
                }
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
        btnFollow.setText("Following...");
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Follow user response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("status")) {
                            Toast.makeText(this, "✅ Followed successfully", Toast.LENGTH_SHORT).show();
                            isFollowing = true;
                            isFollowRequestPending = false;
                            btnFollow.setText("Following");
                            btnFollow.setBackgroundResource(R.drawable.button_following);
                            btnFollow.setEnabled(true);
                            btnMessage.setVisibility(View.VISIBLE);
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
                            btnFollow.setBackgroundResource(R.drawable.button_follow);
                            btnFollow.setEnabled(true);
                            btnMessage.setVisibility(View.GONE);
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