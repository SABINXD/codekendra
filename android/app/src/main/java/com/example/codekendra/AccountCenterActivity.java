package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AccountCenterActivity extends AppCompatActivity {

    Button changeProfileBtn, changePasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_center);

        changeProfileBtn = findViewById(R.id.btn_change_profile);
        changePasswordBtn = findViewById(R.id.btn_change_password);

        changeProfileBtn.setOnClickListener(v -> {
            startActivity(new Intent(AccountCenterActivity.this, EditProfileInfoActivity.class));
        });

        changePasswordBtn.setOnClickListener(v -> {
            startActivity(new Intent(AccountCenterActivity.this, ChangePasswordActivity.class));
        });
    }
}
