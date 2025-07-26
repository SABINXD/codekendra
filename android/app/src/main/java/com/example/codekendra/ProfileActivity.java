package com.example.codekendra;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.ActionBarDrawerToggle;

import com.google.android.material.navigation.NavigationView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    TextView profileName, tvFollowers, tvFollowing, profileBio;
    Button btnEditProfile;
    ImageView profileImage;
    RecyclerView recyclerPosts;

    SessionManager sessionManager;
    int currentUserId;
    final String URL_STATS = "http://192.168.1.3/codekendra/api/get_follow_stats.php";

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
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawers();

            if (id == R.id.nav_logout) {
                showLogoutDialog();
            } else if (id == R.id.nav_account_center) {
                startActivity(new Intent(this, AccountCenterActivity.class));
            }
            return true;
        });

        // UI binding
        profileName    = findViewById(R.id.profile_name);
        tvFollowers    = findViewById(R.id.tv_followers);
        tvFollowing    = findViewById(R.id.tv_following);
        profileBio     = findViewById(R.id.profile_bio);
        btnEditProfile = findViewById(R.id.btn_follow_or_edit);
        recyclerPosts  = findViewById(R.id.recycler_posts);

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileInfoActivity.class));
        });

        fetchFollowStats();
    }

    private void fetchFollowStats() {
        StringRequest request = new StringRequest(Request.Method.POST, URL_STATS,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        tvFollowers.setText(obj.getString("followers") + " Followers");
                        tvFollowing.setText(obj.getString("following") + " Following");
                    } catch (Exception e) {
                        Log.e("PROFILE_STATS", "Parse error", e);
                    }
                },
                error -> Log.e("PROFILE_STATS", "Request failed", error)
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
}
