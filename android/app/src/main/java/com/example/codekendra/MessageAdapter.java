package com.example.codekendra;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages = new ArrayList<>();
    private int currentUserId;

    public MessageAdapter(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageText.setText(message.getMessage());

        // Format timestamp
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date = inputFormat.parse(message.getCreated_at());
            holder.timestampText.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.timestampText.setText("");
        }

        // Set alignment based on sender
        if (message.getFrom_user_id() == currentUserId) {
            holder.messageContainer.setGravity(Gravity.END);
            holder.messageLayout.setBackgroundResource(R.drawable.sent_message_bg);
            holder.messageText.setTextColor(0xFFFFFFFF); // White text
            holder.timestampText.setTextColor(0xAAFFFFFF); // Lighter white
        } else {
            holder.messageContainer.setGravity(Gravity.START);
            holder.messageLayout.setBackgroundResource(R.drawable.received_message_bg);
            holder.messageText.setTextColor(0xFF000000); // Black text
            holder.timestampText.setTextColor(0xAA000000); // Lighter black
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<Message> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timestampText;
        LinearLayout messageContainer;
        LinearLayout messageLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }
}