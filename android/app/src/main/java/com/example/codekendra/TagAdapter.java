package com.example.codekendra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {
    private Context context;
    private List<String> tags;
    private boolean isEditable;
    private OnTagRemovedListener onTagRemovedListener;

    public interface OnTagRemovedListener {
        void onTagRemoved(int position);
    }

    public TagAdapter(Context context, List<String> tags, boolean isEditable) {
        this.context = context;
        this.tags = tags;
        this.isEditable = isEditable;
    }

    public void setOnTagRemovedListener(OnTagRemovedListener listener) {
        this.onTagRemovedListener = listener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        String tag = tags.get(position);
        holder.tagText.setText("#" + tag);

        if (isEditable) {
            holder.removeIcon.setVisibility(View.VISIBLE);
            holder.removeIcon.setOnClickListener(v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && onTagRemovedListener != null) {
                    onTagRemovedListener.onTagRemoved(adapterPosition);
                }
            });
        } else {
            holder.removeIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tagText;
        ImageView removeIcon;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagText = itemView.findViewById(R.id.tag_text);
            removeIcon = itemView.findViewById(R.id.remove_icon);
        }
    }
}