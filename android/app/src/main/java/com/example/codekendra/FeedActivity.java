package com.example.codekendra;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    private static final String TAG = "FeedActivity";
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postList = new ArrayList<>();
    private String FEED_URL;
    private SessionManager sessionManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoadingFeed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        sessionManager = new SessionManager(this);
        String serverIp = getString(R.string.server_ip);

        // FIXED: Use get_posts.php (not get_post.php)
        FEED_URL = "http://" + serverIp + "/codekendra/api/get_posts.php?user_id=" + sessionManager.getUserId();

        Log.d(TAG, "Feed URL: " + FEED_URL);
        Log.d(TAG, "Current user ID: " + sessionManager.getUserId());

        // Initialize views
        recyclerView = findViewById(R.id.recyclerFeed);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(this, postList, serverIp, sessionManager.getUserId());
        recyclerView.setAdapter(adapter);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "Swipe refresh triggered");
            loadFeed();
        });

        // Load initial feed
        loadFeed();
    }

    private void loadFeed() {
        if (isLoadingFeed) {
            Log.d(TAG, "Feed loading already in progress");
            return;
        }

        isLoadingFeed = true;
        Log.d(TAG, "🔄 Loading feed from: " + FEED_URL);

        // Show refresh indicator
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                FEED_URL,
                null,
                response -> {
                    isLoadingFeed = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG, "✅ Feed response received");
                    Log.d(TAG, "Response: " + response.toString());
                    parseFeed(response);
                },
                error -> {
                    isLoadingFeed = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Log.e(TAG, "❌ Feed loading error: " + error.toString());

                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error status code: " + error.networkResponse.statusCode);
                        try {
                            String errorBody = new String(error.networkResponse.data);
                            Log.e(TAG, "Error response body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Could not parse error body", e);
                        }
                    }

                    String errorMessage = "Failed to load posts";
                    if (error.networkResponse != null) {
                        errorMessage += " (Code: " + error.networkResponse.statusCode + ")";
                    }

                    Toast.makeText(this, "�� " + errorMessage, Toast.LENGTH_LONG).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void parseFeed(JSONObject response) {
        try {
            Log.d(TAG, "🔍 Parsing feed response...");

            String status = response.optString("status", "unknown");
            Log.d(TAG, "Response status: " + status);

            if (!"success".equals(status)) {
                String errorMsg = response.optString("message", "Unknown error");
                Toast.makeText(this, "❌ Feed error: " + errorMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Feed status not success: " + errorMsg);
                return;
            }

            if (!response.has("posts")) {
                Log.e(TAG, "No 'posts' array in response");
                Toast.makeText(this, "❌ Invalid response format", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray postsArray = response.getJSONArray("posts");
            postList.clear();

            Log.d(TAG, "📝 Processing " + postsArray.length() + " posts");

            for (int i = 0; i < postsArray.length(); i++) {
                try {
                    JSONObject obj = postsArray.getJSONObject(i);
                    Post post = createPostFromJson(obj);
                    postList.add(post);
                    Log.d(TAG, "✅ Post " + i + ": " + post.getUserName() + " - " + post.getPostDescription().substring(0, Math.min(50, post.getPostDescription().length())));
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing post " + i, e);
                }
            }

            adapter.notifyDataSetChanged();
            Log.d(TAG, "🎉 Feed loaded successfully with " + postList.size() + " posts");

            if (postList.size() > 0) {
                Toast.makeText(this, "✅ Loaded " + postList.size() + " posts", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "📭 No posts found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error parsing feed", e);
            Toast.makeText(this, "❌ Error parsing posts: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private Post createPostFromJson(JSONObject obj) throws Exception {
        Post post = new Post();

        // Required fields
        post.setId(obj.getInt("id"));
        post.setUserId(obj.getInt("user_id"));
        post.setUserName(obj.getString("user_name"));
        post.setPostDescription(obj.getString("post_description"));
        post.setPostImage(obj.getString("post_image"));

        // Optional fields with defaults
        post.setProfilePic(obj.optString("profile_pic", null));
        post.setLikeCount(obj.optInt("like_count", 0));
        post.setCommentCount(obj.optInt("comment_count", 0));
        post.setLikedByCurrentUser(obj.optBoolean("is_liked", false));
        post.setCreatedAt(obj.optString("created_at", ""));

        Log.d(TAG, "📄 Created post: ID=" + post.getId() +
                ", User=" + post.getUserName() +
                ", ProfilePic=" + post.getProfilePic() +
                ", Liked=" + post.isLikedByCurrentUser());

        return post;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "🔄 Activity resumed - refreshing feed");
        loadFeed();
    }
}