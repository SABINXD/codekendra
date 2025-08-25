package com.example.codekendra;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.ProfilePostViewHolder> {
    private static final String TAG = "ProfilePostAdapter";
    private Context context;
    private List<Post> posts;
    private String serverIp;
    private int currentUserId;

    public ProfilePostAdapter(Context context, List<Post> posts, String serverIp, int currentUserId) {
        this.context = context;
        this.posts = posts;
        this.serverIp = serverIp;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ProfilePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_post, parent, false);
        return new ProfilePostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfilePostViewHolder holder, int position) {
        Post post = posts.get(position);

        // Set post description
        if (post.getPostDescription() != null && !post.getPostDescription().isEmpty()) {
            holder.postDescription.setText(post.getPostDescription());
            holder.postDescription.setVisibility(View.VISIBLE);
        } else {
            holder.postDescription.setVisibility(View.GONE);
        }

        // Set like and comment counts
        holder.likeCount.setText(String.valueOf(post.getLikeCount()));
        holder.commentCount.setText(String.valueOf(post.getCommentCount()));

        // Load post image if available using Picasso
        if (post.hasImage()) {
            holder.imageCard.setVisibility(View.VISIBLE);
            String imageUrl = "http://" + serverIp + "/codekendra/web/assets/img/posts/" + post.getPostImage();
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.post_placeholder)
                    .error(R.drawable.post_placeholder)
                    .into(holder.postImage);
        } else {
            holder.imageCard.setVisibility(View.GONE);
        }

        // Handle code content
        if (post.hasCode()) {
            setupCodeView(holder, post);
        } else {
            if (holder.codeCard != null) {
                holder.codeCard.setVisibility(View.GONE);
            }
        }

        // Set like button state
        if (post.isLikedByCurrentUser()) {
            holder.likeIcon.setImageResource(R.drawable.like_icon_filled);
        } else {
            holder.likeIcon.setImageResource(R.drawable.like_icon);
        }

        // Setup click listeners
        holder.likeLayout.setOnClickListener(v -> toggleLike(post, holder, position));
        holder.commentLayout.setOnClickListener(v -> openComments(post));
        holder.postOptions.setOnClickListener(v -> showPostOptions(post, position, holder));
    }

    private void setupCodeView(ProfilePostViewHolder holder, Post post) {
        if (holder.codeCard != null) {
            holder.codeCard.setVisibility(View.VISIBLE);

            // Set language info
            String language = post.getCodeLanguage();
            if (language != null && !language.isEmpty()) {
                if (holder.codeLanguage != null) {
                    holder.codeLanguage.setText(language);
                }
                if (holder.codeLanguageIcon != null) {
                    holder.codeLanguageIcon.setText(getLanguageIcon(language));
                }
            } else {
                if (holder.codeLanguage != null) {
                    holder.codeLanguage.setText("code.txt");
                }
                if (holder.codeLanguageIcon != null) {
                    holder.codeLanguageIcon.setText("📄");
                }
            }

            // Set code content
            if (holder.codeContent != null) {
                holder.codeContent.setText(post.getCodeContent());
            }

            // Setup copy button
            if (holder.btnCopyCode != null) {
                holder.btnCopyCode.setOnClickListener(v -> {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                            context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Code", post.getCodeContent());
                    clipboard.setPrimaryClip(clip);
                    android.widget.Toast.makeText(context, "Code copied to clipboard", android.widget.Toast.LENGTH_SHORT).show();
                });
            }

            // Setup expand button
            if (holder.btnExpandCode != null) {
                holder.btnExpandCode.setOnClickListener(v -> {
                    android.widget.Toast.makeText(context, "Code expand feature coming soon", android.widget.Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private String getLanguageIcon(String language) {
        if (language == null) return "📄";

        switch (language.toLowerCase()) {
            case "java": return "☕";
            case "javascript": case "js": return "🟨";
            case "python": return "🐍";
            case "php": return "🐘";
            case "html": return "🌐";
            case "css": return "🎨";
            case "sql": return "🗃️";
            case "c++": case "cpp": return "⚡";
            case "c#": case "csharp": return "🔷";
            case "kotlin": return "🟣";
            case "swift": return "🍎";
            default: return "📄";
        }
    }

    private void toggleLike(Post post, ProfilePostViewHolder holder, int position) {
        boolean newLikeState = !post.isLikedByCurrentUser();
        post.setLikedByCurrentUser(newLikeState);

        if (newLikeState) {
            post.setLikeCount(post.getLikeCount() + 1);
            holder.likeIcon.setImageResource(R.drawable.like_icon_filled);
        } else {
            post.setLikeCount(post.getLikeCount() - 1);
            holder.likeIcon.setImageResource(R.drawable.like_icon);
        }

        holder.likeCount.setText(String.valueOf(post.getLikeCount()));

        android.widget.Toast.makeText(context,
                newLikeState ? "Liked" : "Unliked",
                android.widget.Toast.LENGTH_SHORT).show();
    }

    private void openComments(Post post) {
        android.widget.Toast.makeText(context, "Comments feature coming soon", android.widget.Toast.LENGTH_SHORT).show();
    }

    private void showPostOptions(Post post, int position, ProfilePostViewHolder holder) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(context, holder.postOptions);
        popup.getMenu().add("Share Post");

        if (post.hasCode()) {
            popup.getMenu().add("Copy Code");
        }

        if (post.getUserId() == currentUserId) {
            popup.getMenu().add("Delete Post");
        }

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            switch (title) {
                case "Share Post":
                    sharePost(post);
                    break;
                case "Copy Code":
                    copyCodeToClipboard(post.getCodeContent());
                    break;
                case "Delete Post":
                    deletePost(post, position);
                    break;
            }
            return true;
        });
        popup.show();
    }

    private void sharePost(Post post) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = post.getPostDescription();
        if (post.hasCode()) {
            shareText += "\n\nCode:\n" + post.getCodeContent();
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Post from CodeKendra");
        context.startActivity(Intent.createChooser(shareIntent, "Share Post"));
    }

    private void copyCodeToClipboard(String code) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Code", code);
        clipboard.setPrimaryClip(clip);
        android.widget.Toast.makeText(context, "Code copied to clipboard", android.widget.Toast.LENGTH_SHORT).show();
    }

    private void deletePost(Post post, int position) {
        posts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, posts.size());
        android.widget.Toast.makeText(context, "Post deleted", android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class ProfilePostViewHolder extends RecyclerView.ViewHolder {
        TextView postDescription, likeCount, commentCount;
        TextView codeLanguage, codeLanguageIcon, codeContent;
        ImageView postImage, likeIcon, commentIcon, postOptions;
        LinearLayout likeLayout, commentLayout;
        androidx.cardview.widget.CardView imageCard, codeCard;
        ImageButton btnCopyCode, btnExpandCode;

        public ProfilePostViewHolder(@NonNull View itemView) {
            super(itemView);

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

            // Optional code-related views
            codeCard = itemView.findViewById(R.id.code_card);
            codeLanguage = itemView.findViewById(R.id.code_language);
            codeLanguageIcon = itemView.findViewById(R.id.code_language_icon);
            codeContent = itemView.findViewById(R.id.code_content);
            btnCopyCode = itemView.findViewById(R.id.btn_copy_code);
            btnExpandCode = itemView.findViewById(R.id.btn_expand_code);
        }
    }
}