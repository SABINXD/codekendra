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

    ImageView searchButton, profileButton, postCreateButton, ChatButton;
    LinearLayout navPostContainer;
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
        int currentUserId = sessionManager.getUserId(); // 🔥 make sure this returns the real logged-in ID

        String serverIp = getString(R.string.server_ip);
        FEED_URL = "http://" + serverIp + "/codekendra/api/get_feed.php";

        recyclerFeed = findViewById(R.id.recyclerFeed);
        recyclerFeed.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PostAdapter(this, postList, serverIp, currentUserId); // ✅ pass user ID!
        recyclerFeed.setAdapter(adapter);

        loadFeed();

        searchButton      = findViewById(R.id.nav_search);
        profileButton     = findViewById(R.id.nav_profile);
        postCreateButton  = findViewById(R.id.nav_post);
        ChatButton        = findViewById(R.id.send);
        navPostContainer  = findViewById(R.id.nav_post_container);

        searchButton.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        ChatButton.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        navPostContainer.setOnClickListener(v -> startActivity(new Intent(this, PostActivity.class)));
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

                            post.setId(obj.getInt("id")); // ✅ crucial for deletion!
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
