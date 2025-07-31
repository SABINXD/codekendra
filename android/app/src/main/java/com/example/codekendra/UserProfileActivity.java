package com.example.codekendra;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = "UserProfile";

    private Toolbar toolbar;
    private ImageView profileImage;
    private TextView profileName, profileUsername, profileBio, tvFollowers, tvFollowing;
    private Button btnFollow;

    private int userId;
    private String username;
    private SessionManager sessionManager;
    private String serverIp;
    private String PROFILE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initializeComponents();
        setupToolbar();
        loadUserProfile();
    }

    private void initializeComponents() {
        sessionManager = new SessionManager(this);
        serverIp = getString(R.string.server_ip);

        // Get user data from intent
        userId = getIntent().getIntExtra("user_id", -1);
        username = getIntent().getStringExtra("username");

        PROFILE_URL = "http://" + serverIp + "/codekendra/api/get_profile_info.php";

        // Find views
        toolbar = findViewById(R.id.toolbar);
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        profileUsername = findViewById(R.id.profile_username);
        profileBio = findViewById(R.id.profile_bio);
        tvFollowers = findViewById(R.id.tv_followers);
        tvFollowing = findViewById(R.id.tv_following);
        btnFollow = findViewById(R.id.btn_follow);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("@" + username);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadUserProfile() {
        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, PROFILE_URL,
                response -> {
                    try {
                        Log.d(TAG, "Profile response: " + response);
                        JSONObject obj = new JSONObject(response);

                        if ("success".equals(obj.optString("status"))) {
                            JSONObject user = obj.optJSONObject("user");
                            if (user != null) {
                                updateProfileUI(user);
                            }
                        } else {
                            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing profile", e);
                        Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error", error);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", String.valueOf(userId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void updateProfileUI(JSONObject user) {
        try {
            String firstName = user.optString("first_name", "");
            String lastName = user.optString("last_name", "");
            String displayName = firstName + " " + lastName;
            String username = user.optString("username", "");
            String bio = user.optString("bio", "No bio available");
            String profilePic = user.optString("profile_pic", "default_profile.jpg");

            profileName.setText(displayName);
            profileUsername.setText("@" + username);
            profileBio.setText(bio);
            tvFollowers.setText("0 Followers");
            tvFollowing.setText("0 Following");

            // Load profile image
            String imageUrl = "http://" + serverIp + "/codekendra/web/assets/img/profile/" + profilePic;

            Glide.with(this)
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileImage);

            // Setup follow button
            btnFollow.setOnClickListener(v -> {
                Toast.makeText(this, "Follow feature coming soon! 🚀", Toast.LENGTH_SHORT).show();
            });

        } catch (Exception e) {
            Log.e(TAG, "Error updating UI", e);
        }
    }
}