package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;    

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {


    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        backButton = findViewById(R.id.back_btn);
        Intent intent = new Intent(ChatActivity.this, HomePage.class);
        startActivity(intent);
        
    }
    
    }

