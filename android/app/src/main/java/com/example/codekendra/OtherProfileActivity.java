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
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OtherProfileActivity extends AppCompatActivity {

    TextView userName, followersView, followingView, bioView;
    Button btnFollowOrMessage;
    ImageView userImage;
    RecyclerView postGrid;

    String viewedUserId = "134"; // Will later be passed from Intent
    String currentUserId = "133"; // Logged-in user ID from SessionManager

    String serverIp = getString(R.string.server_ip);
 
    final String URL_TOGGLE = "http://"+serverIp+"/codekendra/api/follow_toggle.php";
    final String URL_STATS = "http://"+serverIp+"/codekendra/api/get_follow_stats.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName          = findViewById(R.id.profile_name);
        followersView     = findViewById(R.id.tv_followers);
        followingView     = findViewById(R.id.tv_following);
        bioView           = findViewById(R.id.profile_bio);
        btnFollowOrMessage= findViewById(R.id.btn_follow_or_message);
        userImage         = findViewById(R.id.user_profile_image);
        postGrid          = findViewById(R.id.recycler_posts);

        fetchFollowStats();

        btnFollowOrMessage.setOnClickListener(v -> {
            // 🔄 Simulate follow state toggle
            if (btnFollowOrMessage.getText().toString().equalsIgnoreCase("Follow")) {
                btnFollowOrMessage.setText("Requested");
                btnFollowOrMessage.setBackgroundTintList(getColorStateList(R.color.tech_grey));
                btnFollowOrMessage.setEnabled(false);
            }
            // If real follow request accepted:
            // btnFollowOrMessage.setText("Message");
            // btnFollowOrMessage.setBackgroundTintList(getColorStateList(R.color.tech_blue));
        });
    }

    private void fetchFollowStats() {
        StringRequest request = new StringRequest(Request.Method.POST, URL_STATS,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        followersView.setText(obj.getString("followers"));
                        followingView.setText(obj.getString("following"));

                        // Simulate dynamic name/bio/image if available
                        userName.setText("CoderX");
                        bioView.setText("Tech explorer & backend whisperer 🛠️");
                        Glide.with(this)
                                .load("http://192.168.1.3/codekendra/uploads/profile/profile_134.jpg")
                                .placeholder(R.drawable.profile_placeholder)
                                .into(userImage);
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
