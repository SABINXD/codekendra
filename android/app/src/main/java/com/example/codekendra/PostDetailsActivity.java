package com.example.codekendra;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PostDetailsActivity extends AppCompatActivity {
    private static final String TAG = "PostDetailsActivity";

    private ImageView postImage;
    private TextView postTitle, postAuthorDate, postDescription;
    private RecyclerView commentsRecyclerView;
    private EditText commentInput;
    private Button addCommentButton;

    private List<Comment> commentsList = new ArrayList<>();
    private CommentsAdapter commentsAdapter;
    private SessionManager sessionManager;
    private String serverIp;
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        initializeViews();
        setupData();
        setupComments();
        loadComments();
    }

    private void initializeViews() {
        postImage = findViewById(R.id.post_image);
        postTitle = findViewById(R.id.post_title);
        postAuthorDate = findViewById(R.id.post_author_date);
        postDescription = findViewById(R.id.post_description);
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        commentInput = findViewById(R.id.comment_input);
        addCommentButton = findViewById(R.id.add_comment_button);

        sessionManager = new SessionManager(this);
        serverIp = getString(R.string.server_ip);
    }

    private void setupData() {
        String imageUrl = getIntent().getStringExtra("post_img");
        String title = getIntent().getStringExtra("post_text");
        String author = getIntent().getStringExtra("user_name");
        String createdAt = getIntent().getStringExtra("created_at");
        postId = getIntent().getIntExtra("post_id", -1);

        Log.d(TAG, "Post details - ID: " + postId + ", Author: " + author);

        postTitle.setText(title);
        postDescription.setText(title);
        postAuthorDate.setText("By " + author + " • " + getTimeAgo(createdAt));

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_post)
                .error(R.drawable.ic_broken_image)
                .into(postImage);
    }

    private void setupComments() {
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsAdapter = new CommentsAdapter(this, commentsList);
        commentsRecyclerView.setAdapter(commentsAdapter);

        addCommentButton.setOnClickListener(v -> addComment());
    }

    private void loadComments() {
        if (postId == -1) {
            Log.e(TAG, "Invalid post ID");
            return;
        }

        String url = "http://" + serverIp + "/codekendra/api/get_comments.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d(TAG, "Comments response: " + response);
                        JSONObject json = new JSONObject(response);

                        if (json.getBoolean("status")) {
                            JSONArray commentsArray = json.getJSONArray("comments");
                            commentsList.clear();

                            for (int i = 0; i < commentsArray.length(); i++) {
                                JSONObject commentObj = commentsArray.getJSONObject(i);
                                Comment comment = new Comment();
                                comment.setId(commentObj.getInt("id"));
                                comment.setUserId(commentObj.getInt("user_id"));
                                comment.setUserName(commentObj.getString("user_name"));
                                comment.setCommentText(commentObj.getString("comment_text"));
                                comment.setCreatedAt(commentObj.getString("created_at"));
                                comment.setProfilePic(commentObj.optString("profile_pic", null));
                                commentsList.add(comment);
                            }

                            commentsAdapter.notifyDataSetChanged();
                            Log.d(TAG, "Loaded " + commentsList.size() + " comments");
                        } else {
                            String error = json.optString("message", "Failed to load comments");
                            Log.e(TAG, "Comments load failed: " + error);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing comments", e);
                    }
                },
                error -> Log.e(TAG, "Network error loading comments", error)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void addComment() {
        String commentText = commentInput.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        if (postId == -1) {
            Toast.makeText(this, "Invalid post", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://" + serverIp + "/codekendra/api/add_comment.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d(TAG, "Add comment response: " + response);
                        JSONObject json = new JSONObject(response);

                        if (json.getBoolean("status")) {
                            commentInput.setText("");
                            Toast.makeText(this, "✅ Comment added", Toast.LENGTH_SHORT).show();
                            loadComments();
                        } else {
                            String error = json.optString("error", "Failed to add comment");
                            Toast.makeText(this, "❌ " + error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing add comment response", e);
                        Toast.makeText(this, "❌ Error adding comment", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error adding comment", error);
                    Toast.makeText(this, "❌ Network error", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                params.put("user_id", String.valueOf(sessionManager.getUserId()));
                params.put("comment_text", commentText);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private String getTimeAgo(String rawTimestamp) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date postDate = format.parse(rawTimestamp);
            long postMillis = postDate.getTime();
            long nowMillis = System.currentTimeMillis();
            long diff = nowMillis - postMillis;

            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            long days = TimeUnit.MILLISECONDS.toDays(diff);

            if (seconds < 60) return seconds + "s ago";
            else if (minutes < 60) return minutes + "m ago";
            else if (hours < 24) return hours + "h ago";
            else if (days < 7) return days + "d ago";
            else if (days < 30) return (days / 7) + "w ago";
            else if (days < 365) return (days / 30) + "mo ago";
            else return (days / 365) + "y ago";
        } catch (Exception e) {
            Log.e("TimeAgoError", "Failed to parse timestamp: " + rawTimestamp);
            return "just now";
        }
    }
}