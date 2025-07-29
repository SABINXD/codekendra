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

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    ImageView searchButton, postCreateButton, chatButton;
    LinearLayout navPostContainer, navProfileContainer;
    RecyclerView recyclerFeed;
    PostAdapter adapter;
    List<Post> postList = new ArrayList<>();

    String FEED_URL;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        sessionManager = new SessionManager(this);
        int currentUserId = sessionManager.getUserId();

        String serverIp = getString(R.string.server_ip);
        FEED_URL = "http://" + serverIp + "/codekendra/api/get_feed.php";

        recyclerFeed = findViewById(R.id.recyclerFeed);
        recyclerFeed.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(this, postList, serverIp, currentUserId);
        recyclerFeed.setAdapter(adapter);

        loadFeed();

        // 🔧 Bottom Nav Bindings
        searchButton        = findViewById(R.id.nav_search);
        postCreateButton    = findViewById(R.id.nav_post);
        chatButton          = findViewById(R.id.send); // from top bar
        navPostContainer    = findViewById(R.id.nav_post_container);
        navProfileContainer = findViewById(R.id.nav_profile_container); // ✅ actual clickable parent

        // 🔗 Click Actions
        searchButton.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        postCreateButton.setOnClickListener(v -> startActivity(new Intent(this, CreatePostActivity.class)));
        chatButton.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        navPostContainer.setOnClickListener(v -> startActivity(new Intent(this, PostActivity.class)));
        navProfileContainer.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void loadFeed() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                FEED_URL,
                null,
                response -> {
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

                            postList.add(post);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing feed", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("FeedError", error.toString());
                    Toast.makeText(this, "Failed to load feed", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}
