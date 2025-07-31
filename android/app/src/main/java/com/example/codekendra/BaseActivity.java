package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Optional: If you want a default layout for base (not typical)
        // setContentView(R.layout.base_layout); // Only if you create one
    }

    protected void setupBottomNav() {
        ImageView search = findViewById(R.id.nav_search);
        ImageView post = findViewById(R.id.nav_post);
        ImageView chat = findViewById(R.id.send);
        LinearLayout navPostContainer = findViewById(R.id.nav_post_container);
        LinearLayout navProfileContainer = findViewById(R.id.nav_profile_container);

        if (search != null)
            search.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));

        if (post != null)
            post.setOnClickListener(v -> startActivity(new Intent(this, CreatePostActivity.class)));

        if (chat != null)
            chat.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));

        if (navPostContainer != null)
            navPostContainer.setOnClickListener(v -> startActivity(new Intent(this, PostActivity.class)));

        if (navProfileContainer != null)
            navProfileContainer.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }
}
