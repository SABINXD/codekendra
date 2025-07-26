package com.example.codekendra;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PostDetailsActivity extends AppCompatActivity {

    private ImageView postImage;
    private TextView postTitle, postAuthorDate, postDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details); 

        postImage       = findViewById(R.id.post_image);
        postTitle       = findViewById(R.id.post_title);
        postAuthorDate  = findViewById(R.id.post_author_date);
        postDescription = findViewById(R.id.post_description);

        // Get extras from intent
        String imageUrl   = getIntent().getStringExtra("post_img");
        String title      = getIntent().getStringExtra("post_text");
        String author     = getIntent().getStringExtra("user_name");
        String createdAt  = getIntent().getStringExtra("created_at");

        postTitle.setText(title);
        postDescription.setText(title); // or add more description if available
        postAuthorDate.setText("By " + author + " • " + getTimeAgo(createdAt));

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_post)
                .error(R.drawable.ic_broken_image)
                .into(postImage);
    }

    // 🕒 Lightweight time-ago formatter
    private String getTimeAgo(String rawTimestamp) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date postDate = format.parse(rawTimestamp);
            long postMillis = postDate.getTime();
            long nowMillis = System.currentTimeMillis();
            long diff = nowMillis - postMillis;

            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long hours   = TimeUnit.MILLISECONDS.toHours(diff);
            long days    = TimeUnit.MILLISECONDS.toDays(diff);

            if (seconds < 60) return seconds + "s ago";
            else if (minutes < 60) return minutes + "m ago";
            else if (hours < 24) return hours + "h ago";
            else if (days < 7) return days + "d ago";
            else if (days < 30) return (days / 7) + "w ago";
            else if (days < 365) return (days / 30) + "mo ago";
            else return (days / 365) + "y ago";

        } catch (Exception e) {
            Log.e("TimeAgoError", "Failed to parse timestamp: " + rawTimestamp);
            return "just now";
        }
    }
}
