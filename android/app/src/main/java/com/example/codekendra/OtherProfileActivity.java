package com.example.codekendra;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.codekendra.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OtherProfileActivity extends AppCompatActivity {

    TextView userName, followersView, followingView, bioView;
    Button btnFollowUser;
    ImageView userImage;
    RecyclerView postGrid;

    String viewedUserId = "134"; // Pass dynamically based on profile being viewed
    String currentUserId = "133"; // Logged-in user ID
    final String URL_TOGGLE = "https://yourdomain.com/follow_toggle.php";
    final String URL_STATS = "https://yourdomain.com/get_follow_stats.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = findViewById(R.id.user_profile_name);
        followersView = findViewById(R.id.user_followers);
        followingView = findViewById(R.id.user_following);
        bioView = findViewById(R.id.user_profile_bio);
        btnFollowUser = findViewById(R.id.btn_follow_user);
        userImage = findViewById(R.id.user_profile_image);
        postGrid = findViewById(R.id.user_recycler_posts);

        fetchFollowStats();

        btnFollowUser.setOnClickListener(v -> toggleFollow());
    }

    private void toggleFollow() {
        StringRequest request = new StringRequest(Request.Method.POST, URL_TOGGLE,
                response -> {
                    if (response.contains("followed")) {
                        btnFollowUser.setText("UNFOLLOW");
                    } else {
                        btnFollowUser.setText("FOLLOW");
                    }
                    fetchFollowStats();
                },
                error -> Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("follower_id", currentUserId);
                map.put("user_id", viewedUserId);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void fetchFollowStats() {
        StringRequest request = new StringRequest(Request.Method.POST, URL_STATS,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        followersView.setText(obj.getString("followers") + " Followers");
                        followingView.setText(obj.getString("following") + " Following");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to load stats", Toast.LENGTH_SHORT).show()
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("uid", viewedUserId);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
