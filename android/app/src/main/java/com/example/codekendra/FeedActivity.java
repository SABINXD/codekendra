package com.example.codekendra;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.DefaultRetryPolicy;
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
        setContentView(R.layout.feed_item_enhanced);

        sessionManager = new SessionManager(this);
        String serverIp = getString(R.string.server_ip);
        int userId = sessionManager.getUserId();

        FEED_URL = "http://" + serverIp + "/codekendra/api/get_posts.php?user_id=" + userId;

        Log.d(TAG, "=== FEED ACTIVITY DEBUG ===");
        Log.d(TAG, "Server IP: " + serverIp);
        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Feed URL: " + FEED_URL);

        if (userId == -1) {
            Log.e(TAG, "❌ User not logged in!");
            finish();
            return;
        }

        initializeViews();
        setupRecyclerView();
        setupSwipeRefresh();
        testConnectivity();
        loadFeed();
    }

    private void testConnectivity() {
        String testUrl = "http://" + getString(R.string.server_ip) + "/codekendra/api/get_posts.php";
        Log.d(TAG, "🌐 Testing connectivity to: " + testUrl);

        JsonObjectRequest testRequest = new JsonObjectRequest(
                Request.Method.GET,
                testUrl,
                null,
                response -> Log.d(TAG, "✅ Server reachable: " + response.toString()),
                error -> {
                    Log.e(TAG, "❌ Server unreachable: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status code: " + error.networkResponse.statusCode);
                    }
                }
        );
        Volley.newRequestQueue(this).add(testRequest);
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerFeed);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        if (recyclerView == null) {
            Log.e(TAG, "❌ RecyclerView not found!");
            return;
        }
    }

    private void setupRecyclerView() {
        String serverIp = getString(R.string.server_ip);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(this, postList, serverIp, sessionManager.getUserId());
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "✅ RecyclerView setup complete");
    }

    private void setupSwipeRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light
            );
            swipeRefreshLayout.setOnRefreshListener(() -> {
                Log.d(TAG, "🔄 Swipe refresh triggered");
                loadFeed();
            });
        }
    }

    private void loadFeed() {
        if (isLoadingFeed) {
            Log.d(TAG, "⏳ Feed loading already in progress");
            return;
        }

        isLoadingFeed = true;
        Log.d(TAG, "🔄 Loading feed from: " + FEED_URL);

        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                FEED_URL,
                null,
                response -> {
                    isLoadingFeed = false;
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Log.d(TAG, "✅ Feed response received");
                    Log.d(TAG, "📄 Raw response: " + response.toString());
                    parseFeed(response);
                },
                error -> {
                    isLoadingFeed = false;
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Log.e(TAG, "❌ Feed loading error: " + error.toString());
                    String errorMessage = "Failed to load posts";
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        Log.e(TAG, "HTTP Status Code: " + statusCode);
                        try {
                            String errorBody = new String(error.networkResponse.data);
                            Log.e(TAG, "Error response body: " + errorBody);
                            if (statusCode == 404) {
                                errorMessage = "API not found. Check server setup.";
                            } else if (statusCode == 500) {
                                errorMessage = "Server error. Check server logs.";
                            } else {
                                errorMessage += " (HTTP " + statusCode + ")";
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Could not parse error body", e);
                        }
                    } else {
                        Log.e(TAG, "No network response - connectivity issue");
                        errorMessage += " - Check network connection";
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                2,
                1.0f
        ));

        Volley.newRequestQueue(this).add(request);
    }

    private void parseFeed(JSONObject response) {
        try {
            Log.d(TAG, "🔍 Parsing feed response...");

            if (!response.has("status")) {
                Log.e(TAG, "❌ Response missing 'status' field");
                return;
            }

            String status = response.getString("status");
            Log.d(TAG, "Response status: " + status);

            if (!"success".equals(status)) {
                String errorMsg = response.optString("message", "Unknown server error");
                Log.e(TAG, "❌ Server returned error: " + errorMsg);
                return;
            }

            if (!response.has("posts")) {
                Log.e(TAG, "❌ Response missing 'posts' array");
                return;
            }

            JSONArray postsArray = response.getJSONArray("posts");
            Log.d(TAG, "📝 Found " + postsArray.length() + " posts in response");

            postList.clear();

            for (int i = 0; i < postsArray.length(); i++) {
                try {
                    JSONObject postObj = postsArray.getJSONObject(i);
                    Log.d(TAG, "📄 Processing post " + i);
                    Post post = createPostFromJson(postObj);
                    postList.add(post);
                    Log.d(TAG, "✅ Post " + i + " added: " + post.getUserName());
                } catch (Exception e) {
                    Log.e(TAG, "❌ Error parsing post " + i, e);
                }
            }

            adapter.notifyDataSetChanged();
            Log.d(TAG, "🎉 Feed loaded successfully with " + postList.size() + " posts");

        } catch (Exception e) {
            Log.e(TAG, "❌ Critical error parsing feed", e);
        }
    }

    private Post createPostFromJson(JSONObject obj) throws Exception {
        Post post = new Post();

        if (!obj.has("id")) throw new Exception("Missing 'id' field");
        if (!obj.has("user_id")) throw new Exception("Missing 'user_id' field");
        if (!obj.has("user_name")) throw new Exception("Missing 'user_name' field");
        if (!obj.has("post_description")) throw new Exception("Missing 'post_description' field");
        if (!obj.has("post_image")) throw new Exception("Missing 'post_image' field");

        post.setId(obj.getInt("id"));
        post.setUserId(obj.getInt("user_id"));
        post.setUserName(obj.getString("user_name"));
        post.setPostDescription(obj.getString("post_description"));
        post.setPostImage(obj.getString("post_image"));

        post.setProfilePic(obj.optString("profile_pic", null));
        post.setLikeCount(obj.optInt("like_count", 0));
        post.setCommentCount(obj.optInt("comment_count", 0));
        post.setLikedByCurrentUser(obj.optBoolean("is_liked", false));
        post.setCreatedAt(obj.optString("created_at", ""));

        post.setCodeContent(obj.optString("code_content", null));
        post.setCodeLanguage(obj.optString("code_language", null));

        if (obj.has("tags") && !obj.isNull("tags")) {
            JSONArray tagsArray = obj.getJSONArray("tags");
            List<String> tagsList = new ArrayList<>();
            for (int i = 0; i < tagsArray.length(); i++) {
                String tag = tagsArray.getString(i);
                if (!tag.trim().isEmpty()) {
                    tagsList.add(tag.trim());
                }
            }
            post.setTags(tagsList);
        }

        Log.d(TAG, "📄 Created post: ID=" + post.getId() + ", User=" + post.getUserName() +
                ", Tags=" + (post.getTags() != null ? post.getTags().size() : 0) +
                ", HasCode=" + (post.getCodeContent() != null && !post.getCodeContent().isEmpty()));

        return post;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "🔄 Activity resumed - refreshing feed");
        loadFeed();
    }
}