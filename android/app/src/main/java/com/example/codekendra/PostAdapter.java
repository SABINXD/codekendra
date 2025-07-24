package com.example.codekendra;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context context;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
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

        holder.userName.setText(post.userName);
        holder.postDescription.setText(post.postDescription);
        holder.likeCount.setText("❤️ " + post.likeCount + " Likes");
        holder.commentCount.setText("💬 " + post.commentCount + " Comments");

        Log.d("PostImageURL", post.postImage); // Confirm the URL

        Picasso.get()
                .load(post.postImage)
                .placeholder(R.drawable.ic_post)
                .error(R.drawable.ic_broken_image)
                .into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userName, postDescription, likeCount, commentCount;
        ImageView postImage;

        public PostViewHolder(View itemView) {
            super(itemView);
            userName        = itemView.findViewById(R.id.userName);
            postDescription = itemView.findViewById(R.id.postDescription);
            likeCount       = itemView.findViewById(R.id.likeCount);
            commentCount    = itemView.findViewById(R.id.commentCount);
            postImage       = itemView.findViewById(R.id.postImage);
        }
    }
}
