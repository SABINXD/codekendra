package com.example.codekendra;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private static final String TAG = "PostAdapter";
    private Context context;
    private List<Post> posts;
    private String serverIp;
    private int currentUserId;
    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private final Runnable timeUpdater;
    // VS Code Dark Theme Colors
    private static final int COLOR_KEYWORD = Color.parseColor("#C586C0");
    private static final int COLOR_STRING = Color.parseColor("#CE9178");
    private static final int COLOR_COMMENT = Color.parseColor("#6A9955");
    private static final int COLOR_METHOD = Color.parseColor("#DCDCAA");
    private static final int COLOR_TYPE = Color.parseColor("#4EC9B0");
    private static final int COLOR_NUMBER = Color.parseColor("#B5CEA8");
    private static final int COLOR_VARIABLE = Color.parseColor("#9CDCFE");
    private static final int COLOR_DEFAULT = Color.parseColor("#D4D4D4");
    private static final int COLOR_PROPERTY = Color.parseColor("#9CDCFE");
    private static final int COLOR_BRACKET = Color.parseColor("#FFD700");

    public PostAdapter(Context context, List<Post> posts, String serverIp, int currentUserId) {
        this.context = context;
        this.posts = posts;
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

    @Override
    public int getItemViewType(int position) {
        // We'll determine the view type based on the context
        if (context instanceof ProfileActivity || context instanceof UserProfileActivity) {
            return 1; // Grid view type
        } else {
            return 0; // List view type
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) { // Grid view
            view = LayoutInflater.from(context).inflate(R.layout.item_post_grid, parent, false);
        } else { // List view
            view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        }
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        int viewType = getItemViewType(position);

        if (viewType == 1) { // Grid view
            bindGridView(holder, post, position);
        } else { // List view
            bindListView(holder, post, position);
        }
    }

    private void bindGridView(PostViewHolder holder, Post post, int position) {
        // Load post image if available
        if (post.getPostImage() != null && !post.getPostImage().isEmpty()) {
            String postImageUrl = "http://" + serverIp + "/codekendra/web/assets/img/posts/" + post.getPostImage();
            Picasso.get()
                    .load(postImageUrl)
                    .placeholder(R.drawable.ic_post)
                    .error(R.drawable.ic_broken_image)
                    .into(holder.postImage);
        } else if (post.hasCode()) {
            holder.postImage.setImageResource(R.drawable.code_placeholder);
        } else {
            holder.postImage.setImageResource(R.drawable.ic_post);
        }

        // Set like count and status
        holder.tvLikeCount.setText(String.valueOf(post.getLikeCount()));
        if (post.isLikedByCurrentUser()) {
            holder.btnLike.setImageResource(R.drawable.like_icon_filled);
        } else {
            holder.btnLike.setImageResource(R.drawable.like_icon);
        }

        // Set comment count
        holder.tvCommentCount.setText(String.valueOf(post.getCommentCount()));

        // Setup click listeners for grid view
        setupGridClickListeners(holder, post, position);
    }

    private void bindListView(PostViewHolder holder, Post post, int position) {
        // Set user info
        holder.userName.setText(post.getUserName());
        holder.postDescription.setText(post.getPostDescription());
        holder.likeCount.setText(String.valueOf(post.getLikeCount()));
        holder.commentCount.setText(String.valueOf(post.getCommentCount()));
        holder.postTime.setText(getTimeAgo(post.getCreatedAt()));

        // Load profile picture
        if (holder.profilePic != null) {
            if (post.getProfilePic() != null && !post.getProfilePic().isEmpty() && !post.getProfilePic().equals("null")) {
                Picasso.get()
                        .load(post.getProfilePic())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .into(holder.profilePic);
            } else {
                holder.profilePic.setImageResource(R.drawable.profile_placeholder);
            }
        } else if (holder.profilePicRegular != null) {
            if (post.getProfilePic() != null && !post.getProfilePic().isEmpty() && !post.getProfilePic().equals("null")) {
                Picasso.get()
                        .load(post.getProfilePic())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .into(holder.profilePicRegular);
            } else {
                holder.profilePicRegular.setImageResource(R.drawable.profile_placeholder);
            }
        }

        // Load post image
        if (post.getPostImage() != null && !post.getPostImage().isEmpty() && !post.getPostImage().equals("null")) {
            holder.imageCard.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(post.getPostImage())
                    .placeholder(R.drawable.ic_post)
                    .error(R.drawable.ic_broken_image)
                    .into(holder.postImage);
        } else {
            holder.imageCard.setVisibility(View.GONE);
        }

        // Display tags
        setupTags(holder, post.getTags());

        // Handle code content
       

        // Set like button state
        updateLikeButton(holder, post.isLikedByCurrentUser());

        // Setup click listeners for list view
        setupClickListeners(holder, post, position);
    }

    private void setupGridClickListeners(PostViewHolder holder, Post post, int position) {
        // Like button click
        holder.btnLike.setOnClickListener(v -> {
            toggleLike(post, holder, position);
        });

        // Comment button click
        holder.btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("post_id", post.getId());
            intent.putExtra("post_img", post.getPostImage());
            intent.putExtra("post_text", post.getPostDescription());
            intent.putExtra("user_name", post.getUserName());
            intent.putExtra("created_at", post.getCreatedAt());
            intent.putExtra("code_content", post.getCodeContent());
            intent.putExtra("code_language", post.getCodeLanguage());
            context.startActivity(intent);
        });

        // Post options (3-dot menu)
        holder.ivOptions.setOnClickListener(v -> {
            showPostOptions(post, position, v);
        });

        // Post image click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra("post_id", post.getId());
            intent.putExtra("post_img", post.getPostImage());
            intent.putExtra("post_text", post.getPostDescription());
            intent.putExtra("user_name", post.getUserName());
            intent.putExtra("created_at", post.getCreatedAt());
            intent.putExtra("code_content", post.getCodeContent());
            intent.putExtra("code_language", post.getCodeLanguage());
            context.startActivity(intent);
        });
    }

    // Keep all your existing methods (setupTags, setupEnhancedCodeView, etc.) here
    // I'm including only the essential ones for brevity

    private void setupTags(PostViewHolder holder, List<String> tags) {
        if (tags != null && !tags.isEmpty()) {
            holder.rvTags.setVisibility(View.VISIBLE);
            TagAdapter tagAdapter = new TagAdapter(context, tags, false);
            holder.rvTags.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.rvTags.setAdapter(tagAdapter);
        } else {
            holder.rvTags.setVisibility(View.GONE);
        }
    }

    private void updateLikeButton(PostViewHolder holder, boolean isLiked) {
        if (isLiked) {
            holder.likeIcon.setImageResource(R.drawable.like_icon_filled);
            holder.likeIcon.setColorFilter(Color.parseColor("#FF6B6B"));
        } else {
            holder.likeIcon.setImageResource(R.drawable.like_icon);
            holder.likeIcon.setColorFilter(Color.parseColor("#666666"));
        }
    }

    private void setupClickListeners(PostViewHolder holder, Post post, int position) {
        // Like button click
        holder.likeLayout.setOnClickListener(v -> toggleLike(post, holder, position));

        // Comment button click
        holder.commentLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("post_id", post.getId());
            intent.putExtra("post_img", post.getPostImage());
            intent.putExtra("post_text", post.getPostDescription());
            intent.putExtra("user_name", post.getUserName());
            intent.putExtra("created_at", post.getCreatedAt());
            intent.putExtra("code_content", post.getCodeContent());
            intent.putExtra("code_language", post.getCodeLanguage());
            context.startActivity(intent);
        });

        // Post options (3-dot menu)
        holder.postOptions.setOnClickListener(v -> showPostOptions(post, position, v));

        // Post image click
        if (holder.postImage != null) {
            holder.postImage.setOnClickListener(v -> {
                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra("post_id", post.getId());
                intent.putExtra("post_img", post.getPostImage());
                intent.putExtra("post_text", post.getPostDescription());
                intent.putExtra("user_name", post.getUserName());
                intent.putExtra("created_at", post.getCreatedAt());
                intent.putExtra("code_content", post.getCodeContent());
                intent.putExtra("code_language", post.getCodeLanguage());
                context.startActivity(intent);
            });
        }
    }

    private void toggleLike(Post post, PostViewHolder holder, int position) {
        String url = "http://" + serverIp + "/codekendra/api/toggle_like.php";
        Log.d(TAG, "Toggling like for post " + post.getId() + " by user " + currentUserId);
        boolean currentLikeState = post.isLikedByCurrentUser();
        boolean newLikeState = !currentLikeState;
        int currentLikeCount = post.getLikeCount();
        int newLikeCount = currentLikeCount + (newLikeState ? 1 : -1);
        post.setLikedByCurrentUser(newLikeState);
        post.setLikeCount(newLikeCount);

        // Update UI based on view type
        int viewType = getItemViewType(position);
        if (viewType == 1) { // Grid view
            updateLikeButtonGrid(holder, newLikeState);
            holder.tvLikeCount.setText(String.valueOf(newLikeCount));
        } else { // List view
            updateLikeButton(holder, newLikeState);
            holder.likeCount.setText(String.valueOf(newLikeCount));
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Like response: " + response);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("status")) {
                            int serverLikeCount = json.getInt("like_count");
                            boolean serverIsLiked = json.getBoolean("is_liked");
                            post.setLikeCount(serverLikeCount);
                            post.setLikedByCurrentUser(serverIsLiked);

                            // Update UI based on view type
                            if (viewType == 1) { // Grid view
                                updateLikeButtonGrid(holder, serverIsLiked);
                                holder.tvLikeCount.setText(String.valueOf(serverLikeCount));
                            } else { // List view
                                updateLikeButton(holder, serverIsLiked);
                                holder.likeCount.setText(String.valueOf(serverLikeCount));
                            }

                            String action = json.optString("action", "");
                            Toast.makeText(context, "✅ Post " + action, Toast.LENGTH_SHORT).show();
                        } else {
                            post.setLikedByCurrentUser(currentLikeState);
                            post.setLikeCount(currentLikeCount);

                            // Update UI based on view type
                            if (viewType == 1) { // Grid view
                                updateLikeButtonGrid(holder, currentLikeState);
                                holder.tvLikeCount.setText(String.valueOf(currentLikeCount));
                            } else { // List view
                                updateLikeButton(holder, currentLikeState);
                                holder.likeCount.setText(String.valueOf(currentLikeCount));
                            }

                            String error = json.optString("error", "Unknown error");
                            Toast.makeText(context, "❌ " + error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing like response", e);
                        post.setLikedByCurrentUser(currentLikeState);
                        post.setLikeCount(currentLikeCount);

                        // Update UI based on view type
                        if (viewType == 1) { // Grid view
                            updateLikeButtonGrid(holder, currentLikeState);
                            holder.tvLikeCount.setText(String.valueOf(currentLikeCount));
                        } else { // List view
                            updateLikeButton(holder, currentLikeState);
                            holder.likeCount.setText(String.valueOf(currentLikeCount));
                        }

                        Toast.makeText(context, "❌ Failed to update like", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Like toggle error: " + error.toString());
                    post.setLikedByCurrentUser(currentLikeState);
                    post.setLikeCount(currentLikeCount);

                    // Update UI based on view type
                    if (viewType == 1) { // Grid view
                        updateLikeButtonGrid(holder, currentLikeState);
                        holder.tvLikeCount.setText(String.valueOf(currentLikeCount));
                    } else { // List view
                        updateLikeButton(holder, currentLikeState);
                        holder.likeCount.setText(String.valueOf(currentLikeCount));
                    }

                    Toast.makeText(context, "❌ Network error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(post.getId()));
                params.put("user_id", String.valueOf(currentUserId));
                Log.d(TAG, "Like params: " + params.toString());
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    private void updateLikeButtonGrid(PostViewHolder holder, boolean isLiked) {
        if (isLiked) {
            holder.btnLike.setImageResource(R.drawable.like_icon_filled);
        } else {
            holder.btnLike.setImageResource(R.drawable.like_icon);
        }
    }

    private void showPostOptions(Post post, int position, View anchorView) {
        PopupMenu popup = new PopupMenu(context, anchorView);
        if (post.getUserId() == currentUserId) {
            popup.getMenu().add("Delete Post");
        }
        if (post.getCodeContent() != null && !post.getCodeContent().isEmpty()) {
            popup.getMenu().add("Copy Code");
            popup.getMenu().add("Share Code");
        }
        popup.getMenu().add("Share Post");
        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            switch (title) {
                case "Delete Post":
                    showDeleteConfirmation(post.getId(), position);
                    break;
                case "Copy Code":
                    copyCodeToClipboard(post.getCodeContent());
                    break;
                case "Share Code":
                    shareCode(post.getCodeContent(), post.getCodeLanguage());
                    break;
                case "Share Post":
                    sharePost(post);
                    break;
            }
            return true;
        });
        popup.show();
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
                            posts.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, posts.size());
                            Toast.makeText(context, "✅ Post deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = json.optString("error", "Unknown error");
                            Toast.makeText(context, "❌ Failed to delete: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "⚠️ Invalid server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Delete error: " + error.toString());
                    Toast.makeText(context, "❌ Delete failed: network error", Toast.LENGTH_SHORT).show();
                }) {
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

    private void copyCodeToClipboard(String code) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Code", code);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "✅ Code copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void shareCode(String code, String language) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this " + (language != null ? language : "code") + ":\n\n" + code);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Code from CodeKendra");
        context.startActivity(Intent.createChooser(shareIntent, "Share Code"));
    }

    private void sharePost(Post post) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = post.getPostDescription();
        if (post.getCodeContent() != null && !post.getCodeContent().isEmpty()) {
            shareText += "\n\nCode:\n" + post.getCodeContent();
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Post from CodeKendra");
        context.startActivity(Intent.createChooser(shareIntent, "Share Post"));
    }

    private String getTimeAgo(String rawTimestamp) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date postDate = format.parse(rawTimestamp);
            if (postDate == null) return "just now";
            long diff = System.currentTimeMillis() - postDate.getTime();
            if (diff < 0) return "just now";
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
            return "just now";
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        timeHandler.removeCallbacks(timeUpdater);
    }

    public void updatePosts(List<Post> newPosts) {
        this.posts.clear();
        this.posts.addAll(newPosts);
        notifyDataSetChanged();
    }

    public void addPost(Post post) {
        this.posts.add(0, post);
        notifyItemInserted(0);
    }

    // Update PostViewHolder to include grid elements
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        // List view elements
        CircleImageView profilePic;
        ImageView profilePicRegular;
        TextView userName, postTime, postDescription, likeCount, commentCount;
        TextView codeLanguage, codeLanguageIcon, codeLineCount, codeContent, lineNumbers, codeStats;
        ImageView postImage, likeIcon, commentIcon, postOptions;
        LinearLayout likeLayout, commentLayout;
        CardView imageCard, codeCard;
        RecyclerView rvTags;
        ImageButton btnCopyCode, btnExpandCode;
        ScrollView codeVerticalScroll;
        HorizontalScrollView codeHorizontalScroll;

        // Grid view elements
        public ImageButton btnLike, btnComment, ivOptions;
        public TextView tvLikeCount, tvCommentCount;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            // List view elements
            try {
                profilePic = itemView.findViewById(R.id.profilePic);
            } catch (ClassCastException e) {
                profilePic = null;
                profilePicRegular = itemView.findViewById(R.id.profilePic);
            }
            userName = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            postDescription = itemView.findViewById(R.id.postDescription);
            likeCount = itemView.findViewById(R.id.likeCount);
            commentCount = itemView.findViewById(R.id.commentCount);
            postImage = itemView.findViewById(R.id.postImage);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            commentIcon = itemView.findViewById(R.id.commentIcon);
            postOptions = itemView.findViewById(R.id.postOptions);
            likeLayout = itemView.findViewById(R.id.likeLayout);
            commentLayout = itemView.findViewById(R.id.commentLayout);
            imageCard = itemView.findViewById(R.id.imageCard);
            codeCard = itemView.findViewById(R.id.code_card);
            rvTags = itemView.findViewById(R.id.rv_tags);
            codeLanguage = itemView.findViewById(R.id.code_language);
            codeLanguageIcon = itemView.findViewById(R.id.code_language_icon);
            codeLineCount = itemView.findViewById(R.id.code_line_count);
            codeContent = itemView.findViewById(R.id.code_content);
            lineNumbers = itemView.findViewById(R.id.line_numbers);
            btnCopyCode = itemView.findViewById(R.id.btn_copy_code);
            btnExpandCode = itemView.findViewById(R.id.btn_expand_code);
            codeVerticalScroll = itemView.findViewById(R.id.code_vertical_scroll);
            codeHorizontalScroll = itemView.findViewById(R.id.code_horizontal_scroll);
            codeStats = itemView.findViewById(R.id.code_stats);

            // Grid view elements
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            ivOptions = itemView.findViewById(R.id.ivOptions);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
        }
    }
}