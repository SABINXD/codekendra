package com.example.codekendra;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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
        View view = LayoutInflater.from(context).inflate(R.layout.feed_item_enhanced, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Set text data
        holder.userName.setText(post.getUserName());
        holder.postDescription.setText(post.getPostDescription());
        holder.likeCount.setText(String.valueOf(post.getLikeCount()));
        holder.commentCount.setText(String.valueOf(post.getCommentCount()));

        holder.postTime.setText(getTimeAgo(post.getCreatedAt()));

        // Load post image with Picasso
        if (post.getPostImage() != null && !post.getPostImage().isEmpty()) {
            holder.imageCard.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(post.getPostImage())
                    .placeholder(R.drawable.ic_post)
                    .error(R.drawable.ic_broken_image)
                    .into(holder.postImage);
        } else {
            holder.imageCard.setVisibility(View.GONE);
        }

        // Handle tags
        if (post.getTags() != null && !post.getTags().isEmpty()) {
            holder.rvTags.setVisibility(View.VISIBLE);
            TagAdapter tagAdapter = new TagAdapter(context, post.getTags(), false);
            holder.rvTags.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.rvTags.setAdapter(tagAdapter);
            Log.d(TAG, "Tags displayed for post " + post.getId() + ": " + post.getTags().size() + " tags");
        } else {
            holder.rvTags.setVisibility(View.GONE);
        }

        // Handle code with syntax highlighting
        if (post.getCodeContent() != null && !post.getCodeContent().isEmpty()) {
            holder.codeCard.setVisibility(View.VISIBLE);
            holder.codeLanguage.setText(post.getCodeLanguage() != null ? post.getCodeLanguage() : "Code");

            // Apply syntax highlighting
            String highlightedCode = applySyntaxHighlighting(post.getCodeContent(), post.getCodeLanguage());
            holder.codeContent.setText(Html.fromHtml(highlightedCode, Html.FROM_HTML_MODE_LEGACY));

            // Generate line numbers
            generateLineNumbers(holder.lineNumbers, post.getCodeContent());

            // Show/hide edit button based on ownership
            if (post.getUserId() != currentUserId) {
                holder.btnEditCode.setVisibility(View.VISIBLE);
                holder.btnEditCode.setOnClickListener(v -> showEditCodeDialog(post));
            } else {
                holder.btnEditCode.setVisibility(View.GONE);
            }

            // Copy code functionality
            holder.btnCopyCode.setOnClickListener(v -> copyCodeToClipboard(post.getCodeContent()));

            Log.d(TAG, "Code displayed for post " + post.getId() + ": " + post.getCodeLanguage());
        } else {
            holder.codeCard.setVisibility(View.GONE);
        }

        // Profile picture
        String profilePicUrl = post.getProfilePic();
        Log.d(TAG, "=== PROFILE PIC DEBUG ===");
        Log.d(TAG, "User: " + post.getUserName());
        Log.d(TAG, "Raw profile pic from Post object: '" + profilePicUrl + "'");

        holder.profilePic.setImageResource(R.drawable.profile_placeholder);

        if (profilePicUrl != null &&
                !profilePicUrl.trim().isEmpty() &&
                !profilePicUrl.equals("null") &&
                !profilePicUrl.equals("")) {

            String imageUrl;
            if (profilePicUrl.startsWith("http")) {
                imageUrl = profilePicUrl;
            } else {
                imageUrl = "http://" + serverIp + "/codekendra/web/assets/img/profile/" + profilePicUrl;
            }

            Log.d(TAG, "✅ Loading profile pic with Picasso: " + imageUrl);
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .transform(new CircleTransform())
                    .into(holder.profilePic, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "✅ Profile pic loaded successfully for " + post.getUserName());
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "❌ Profile pic load failed for " + post.getUserName() + ": " + e.getMessage());
                            holder.profilePic.setImageResource(R.drawable.profile_placeholder);
                        }
                    });
        } else {
            Log.d(TAG, "❌ Using placeholder for " + post.getUserName() + " - Invalid URL: '" + profilePicUrl + "'");
            holder.profilePic.setImageResource(R.drawable.profile_placeholder);
        }

        // Set like icon based on current state
        updateLikeIcon(holder, post.isLikedByCurrentUser());

        // Set up click listeners
        holder.postImage.setOnClickListener(v -> {
            Log.d(TAG, "Post image clicked for post ID: " + post.getId());
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

        holder.commentLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("post_id", post.getId());
            intent.putExtra("post_img", post.getPostImage());
            intent.putExtra("post_text", post.getPostDescription());
            intent.putExtra("user_name", post.getUserName());
            context.startActivity(intent);
        });

        holder.postOptions.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(context, holder.postOptions);
            if (post.getUserId() == currentUserId) {
                menu.getMenu().add("Delete Post");
            }
            menu.getMenu().add("Copy Code");
            menu.setOnMenuItemClickListener(item -> {
                if ("Delete Post".equals(item.getTitle())) {
                    showDeleteConfirmation(post.getId(), position);
                } else if ("Copy Code".equals(item.getTitle()) && post.getCodeContent() != null) {
                    copyCodeToClipboard(post.getCodeContent());
                }
                return true;
            });
            menu.show();
        });

        holder.likeLayout.setOnClickListener(v -> toggleLike(post.getId(), position, holder));

        // Code card click listener for full screen view
        if (holder.codeCard != null) {
            holder.codeCard.setOnClickListener(v -> {
                if (post.getCodeContent() != null && !post.getCodeContent().isEmpty()) {
                    showFullScreenCode(post.getCodeContent(), post.getCodeLanguage());
                }
            });
        }
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

    private String applySyntaxHighlighting(String code, String language) {
        if (code == null || code.isEmpty()) return "";

        String highlighted = code;

        // Basic syntax highlighting based on language
        switch (language != null ? language.toLowerCase() : "") {
            case "html":
                highlighted = highlightHtml(code);
                break;
            case "css":
                highlighted = highlightCss(code);
                break;
            case "javascript":
            case "js":
                highlighted = highlightJavaScript(code);
                break;
            case "java":
                highlighted = highlightJava(code);
                break;
            case "python":
                highlighted = highlightPython(code);
                break;
            default:
                highlighted = highlightGeneric(code);
                break;
        }

        return highlighted;
    }

    private String highlightHtml(String code) {
        return code
                .replaceAll("(&lt;/?)(\\w+)", "<font color='#E11D48'>$1$2</font>")
                .replaceAll("(\\w+)(=)", "<font color='#059669'>$1</font><font color='#DC2626'>$2</font>")
                .replaceAll("\"([^\"]*?)\"", "<font color='#0EA5E9'>\"$1\"</font>")
                .replaceAll("(&lt;!--.*?--&gt;)", "<font color='#6B7280'>$1</font>");
    }

    private String highlightCss(String code) {
        return code
                .replaceAll("([.#]?[\\w-]+)\\s*\\{", "<font color='#E11D48'>$1</font> {")
                .replaceAll("([\\w-]+)\\s*:", "<font color='#059669'>$1</font>:")
                .replaceAll(":\\s*([^;]+);", ": <font color='#0EA5E9'>$1</font>;")
                .replaceAll("(/\\*.*?\\*/)", "<font color='#6B7280'>$1</font>");
    }

    private String highlightJavaScript(String code) {
        return code
                .replaceAll("\\b(function|var|let|const|if|else|for|while|return|class|extends)\\b",
                        "<font color='#E11D48'>$1</font>")
                .replaceAll("\"([^\"]*?)\"", "<font color='#0EA5E9'>\"$1\"</font>")
                .replaceAll("'([^']*?)'", "<font color='#0EA5E9'>'$1'</font>")
                .replaceAll("(//.*?)$", "<font color='#6B7280'>$1</font>");
    }

    private String highlightJava(String code) {
        return code
                .replaceAll("\\b(public|private|protected|static|final|class|interface|extends|implements|import|package)\\b",
                        "<font color='#E11D48'>$1</font>")
                .replaceAll("\\b(int|String|boolean|void|double|float|long|char)\\b",
                        "<font color='#059669'>$1</font>")
                .replaceAll("\"([^\"]*?)\"", "<font color='#0EA5E9'>\"$1\"</font>")
                .replaceAll("(//.*?)$", "<font color='#6B7280'>$1</font>");
    }

    private String highlightPython(String code) {
        return code
                .replaceAll("\\b(def|class|import|from|if|else|elif|for|while|return|try|except|finally)\\b",
                        "<font color='#E11D48'>$1</font>")
                .replaceAll("\"([^\"]*?)\"", "<font color='#0EA5E9'>\"$1\"</font>")
                .replaceAll("'([^']*?)'", "<font color='#0EA5E9'>'$1'</font>")
                .replaceAll("(#.*?)$", "<font color='#6B7280'>$1</font>");
    }

    private String highlightGeneric(String code) {
        return code
                .replaceAll("\"([^\"]*?)\"", "<font color='#0EA5E9'>\"$1\"</font>")
                .replaceAll("'([^']*?)'", "<font color='#0EA5E9'>'$1'</font>")
                .replaceAll("(//.*?)$", "<font color='#6B7280'>$1</font>")
                .replaceAll("(/\\*.*?\\*/)", "<font color='#6B7280'>$1</font>");
    }

    private void generateLineNumbers(TextView lineNumbersView, String code) {
        if (code == null || code.isEmpty()) {
            lineNumbersView.setText("1");
            return;
        }

        String[] lines = code.split("\n");
        StringBuilder lineNumbers = new StringBuilder();

        for (int i = 1; i <= lines.length; i++) {
            lineNumbers.append(i);
            if (i < lines.length) {
                lineNumbers.append("\n");
            }
        }

        lineNumbersView.setText(lineNumbers.toString());
    }

    private void showEditCodeDialog(Post post) {
        // Implementation for editing code (for non-owners)
        Toast.makeText(context, "Edit functionality coming soon!", Toast.LENGTH_SHORT).show();
    }

    
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

    private void toggleLike(int postId, int position, PostViewHolder holder) {
        String url = "http://" + serverIp + "/codekendra/api/toggle_like.php";
        Post post = postList.get(position);
        boolean currentLikeState = post.isLikedByCurrentUser();
        boolean newLikeState = !currentLikeState;
        int currentLikeCount = post.getLikeCount();
        int newLikeCount = currentLikeCount + (newLikeState ? 1 : -1);

        post.setLikedByCurrentUser(newLikeState);
        post.setLikeCount(newLikeCount);
        updateLikeIcon(holder, newLikeState);
        holder.likeCount.setText(String.valueOf(newLikeCount));

        Log.d(TAG, "Toggling like for post " + postId + " - New state: " + newLikeState);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d(TAG, "Like response: " + response);
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("status")) {
                            int serverLikeCount = json.getInt("like_count");
                            boolean serverIsLiked = json.getBoolean("is_liked");

                            post.setLikeCount(serverLikeCount);
                            post.setLikedByCurrentUser(serverIsLiked);
                            updateLikeIcon(holder, serverIsLiked);
                            holder.likeCount.setText(String.valueOf(serverLikeCount));

                            Log.d(TAG, "Like status updated for post " + postId + ": " + (serverIsLiked ? "Liked" : "Unliked"));
                        } else {
                            post.setLikedByCurrentUser(currentLikeState);
                            post.setLikeCount(currentLikeCount);
                            updateLikeIcon(holder, currentLikeState);
                            holder.likeCount.setText(String.valueOf(currentLikeCount));

                            String errorMsg = json.optString("error", "Unknown error");
                            Toast.makeText(context, "❌ Like failed: " + errorMsg, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Like failed: " + errorMsg);
                        }
                    } catch (JSONException e) {
                        post.setLikedByCurrentUser(currentLikeState);
                        post.setLikeCount(currentLikeCount);
                        updateLikeIcon(holder, currentLikeState);
                        holder.likeCount.setText(String.valueOf(currentLikeCount));

                        Toast.makeText(context, "⚠️ Invalid server response for like", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Like JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    post.setLikedByCurrentUser(currentLikeState);
                    post.setLikeCount(currentLikeCount);
                    updateLikeIcon(holder, currentLikeState);
                    holder.likeCount.setText(String.valueOf(currentLikeCount));

                    Toast.makeText(context, "❌ Like failed: network error", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Like network error: " + error.toString());
                }) {
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

    private void copyCodeToClipboard(String code) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Code", code);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "✅ Code copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void showFullScreenCode(String code, String language) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(language != null ? language + " Code" : "Code");

        TextView codeView = new TextView(context);
        codeView.setText(code);
        codeView.setTextIsSelectable(true);
        codeView.setTypeface(android.graphics.Typeface.MONOSPACE);
        codeView.setPadding(32, 32, 32, 32);
        codeView.setTextSize(12);
        codeView.setBackgroundColor(0xFFF8FAFC);

        builder.setView(codeView);
        builder.setPositiveButton("Copy", (dialog, which) -> copyCodeToClipboard(code));
        builder.setNegativeButton("Close", null);
        builder.show();
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

    public static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userName, postDescription, likeCount, commentCount, shareCount, postTime;
        ImageView postImage, postOptions, profilePic, likeIcon, commentIcon, shareIcon;
        LinearLayout likeLayout, commentLayout, shareLayout;

        // Enhanced views for code and tags
        RecyclerView rvTags;
        CardView codeCard, imageCard;
        TextView codeLanguage, codeContent, lineNumbers;
        ImageButton btnEditCode, btnCopyCode;

        public PostViewHolder(View itemView) {
            super(itemView);

            // Basic views
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
        

            // Enhanced views for code and tags
            rvTags = itemView.findViewById(R.id.rv_tags);
            codeCard = itemView.findViewById(R.id.code_card);
            imageCard = itemView.findViewById(R.id.imageCard);
            codeLanguage = itemView.findViewById(R.id.code_language);
            codeContent = itemView.findViewById(R.id.code_content);
            lineNumbers = itemView.findViewById(R.id.line_numbers);
            btnEditCode = itemView.findViewById(R.id.btn_edit_code);
            btnCopyCode = itemView.findViewById(R.id.btn_copy_code);
        }
    }
}