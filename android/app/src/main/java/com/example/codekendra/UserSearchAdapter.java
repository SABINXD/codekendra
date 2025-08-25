package com.example.codekendra;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserViewHolder> {
    private Context context;
    private List<User> userList;
    private String serverIp;

    public UserSearchAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.serverIp = context.getString(R.string.server_ip);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_search, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.displayName.setText(user.getDisplayName());
        holder.username.setText("@" + user.getUsername());

        String bio = user.getBio();
        if (bio == null || bio.trim().isEmpty()) {
            holder.bio.setText("No bio available");
            holder.bio.setAlpha(0.6f);
        } else {
            holder.bio.setText(bio);
            holder.bio.setAlpha(1.0f);
        }

        // Load profile image
        String imageUrl = "http://" + serverIp + "/codekendra/web/assets/img/profile/" + user.getProfilePic();
        Glide.with(context)
                .load(imageUrl + "?t=" + System.currentTimeMillis())
                .circleCrop()
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .into(holder.profileImage);

        // Click listener to view user profile
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("user_id", user.getId());
            intent.putExtra("username", user.getUsername());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView displayName, username, bio;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            displayName = itemView.findViewById(R.id.display_name);
            username = itemView.findViewById(R.id.username);
            bio = itemView.findViewById(R.id.bio);
        }
    }
}