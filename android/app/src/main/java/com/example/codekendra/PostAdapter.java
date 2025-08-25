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
import com.squareup.picasso.Picasso;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.userName.setText(post.getUserName());
        holder.postDescription.setText(post.getPostDescription());
        holder.likeCount.setText(post.getLikeCount() + " Likes");
        holder.commentCount.setText(post.getCommentCount() + " Comments");
        holder.postTime.setText(getTimeAgo(post.getCreatedAt()));

        // Load post image with Picasso
        if (post.hasImage()) {
            holder.postImage.setVisibility(View.VISIBLE);
            String imageUrl = "http://" + serverIp + "/codekendra/web/assets/img/posts/" + post.getPostImage();
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.media_placeholder)
                    .error(R.drawable.ic_broken_image)
                    .into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        // Load profile picture with Picasso
        if (post.getProfilePic() != null && !post.getProfilePic().isEmpty() && !post.getProfilePic().equals("null")) {
            Picasso.get()
                    .load(post.getProfilePic())
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.profilePic);
        } else {
            holder.profilePic.setImageResource(R.drawable.profile_placeholder);
        }

        // Set like button state
        if (post.isLikedByCurrentUser()) {
            holder.likeIcon.setImageResource(R.drawable.like_icon_filled);
        } else {
            holder.likeIcon.setImageResource(R.drawable.like_icon);
        }

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

        holder.likeLayout.setOnClickListener(v -> toggleLike(post.getId(), position));
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
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("status")) {
                            postList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, postList.size());
                            Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = json.optString("error", "Unknown error");
                            Toast.makeText(context, "Failed to delete: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Invalid server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Delete failed: network error", Toast.LENGTH_SHORT).show()
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

    private void toggleLike(int postId, int position) {
        String url = "http://" + serverIp + "/codekendra/api/toggle_like.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            int newLikeCount = json.getInt("like_count");
                            boolean isLiked = json.getBoolean("is_liked");
                            postList.get(position).setLikeCount(newLikeCount);
                            postList.get(position).setLikedByCurrentUser(isLiked);
                            notifyItemChanged(position);
                        } else {
                            String errorMsg = json.optString("message", "Unknown error");
                            Toast.makeText(context, "Like failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Invalid server response for like", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Like failed: network error", Toast.LENGTH_SHORT).show()
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
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            int newCommentCount = json.getInt("comment_count");
                            postList.get(position).setCommentCount(newCommentCount);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Comment added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = json.optString("message", "Unknown error");
                            Toast.makeText(context, "Comment failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Invalid server response for comment", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Comment failed: network error", Toast.LENGTH_SHORT).show()
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
            Log.e("TimeAgoError", "Failed to parse → " + rawTimestamp);
            return "just now";
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userName, postDescription, likeCount, commentCount, postTime;
        ImageView postImage, postOptions, profilePic, likeIcon, commentIcon;
        LinearLayout likeLayout, commentLayout;

        public PostViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            postDescription = itemView.findViewById(R.id.postDescription);
            likeCount = itemView.findViewById(R.id.likeCount);
            commentCount = itemView.findViewById(R.id.commentCount);
            postTime = itemView.findViewById(R.id.postTime);
            postImage = itemView.findViewById(R.id.postImage);
            postOptions = itemView.findViewById(R.id.postOptions);
            profilePic = itemView.findViewById(R.id.profilePic);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            commentIcon = itemView.findViewById(R.id.commentIcon);
            likeLayout = itemView.findViewById(R.id.likeLayout);
            commentLayout = itemView.findViewById(R.id.commentLayout);
        }
    }
}