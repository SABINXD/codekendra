package com.example.codekendra;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private static final String TAG = "PostAdapter";
    private List<Post> postList;
    private Context context;
    private String serverIp;
    private int currentUserId;
    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private final Runnable timeUpdater;

    public PostAdapter(Context context, List<Post> postList, String serverIp, int currentUserId) {
        this.context = context;
        this.postList = postList;
        this.serverIp = serverIp;
        this.currentUserId = currentUserId;

        timeUpdater = new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
                timeHandler.postDelayed(this, 60000);
            }
        };
        timeHandler.postDelayed(timeUpdater, 60000);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Set text data
        holder.userName.setText(post.getUserName());
        holder.postDescription.setText(post.getPostDescription());
        holder.likeCount.setText(post.getLikeCount() + " Likes");
        holder.commentCount.setText(post.getCommentCount() + " Comments");
        holder.postTime.setText(getTimeAgo(post.getCreatedAt()));

        // Load post image with Glide
        Glide.with(context)
                .load(post.getPostImage())
                .placeholder(R.drawable.ic_post)
                .error(R.drawable.ic_broken_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.postImage);

        // Load profile picture - FIXED TO HANDLE FULL URLs
        String profilePicUrl = post.getProfilePic();
        Log.d(TAG, "Profile pic URL for " + post.getUserName() + ": " + profilePicUrl);

        // Set placeholder immediately to prevent flickering
        holder.profilePic.setImageResource(R.drawable.profile_placeholder);

        // Check if we have a valid profile pic URL
        if (profilePicUrl != null && !profilePicUrl.isEmpty() && !profilePicUrl.equals("null")) {
            Log.d(TAG, "Loading profile pic from: " + profilePicUrl);

            Glide.with(context)
                    .load(profilePicUrl)
                    .circleCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(holder.profilePic);
        } else {
            Log.d(TAG, "Using placeholder for " + post.getUserName() + " - URL: " + profilePicUrl);
            // Keep the placeholder that was already set
        }

        // Set like icon based on current state
        updateLikeIcon(holder, post.isLikedByCurrentUser());

        // Click listeners
        holder.postImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra("post_img", post.getPostImage());
            intent.putExtra("post_text", post.getPostDescription());
            intent.putExtra("user_name", post.getUserName());
            intent.putExtra("created_at", post.getCreatedAt());
            context.startActivity(intent);
        });

        holder.postOptions.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(context, holder.postOptions);
            if (post.getUserId() == currentUserId) {
                menu.getMenu().add("Delete Post");
            }
            menu.setOnMenuItemClickListener(item -> {
                if ("Delete Post".equals(item.getTitle())) {
                    showDeleteConfirmation(post.getId(), position);
                }
                return true;
            });
            menu.show();
        });

        holder.likeLayout.setOnClickListener(v -> toggleLike(post.getId(), position, holder));
        holder.commentLayout.setOnClickListener(v -> showCommentDialog(post.getId(), position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        timeHandler.removeCallbacks(timeUpdater);
        Log.d(TAG, "🛑 Handler stopped");
    }

    // Helper method to update like icon
    private void updateLikeIcon(PostViewHolder holder, boolean isLiked) {
        if (isLiked) {
            holder.likeIcon.setImageResource(R.drawable.like_icon_filled);
        } else {
            holder.likeIcon.setImageResource(R.drawable.like_icon);
        }
    }

    private void showDeleteConfirmation(int postId, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> deletePost(postId, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePost(int postId, int position) {
        String url = "http://" + serverIp + "/codekendra/api/delete_post.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d(TAG, "Delete response: " + response);
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("status")) {
                            postList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, postList.size());
                            Toast.makeText(context, "✅ Post deleted", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Post deleted: " + postId);
                        } else {
                            String errorMsg = json.optString("error", "Unknown error");
                            Toast.makeText(context, "❌ Server failed to delete: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "⚠️ Invalid server response", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Delete JSON error: " + e.getMessage());
                    }
                },
                error -> {
                    Toast.makeText(context, "❌ Delete failed: network error", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Delete network error: " + error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                params.put("user_id", String.valueOf(currentUserId));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private void toggleLike(int postId, int position, PostViewHolder holder) {
        String url = "http://" + serverIp + "/codekendra/api/toggle_like.php";

        // Optimistically update UI first for better UX
        Post post = postList.get(position);
        boolean currentLikeState = post.isLikedByCurrentUser();
        boolean newLikeState = !currentLikeState;
        int currentLikeCount = post.getLikeCount();
        int newLikeCount = currentLikeCount + (newLikeState ? 1 : -1);

        // Update UI immediately
        post.setLikedByCurrentUser(newLikeState);
        post.setLikeCount(newLikeCount);
        updateLikeIcon(holder, newLikeState);
        holder.likeCount.setText(newLikeCount + " Likes");

        Log.d(TAG, "Toggling like for post " + postId + " - New state: " + newLikeState);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d(TAG, "Like response: " + response);
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("status")) {
                            // Server confirmed - update with actual values
                            int serverLikeCount = json.getInt("like_count");
                            boolean serverIsLiked = json.getBoolean("is_liked");

                            post.setLikeCount(serverLikeCount);
                            post.setLikedByCurrentUser(serverIsLiked);

                            // Update UI with server values
                            updateLikeIcon(holder, serverIsLiked);
                            holder.likeCount.setText(serverLikeCount + " Likes");

                            Log.d(TAG, "Like status updated for post " + postId + ": " + (serverIsLiked ? "Liked" : "Unliked"));
                        } else {
                            // Server failed - revert UI changes
                            post.setLikedByCurrentUser(currentLikeState);
                            post.setLikeCount(currentLikeCount);
                            updateLikeIcon(holder, currentLikeState);
                            holder.likeCount.setText(currentLikeCount + " Likes");

                            String errorMsg = json.optString("error", "Unknown error");
                            Toast.makeText(context, "❌ Like failed: " + errorMsg, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Like failed: " + errorMsg);
                        }
                    } catch (JSONException e) {
                        // Revert UI changes on error
                        post.setLikedByCurrentUser(currentLikeState);
                        post.setLikeCount(currentLikeCount);
                        updateLikeIcon(holder, currentLikeState);
                        holder.likeCount.setText(currentLikeCount + " Likes");

                        Toast.makeText(context, "⚠️ Invalid server response for like", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Like JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    // Revert UI changes on network error
                    post.setLikedByCurrentUser(currentLikeState);
                    post.setLikeCount(currentLikeCount);
                    updateLikeIcon(holder, currentLikeState);
                    holder.likeCount.setText(currentLikeCount + " Likes");

                    Toast.makeText(context, "❌ Like failed: network error", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Like network error: " + error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                params.put("user_id", String.valueOf(currentUserId));
                Log.d(TAG, "Like params: post_id=" + postId + ", user_id=" + currentUserId);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private void showCommentDialog(int postId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Comment");

        final EditText input = new EditText(context);
        input.setHint("Type your comment here...");
        builder.setView(input);

        builder.setPositiveButton("Post", (dialog, which) -> {
            String commentText = input.getText().toString().trim();
            if (!commentText.isEmpty()) {
                addComment(postId, position, commentText);
            } else {
                Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addComment(int postId, int position, String commentText) {
        String url = "http://" + serverIp + "/codekendra/api/add_comment.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d(TAG, "Comment response: " + response);
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("status")) {
                            int newCommentCount = json.getInt("comment_count");
                            postList.get(position).setCommentCount(newCommentCount);
                            notifyItemChanged(position);
                            Toast.makeText(context, "✅ Comment added", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Comment added for post " + postId);
                        } else {
                            String errorMsg = json.optString("error", "Unknown error");
                            Toast.makeText(context, "❌ Comment failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "⚠️ Invalid server response for comment", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Comment JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Toast.makeText(context, "❌ Comment failed: network error", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Comment network error: " + error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(postId));
                params.put("user_id", String.valueOf(currentUserId));
                params.put("comment_text", commentText);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private String getTimeAgo(String rawTimestamp) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date postDate = format.parse(rawTimestamp);
            if (postDate == null) {
                return "just now";
            }

            long postMillis = postDate.getTime();
            long nowMillis = System.currentTimeMillis();
            long diff = nowMillis - postMillis;

            if (diff < 0) {
                return "just now";
            }

            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long hours   = TimeUnit.MILLISECONDS.toHours(diff);
            long days    = TimeUnit.MILLISECONDS.toDays(diff);

            if (seconds < 60) return seconds + "s ago";
            else if (minutes < 60) return minutes + "m ago";
            else if (hours < 24) return hours + "h ago";
            else if (days < 7) return days + "d ago";
            else if (days < 30) return (days / 7) + "w ago";
            else if (days < 365) return (days / 30) + "mo ago";
            else return (days / 365) + "y ago";
        } catch (Exception e) {
            Log.e(TAG, "TimeAgo parsing error for: " + rawTimestamp, e);
            return "just now";
        }
    }

    public void updatePosts(List<Post> newPosts) {
        this.postList.clear();
        this.postList.addAll(newPosts);
        notifyDataSetChanged();
        Log.d(TAG, "Posts updated: " + newPosts.size() + " posts");
    }

    public void addPost(Post post) {
        this.postList.add(0, post);
        notifyItemInserted(0);
        Log.d(TAG, "New post added at top");
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userName, postDescription, likeCount, commentCount, postTime;
        ImageView postImage, postOptions, profilePic, likeIcon, commentIcon;
        LinearLayout likeLayout, commentLayout;

        public PostViewHolder(View itemView) {
            super(itemView);
            userName        = itemView.findViewById(R.id.userName);
            postDescription = itemView.findViewById(R.id.postDescription);
            likeCount       = itemView.findViewById(R.id.likeCount);
            commentCount    = itemView.findViewById(R.id.commentCount);
            postTime        = itemView.findViewById(R.id.postTime);
            postImage       = itemView.findViewById(R.id.postImage);
            postOptions     = itemView.findViewById(R.id.postOptions);
            profilePic      = itemView.findViewById(R.id.profilePic);
            likeIcon        = itemView.findViewById(R.id.likeIcon);
            commentIcon     = itemView.findViewById(R.id.commentIcon);
            likeLayout      = itemView.findViewById(R.id.likeLayout);
            commentLayout   = itemView.findViewById(R.id.commentLayout);
        }
    }
}