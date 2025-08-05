package com.example.codekendra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {
    private Context context;
    private List<Notification> notifications;
    private OnFollowRequestListener followRequestListener;

    public interface OnFollowRequestListener {
        void onFollowRequest(int followerId, String action);
    }

    public NotificationsAdapter(Context context, List<Notification> notifications, OnFollowRequestListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.followRequestListener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        // Set notification message
        String message = notification.getUserName() + " " + notification.getMessage();
        holder.notificationMessage.setText(message);

        // Set time
        holder.notificationTime.setText(getTimeAgo(notification.getCreatedAt()));

        // Load profile picture - Handle both CircleImageView and regular ImageView
        if (holder.notificationProfilePic != null) {
            if (notification.getProfilePic() != null && !notification.getProfilePic().isEmpty()) {
                String imageUrl = "http://" + context.getString(R.string.server_ip) + "/codekendra/web/assets/img/profile/" + notification.getProfilePic();
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .into(holder.notificationProfilePic);
            } else {
                holder.notificationProfilePic.setImageResource(R.drawable.profile_placeholder);
            }
        } else if (holder.notificationProfilePicRegular != null) {
            if (notification.getProfilePic() != null && !notification.getProfilePic().isEmpty()) {
                String imageUrl = "http://" + context.getString(R.string.server_ip) + "/codekendra/web/assets/img/profile/" + notification.getProfilePic();
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .into(holder.notificationProfilePicRegular);
            } else {
                holder.notificationProfilePicRegular.setImageResource(R.drawable.profile_placeholder);
            }
        }

        // Show follow request buttons if it's a follow request
        if ("follow_request".equals(notification.getType())) {
            holder.followRequestButtons.setVisibility(View.VISIBLE);

            holder.btnAcceptFollow.setOnClickListener(v -> {
                if (followRequestListener != null) {
                    followRequestListener.onFollowRequest(notification.getFromUserId(), "accept");
                }
            });

            holder.btnDeclineFollow.setOnClickListener(v -> {
                if (followRequestListener != null) {
                    followRequestListener.onFollowRequest(notification.getFromUserId(), "decline");
                }
            });
        } else {
            holder.followRequestButtons.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private String getTimeAgo(String rawTimestamp) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date notificationDate = format.parse(rawTimestamp);
            if (notificationDate == null) return "just now";

            long diff = System.currentTimeMillis() - notificationDate.getTime();
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

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView notificationProfilePic; // For CircleImageView
        ImageView notificationProfilePicRegular; // For regular ImageView fallback
        TextView notificationMessage, notificationTime;
        LinearLayout followRequestButtons;
        Button btnAcceptFollow, btnDeclineFollow;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            // Try to handle both CircleImageView and regular ImageView
            View profileImageView = itemView.findViewById(R.id.notification_profile_pic);
            try {
                if (profileImageView instanceof de.hdodenhof.circleimageview.CircleImageView) {
                    notificationProfilePic = (ImageView) profileImageView;
                    notificationProfilePicRegular = null;
                } else if (profileImageView instanceof ImageView) {
                    notificationProfilePic = null;
                    notificationProfilePicRegular = (ImageView) profileImageView;
                } else {
                    notificationProfilePic = null;
                    notificationProfilePicRegular = null;
                }
            } catch (Exception e) {
                notificationProfilePic = null;
                notificationProfilePicRegular = null;
            }


            notificationMessage = itemView.findViewById(R.id.notification_message);
            notificationTime = itemView.findViewById(R.id.notification_time);
            followRequestButtons = itemView.findViewById(R.id.follow_request_buttons);
            btnAcceptFollow = itemView.findViewById(R.id.btn_accept_follow);
            btnDeclineFollow = itemView.findViewById(R.id.btn_decline_follow);
        }
    }
}