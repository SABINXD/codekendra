package com.example.codekendra;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postList = new ArrayList<>();

    private final String FEED_URL = "http://"+getString(R.string.server_ip)+"/codekendra/api/get_feed.php"; // ⚠️ Replace with your actual IP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed); 

        recyclerView = findViewById(R.id.recyclerFeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(adapter);

        loadFeed();
    }

    private void loadFeed() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                FEED_URL,
                null,
                response -> parseFeed(response),
                error -> Toast.makeText(this, "Failed to load posts", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void parseFeed(JSONArray response) {
        try {
            postList.clear();
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                Post post = new Post();
                post.userName        = obj.getString("username");
                post.postDescription = obj.getString("post_text");
                post.postImage       = obj.getString("post_img");
                post.likeCount       = obj.optInt("like_count", 0);
                post.commentCount    = obj.optInt("comment_count", 0);
                postList.add(post);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing posts", Toast.LENGTH_SHORT).show();
        }
    }
}
