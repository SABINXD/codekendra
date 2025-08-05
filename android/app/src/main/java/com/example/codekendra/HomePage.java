package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class HomePage extends AppCompatActivity {
    private static final String TAG = "HomePage";
    ImageView searchButton, postCreateButton, chatButton;
    LinearLayout navPostContainer, navProfileContainer;
    RecyclerView recyclerFeed;
    SwipeRefreshLayout swipeRefreshLayout;
    PostAdapter adapter;
    List<Post> postList = new ArrayList<>();
    String FEED_URL;
    SessionManager sessionManager;
    private boolean isLoadingFeed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        initializeComponents();
        setupRecyclerView();
        setupSwipeRefresh();
        setupClickListeners();
        loadFeed();
    }

    private void initializeComponents() {
        sessionManager = new SessionManager(this);
        String serverIp = getString(R.string.server_ip);
        FEED_URL = "http://" + serverIp + "/codekendra/api/get_posts.php?user_id=" + sessionManager.getUserId();

        Log.d(TAG, "Feed URL: " + FEED_URL);
        Log.d(TAG, "Current user ID: " + sessionManager.getUserId());

        recyclerFeed = findViewById(R.id.recyclerFeed);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchButton = findViewById(R.id.nav_search);
        postCreateButton = findViewById(R.id.nav_post);
        chatButton = findViewById(R.id.send);
        navPostContainer = findViewById(R.id.nav_post_container);
        navProfileContainer = findViewById(R.id.nav_profile_container);
    }

    private void setupRecyclerView() {
        int currentUserId = sessionManager.getUserId();
        String serverIp = getString(R.string.server_ip);
        recyclerFeed.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(this, postList, serverIp, currentUserId);
        recyclerFeed.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
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
    }

    private void setupClickListeners() {
        searchButton.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));

        postCreateButton.setOnClickListener(v ->
                startActivity(new Intent(this, CreatePostActivity.class)));

        chatButton.setOnClickListener(v ->
                startActivity(new Intent(this, ChatActivity.class)));

        navPostContainer.setOnClickListener(v ->
                startActivity(new Intent(this, PostActivity.class)));

        navProfileContainer.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void loadFeed() {
        if (isLoadingFeed) {
            Log.d(TAG, "Feed loading already in progress");
            return;
        }

        isLoadingFeed = true;
        Log.d(TAG, "Loading feed from: " + FEED_URL);

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
                    Log.d(TAG, "Feed response received: " + response.toString());
                    parseFeedResponse(response);
                },
                error -> {
                    isLoadingFeed = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Log.e(TAG, "Feed loading error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error status code: " + error.networkResponse.statusCode);
                        String errorBody = new String(error.networkResponse.data);
                        Log.e(TAG, "Error response: " + errorBody);
                    }
                    Toast.makeText(this, "❌ Failed to load posts", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void parseFeedResponse(JSONObject response) {
        try {
            Log.d(TAG, "Parsing feed response...");

            if (!"success".equals(response.getString("status"))) {
                String errorMsg = response.optString("message", "Unknown error");
                Toast.makeText(this, "❌ Feed error: " + errorMsg, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Feed status not success: " + errorMsg);
                return;
            }

            JSONArray postsArray = response.getJSONArray("posts");
            postList.clear();
            Log.d(TAG, "Processing " + postsArray.length() + " posts");

            for (int i = 0; i < postsArray.length(); i++) {
                JSONObject obj = postsArray.getJSONObject(i);
                Post post = createPostFromJson(obj);
                postList.add(post);
                Log.d(TAG, "Post " + i + ": " + post.getUserName() + " - " + post.getPostDescription());
            }

            adapter.notifyDataSetChanged();
            Log.d(TAG, "✅ Feed loaded successfully with " + postList.size() + " posts");
            Toast.makeText(this, "✅ Loaded " + postList.size() + " posts", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error parsing feed", e);
            Toast.makeText(this, "❌ Error parsing posts: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private Post createPostFromJson(JSONObject obj) throws Exception {
        Post post = new Post();

        post.setId(obj.getInt("id"));
        post.setUserId(obj.getInt("user_id"));
        post.setUserName(obj.getString("user_name"));
        post.setProfilePic(obj.optString("profile_pic", null));
        post.setPostDescription(obj.getString("post_description"));
        post.setPostImage(obj.optString("post_image", ""));
        post.setLikeCount(obj.optInt("like_count", 0));
        post.setCommentCount(obj.optInt("comment_count", 0));
        post.setLikedByCurrentUser(obj.optBoolean("is_liked", false));
        post.setCreatedAt(obj.optString("created_at", ""));

        String codeContent = obj.optString("code_content", null);
        String codeLanguage = obj.optString("code_language", null);
        if (codeContent != null && !codeContent.equals("null") && !codeContent.trim().isEmpty()) {
            post.setCodeContent(codeContent);
            post.setCodeLanguage(codeLanguage);
            Log.d(TAG, "✅ Code found for post " + post.getId() + ": " + codeLanguage + " (" + codeContent.length() + " chars)");
        } else {
            Log.d(TAG, "❌ No code for post " + post.getId());
        }

        if (obj.has("tags") && !obj.isNull("tags")) {
            try {
                JSONArray tagsArray = obj.getJSONArray("tags");
                List<String> tagsList = new ArrayList<>();
                for (int i = 0; i < tagsArray.length(); i++) {
                    String tag = tagsArray.getString(i);
                    if (!tag.trim().isEmpty()) {
                        tagsList.add(tag.trim());
                    }
                }
                post.setTags(tagsList);
                Log.d(TAG, "✅ Tags (JSONArray) found for post " + post.getId() + ": " + tagsList.size() + " tags");
            } catch (Exception e) {
                String tagsString = obj.optString("tags", "");
                if (!tagsString.trim().isEmpty()) {
                    String[] tagsArray = tagsString.split(",");
                    List<String> tagsList = new ArrayList<>();
                    for (String tag : tagsArray) {
                        if (!tag.trim().isEmpty()) {
                            tagsList.add(tag.trim());
                        }
                    }
                    post.setTags(tagsList);
                    Log.d(TAG, "✅ Tags (String) found for post " + post.getId() + ": " + tagsList.size() + " tags");
                } else {
                    Log.d(TAG, "❌ No tags for post " + post.getId());
                }
            }
        } else {
            Log.d(TAG, "❌ No tags for post " + post.getId());
        }

        Log.d(TAG, "📄 Created post: ID=" + post.getId() + ", User=" + post.getUserName() +
                ", Tags=" + (post.getTags() != null ? post.getTags().size() : 0) +
                ", HasCode=" + (post.getCodeContent() != null && !post.getCodeContent().isEmpty()));

        return post;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity resumed - refreshing feed");
        loadFeed();
    }
}