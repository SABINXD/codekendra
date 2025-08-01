package com.example.codekendra;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity implements RealTimeManager.RealTimeListener {
    private static final String TAG = "CommentActivity";

    private TextView postTitle, postAuthor;
    private ImageView postImage;
    private RecyclerView commentsRecyclerView;
    private EditText commentInput;
    private Button addCommentButton;
    private List<Comment> commentsList = new ArrayList<>();
    private CommentsAdapter commentsAdapter;
    private SessionManager sessionManager;
    private String serverIp;
    private int postId;
    private RealTimeManager realTimeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Initialize RealTimeManager
        realTimeManager = RealTimeManager.getInstance();

        initializeViews();
        setupData();
        setupComments();
        loadComments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register WebSocket listener
        realTimeManager.addListener(this);
        Log.d(TAG, "WebSocket listener registered");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister WebSocket listener
        realTimeManager.removeListener(this);
        Log.d(TAG, "WebSocket listener unregistered");
    }

    private void initializeViews() {
        postImage = findViewById(R.id.comment_post_image);
        postTitle = findViewById(R.id.comment_post_title);
        postAuthor = findViewById(R.id.comment_post_author);
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        commentInput = findViewById(R.id.comment_input);
        addCommentButton = findViewById(R.id.add_comment_button);
        sessionManager = new SessionManager(this);
        serverIp = getString(R.string.server_ip);

        // Set up button click listener
        addCommentButton.setOnClickListener(v -> postComment());
    }

    private void setupData() {
        String imageUrl = getIntent().getStringExtra("post_img");
        String title = getIntent().getStringExtra("post_text");
        String author = getIntent().getStringExtra("user_name");
        postId = getIntent().getIntExtra("post_id", -1);
        Log.d(TAG, "Comment activity - Post ID: " + postId + ", Author: " + author);
        postTitle.setText(title);
        postAuthor.setText("By " + author);
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
                error -> {
                    Log.e(TAG, "Network error loading comments", error);
                }
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

    private void postComment() {
        Log.d(TAG, "=== POST COMMENT DEBUG ===");
        String commentText = commentInput.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        if (postId == -1) {
            Toast.makeText(this, "Invalid post", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Please login to comment", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://" + serverIp + "/codekendra/api/add_comment.php";
        Log.d(TAG, "Sending request to: " + url);
        Log.d(TAG, "Post ID: " + postId);
        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Comment: " + commentText);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d(TAG, "Raw response: " + response);
                        JSONObject json = new JSONObject(response);
                        boolean status = json.getBoolean("status");
                        Log.d(TAG, "Status: " + status);

                        if (status) {
                            commentInput.setText("");
                            Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show();
                            // Comment will be added via WebSocket, no need to reload
                        } else {
                            String error = json.optString("error", "Failed to add comment");
                            Log.e(TAG, "Error from server: " + error);
                            Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error parsing response", e);
                        Log.e(TAG, "Raw response that failed to parse: " + response);
                    }
                },
                error -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Network error", error);
                    Log.e(TAG, "Network error details: " + error.getMessage());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Data: " + new String(error.networkResponse.data));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                params.put("user_id", String.valueOf(userId));
                params.put("comment_text", commentText);

                Log.d(TAG, "Sending params: " + params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        Log.d(TAG, "Adding request to queue");
        Volley.newRequestQueue(this).add(request);
    }

    // RealTimeManager.RealTimeListener methods
    @Override
    public void onEvent(String eventType, JSONObject data) {
        try {
            Log.d(TAG, "Received WebSocket event: " + eventType);
            Log.d(TAG, "Event data: " + data.toString());

            if ("new_comment".equals(eventType)) {
                int receivedPostId = data.getInt("post_id");
                Log.d(TAG, "Received comment for post_id: " + receivedPostId + ", current post_id: " + postId);

                if (receivedPostId == postId) {
                    // This comment is for the current post
                    JSONObject commentData = data.getJSONObject("comment");

                    Comment comment = new Comment();
                    comment.setId(commentData.getInt("id"));
                    comment.setUserId(commentData.getInt("user_id"));
                    comment.setUserName(commentData.getString("user_name"));
                    comment.setCommentText(commentData.getString("comment_text"));
                    comment.setCreatedAt(commentData.getString("created_at"));
                    comment.setProfilePic(commentData.optString("profile_pic", null));

                    // Add comment to the top of the list
                    commentsList.add(0, comment);
                    commentsAdapter.notifyItemInserted(0);

                    // Scroll to top to show the new comment
                    commentsRecyclerView.scrollToPosition(0);

                    Log.d(TAG, "New comment added via WebSocket: " + comment.getCommentText());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing WebSocket event", e);
        }
    }

    @Override
    public void onConnectionStateChanged(boolean connected) {
        Log.d(TAG, "WebSocket connection state changed: " + connected);
    }
}