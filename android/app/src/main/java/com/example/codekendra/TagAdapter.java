package com.example.codekendra;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codekendra.R;
import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {
    private Context context;
    private List<String> tags;
    private boolean showRemoveButton;
    private OnTagRemovedListener onTagRemovedListener;

    public interface OnTagRemovedListener {
        void onTagRemoved(int position);
    }

    public TagAdapter(Context context, List<String> tags, boolean showRemoveButton) {
        this.context = context;
        this.tags = tags;
        this.showRemoveButton = showRemoveButton;
    }

    public TagAdapter(Context context, List<String> tags) {
        this(context, tags, false);
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
        holder.tagText.setText("#" + tag.trim());

        if (showRemoveButton) {
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.removeButton.setOnClickListener(v -> {
                if (onTagRemovedListener != null) {
                    onTagRemovedListener.onTagRemoved(position);
                }
            });
        } else {
            holder.removeButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tagText;
        ImageView removeButton;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagText = itemView.findViewById(R.id.tag_text);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}