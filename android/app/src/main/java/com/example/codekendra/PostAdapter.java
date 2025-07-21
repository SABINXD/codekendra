package com.example.codekendra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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

        String imageUrl = "http://192.168.1.10/codekendra/" + post.postImage;
        Glide.with(context).load(imageUrl).placeholder(R.drawable.ic_post).into(holder.postImage);
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
