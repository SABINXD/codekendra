package com.example.codekendra;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private static final String TAG = "NotificationsActivity";

    private RecyclerView notificationsRecycler;
    private LinearLayout emptyNotificationsLayout;
    private NotificationsAdapter adapter;
    private List<Notification> notificationsList = new ArrayList<>();
    private SessionManager sessionManager;
    private String serverIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadNotifications();
    }

    private void initializeViews() {
        sessionManager = new SessionManager(this);
        serverIp = getString(R.string.server_ip);

        notificationsRecycler = findViewById(R.id.notifications_recycler);
        emptyNotificationsLayout = findViewById(R.id.empty_notifications_layout);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Notifications");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new NotificationsAdapter(this, notificationsList, this::handleFollowRequest);
        notificationsRecycler.setLayoutManager(new LinearLayoutManager(this));
        notificationsRecycler.setAdapter(adapter);
    }

    private void loadNotifications() {
        String url = "http://" + serverIp + "/codekendra/api/get_notifications.php?user_id=" + sessionManager.getUserId();
        Log.d(TAG, "Loading notifications from: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Raw notifications response: " + response);
                        parseNotificationsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error loading notifications: " + error.toString());
                        Toast.makeText(NotificationsActivity.this, "❌ Failed to load notifications", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void parseNotificationsResponse(String response) {
        try {
            // Clean response to remove any HTML/PHP errors
            String cleanResponse = response.trim();
            if (cleanResponse.startsWith("<")) {
                // Response contains HTML/PHP errors
                Log.e(TAG, "Server returned HTML instead of JSON: " + cleanResponse);
                Toast.makeText(this, "❌ Server configuration error", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject jsonResponse = new JSONObject(cleanResponse);

            if ("success".equals(jsonResponse.getString("status"))) {
                JSONArray notificationsArray = jsonResponse.getJSONArray("notifications");
                notificationsList.clear();

                for (int i = 0; i < notificationsArray.length(); i++) {
                    JSONObject notificationObj = notificationsArray.getJSONObject(i);

                    Notification notification = new Notification();
                    notification.setId(notificationObj.getInt("id"));
                    notification.setFromUserId(notificationObj.getInt("from_user_id"));
                    notification.setMessage(notificationObj.getString("message"));
                    notification.setType(notificationObj.optString("type", "general"));
                    notification.setReadStatus(notificationObj.getInt("read_status"));
                    notification.setCreatedAt(notificationObj.getString("created_at"));
                    notification.setUserName(notificationObj.getString("user_name"));
                    notification.setProfilePic(notificationObj.optString("profile_pic", null));

                    notificationsList.add(notification);
                }

                adapter.notifyDataSetChanged();

                // Show/hide empty state
                if (notificationsList.isEmpty()) {
                    notificationsRecycler.setVisibility(View.GONE);
                    emptyNotificationsLayout.setVisibility(View.VISIBLE);
                } else {
                    notificationsRecycler.setVisibility(View.VISIBLE);
                    emptyNotificationsLayout.setVisibility(View.GONE);
                }

                Log.d(TAG, "✅ Loaded " + notificationsList.size() + " notifications");
            } else {
                String errorMsg = jsonResponse.optString("message", "Unknown error");
                Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing notifications response", e);
            Log.e(TAG, "Raw response was: " + response);
            Toast.makeText(this, "❌ Error parsing notifications", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFollowRequest(int followerId, String action) {
        String url = "http://" + serverIp + "/codekendra/api/handle_follow_request.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Follow request response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("status")) {
                            String message = action.equals("accept") ? "✅ Follow request accepted" : "✅ Follow request declined";
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            loadNotifications(); // Refresh notifications
                        } else {
                            String errorMsg = jsonResponse.optString("error", "Unknown error");
                            Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing follow request response", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Error handling follow request: " + error.toString());
                    Toast.makeText(this, "❌ Network error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("follower_id", String.valueOf(followerId));
                params.put("following_id", String.valueOf(sessionManager.getUserId()));
                params.put("action", action);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void playNotificationSound() {
        try {
            Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mediaPlayer = MediaPlayer.create(this, notificationUri);
            if (mediaPlayer != null) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing notification sound", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }
}