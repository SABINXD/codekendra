package com.example.codekendra;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postList = new ArrayList<>();
    private String FEED_URL;
    private SessionManager sessionManager;

    // Real-time listener
    private final RealTimeManager.RealTimeListener feedListener = (eventType, data) -> {
        Log.d("FeedActivity", "Listener triggered by event: " + eventType);
        if ("new-post".equals(eventType)) {
            runOnUiThread(() -> {
                Toast.makeText(this, "New post detected! Refreshing...", Toast.LENGTH_SHORT).show();
                Log.d("FeedActivity", "loadFeed() called due to real-time trigger");
                loadFeed();
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        sessionManager = new SessionManager(this);
        int currentUserId = sessionManager.getUserId();
        String serverIp = getString(R.string.server_ip);
        FEED_URL = "http://" + serverIp + "/codekendra/api/get_feed.php";

        recyclerView = findViewById(R.id.recyclerFeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(this, postList, serverIp, currentUserId);
        recyclerView.setAdapter(adapter);

        loadFeed();

        // For testing: trigger dummy event manually
        findViewById(R.id.btnTestRealtime).setOnClickListener(v -> {
            try {
                JSONObject dummy = new JSONObject();
                dummy.put("post_id", 999);
                dummy.put("user_id", currentUserId);
                dummy.put("post_text", "Real-time debug test post");
                RealTimeManager.getInstance().sendEvent("new-post", dummy);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RealTimeManager.getInstance().addListener(feedListener);
        Log.d("FeedActivity", "Listener attached in onResume");
    }

    @Override
    protected void onPause() {
        RealTimeManager.getInstance().removeListener(feedListener);
        Log.d("FeedActivity", "Listener detached in onPause");
        super.onPause();
    }

    private void loadFeed() {
        Log.d("FeedActivity", "loadFeed() triggered");
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                FEED_URL,
                null,
                response -> parseFeed(response),
                error -> {
                    Log.e("FeedError", error.toString());
                    Toast.makeText(this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void parseFeed(JSONObject response) {
        try {
            if (!response.getString("status").equals("success")) {
                Toast.makeText(this, "Feed status not success", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray postsArray = response.getJSONArray("posts");
            postList.clear();

            for (int i = 0; i < postsArray.length(); i++) {
                JSONObject obj = postsArray.getJSONObject(i);
                Post post = new Post();

                post.setId(obj.getInt("id"));
                post.setUserName(obj.getString("user_name"));
                post.setPostDescription(obj.getString("post_text"));
                post.setPostImage(obj.getString("post_img"));
                post.setLikeCount(obj.optInt("like_count", 0));
                post.setCommentCount(obj.optInt("comment_count", 0));
                post.setCreatedAt(obj.optString("created_at", ""));

                postList.add(post);
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing posts", Toast.LENGTH_SHORT).show();
        }
    }
}
