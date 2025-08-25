package com.example.codekendra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatUserViewHolder> {

    private Context context;
    private List<ChatUser> chatUsers;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ChatUser chatUser, int position);
    }

    public ChatListAdapter(Context context, List<ChatUser> chatUsers) {
        this.context = context;
        this.chatUsers = chatUsers;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ChatUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_user, parent, false);
        return new ChatUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserViewHolder holder, int position) {
        ChatUser chatUser = chatUsers.get(position);

        // Set user name
        holder.userName.setText(chatUser.getUserName());

        // Set last message
        holder.lastMessage.setText(chatUser.getLastMessage());

        // Format and set timestamp
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat;
            Date date = inputFormat.parse(chatUser.getTimestamp());

            // Check if the message is from today
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = dayFormat.format(new Date());
            String messageDay = dayFormat.format(date);

            if (today.equals(messageDay)) {
                outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            } else {
                outputFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
            }

            holder.timestamp.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.timestamp.setText("");
        }

        // Set unread count
        if (chatUser.getUnreadCount() > 0) {
            holder.unreadCount.setVisibility(View.VISIBLE);
            holder.unreadCount.setText(String.valueOf(chatUser.getUnreadCount()));
        } else {
            holder.unreadCount.setVisibility(View.GONE);
        }

        // Load profile image
        String imageUrl = "http://" + context.getString(R.string.server_ip) +
                "/codekendra/web/assets/img/profile/" + chatUser.getProfilePic();

        Glide.with(context)
                .load(imageUrl)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.profile_placeholder)
                .into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return chatUsers.size();
    }

    public void updateChatList(List<ChatUser> newChatUsers) {
        chatUsers.clear();
        chatUsers.addAll(newChatUsers);
        notifyDataSetChanged();
    }

    class ChatUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userName, lastMessage, timestamp, unreadCount;
        ImageView profileImage;

        public ChatUserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            timestamp = itemView.findViewById(R.id.timestamp);
            unreadCount = itemView.findViewById(R.id.unread_count);
            profileImage = itemView.findViewById(R.id.profile_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(chatUsers.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }
}