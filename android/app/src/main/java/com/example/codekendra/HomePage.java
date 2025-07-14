package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {

    ImageView searchButton, profileButton,postCreateButton,ChatButton;
   
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        sessionManager = new SessionManager(this);

        searchButton = findViewById(R.id.nav_search);
        searchButton.setOnClickListener(v -> {
            Log.d("SearchClick", "Search icon clicked");
            Toast.makeText(HomePage.this, "", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(HomePage.this, SearchActivity.class);
            startActivity(intent);
        });

        profileButton = findViewById(R.id.nav_profile);
        profileButton.setOnClickListener(v -> {
            Log.d("ProfileClick", "Profile icon clicked");
            Toast.makeText(HomePage.this, "", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(HomePage.this, ProfileActivity.class);
            startActivity(intent);
        });
        postCreateButton = findViewById(R.id.nav_post);
        postCreateButton.setOnClickListener(v -> {
            Log.d("PostClick", "Post icon clicked");
            Toast.makeText(HomePage.this, "", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(HomePage.this, PostActivity.class);
            startActivity(intent);
        });
        ChatButton = findViewById(R.id.send);
        ChatButton.setOnClickListener(v -> {
            Log.d("PostClick", "Post icon clicked");
            Toast.makeText(HomePage.this, "", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(HomePage.this, ChatActivity.class);
            startActivity(intent);
        });
    }
}
