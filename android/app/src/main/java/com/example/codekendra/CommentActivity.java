package com.example.codekendra;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";

    private RecyclerView commentsRecyclerView;
    private EditText commentEditText;
    private Button submitCommentButton;
    private ImageView postImage;
    private TextView postText, userName, postTime;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentsList = new ArrayList<>();

    private int postId;
    private String serverIp;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        sessionManager = new SessionManager(this);
        serverIp = getString(R.string.server_ip);

        initializeViews();
        getIntentData();
        setupRecyclerView();
        setupClickListeners();
        loadComments();
    }

    private void initializeViews() {
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        commentEditText = findViewById(R.id.comment_edit_text);
        submitCommentButton = findViewById(R.id.submit_comment_button);
        postImage = findViewById(R.id.post_image);
        postText = findViewById(R.id.post_text);
        userName = findViewById(R.id.user_name);
        postTime = findViewById(R.id.post_time);
    }

    private void getIntentData() {
        postId = getIntent().getIntExtra("post_id", -1);
        String postImg = getIntent().getStringExtra("post_img");
        String postDescription = getIntent().getStringExtra("post_text");
        String userNameStr = getIntent().getStringExtra("user_name");
        String createdAt = getIntent().getStringExtra("created_at");

        Log.d(TAG, "Post ID: " + postId);
        Log.d(TAG, "Post Image: " + postImg);
        Log.d(TAG, "Post Text: " + postDescription);

        // Set post data
        if (postDescription != null) {
            postText.setText(postDescription);
        }

        if (userNameStr != null) {
            userName.setText(userNameStr);
        }

        if (createdAt != null) {
            postTime.setText(createdAt);
        }

        // Load post image if available
        if (postImg != null && !postImg.isEmpty() && !postImg.equals("null")) {
            postImage.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(postImg)
                    .placeholder(R.drawable.ic_post)
                    .error(R.drawable.ic_broken_image)
                    .into(postImage);
        } else {
            postImage.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        commentsAdapter = new CommentsAdapter(this, commentsList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    private void setupClickListeners() {
        submitCommentButton.setOnClickListener(v -> submitComment());

        // Back button
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    private void loadComments() {
        String url = "http://" + serverIp + "/codekendra/api/get_comments.php";

        Log.d(TAG, "Loading comments from: " + url);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Comments response: " + response);
                    parseCommentsResponse(response);
                },
                error -> {
                    Log.e(TAG, "Error loading comments: " + error.toString());
                    Toast.makeText(this, "❌ Failed to load comments", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                Log.d(TAG, "Comments params: " + params.toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void parseCommentsResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getBoolean("status")) {
                JSONArray commentsArray = jsonResponse.getJSONArray("comments");
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
                Log.d(TAG, "✅ Loaded " + commentsList.size() + " comments");

            } else {
                String errorMsg = jsonResponse.optString("message", "Unknown error");
                Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing comments response", e);
            Toast.makeText(this, "❌ Error parsing comments", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitComment() {
        String commentText = commentEditText.getText().toString().trim();

        if (commentText.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "You must be logged in to comment", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://" + serverIp + "/codekendra/api/add_comment.php";

        Log.d(TAG, "Submitting comment to: " + url);
        Log.d(TAG, "Comment text: " + commentText);
        Log.d(TAG, "Post ID: " + postId);
        Log.d(TAG, "User ID: " + userId);

        submitCommentButton.setEnabled(false);
        submitCommentButton.setText("Posting...");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Add comment response: " + response);
                    submitCommentButton.setEnabled(true);
                    submitCommentButton.setText("Post Comment");

                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        if (jsonResponse.getBoolean("status")) {
                            commentEditText.setText("");
                            Toast.makeText(this, "✅ Comment posted!", Toast.LENGTH_SHORT).show();
                            loadComments(); // Reload comments
                        } else {
                            String errorMsg = jsonResponse.optString("error", "Unknown error");
                            Toast.makeText(this, "❌ " + errorMsg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing add comment response", e);
                        Toast.makeText(this, "❌ Error posting comment", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error submitting comment: " + error.toString());
                    submitCommentButton.setEnabled(true);
                    submitCommentButton.setText("Post Comment");

                    if (error.networkResponse != null) {
                        try {
                            String errorBody = new String(error.networkResponse.data, "UTF-8");
                            Log.e(TAG, "Error response body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Could not parse error response", e);
                        }
                    }

                    Toast.makeText(this, "❌ Network error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                params.put("user_id", String.valueOf(userId));
                params.put("comment_text", commentText);
                Log.d(TAG, "Add comment params: " + params.toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
