package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {

    ImageView searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        searchButton = findViewById(R.id.send);

        searchButton.setOnClickListener(v -> {
            Log.d("SearchClick", "Search icon clicked");
            Toast.makeText(HomePage.this, "Opening Search...", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(HomePage.this, SearchActivity.class);
            startActivity(intent);
        });

    }
}
