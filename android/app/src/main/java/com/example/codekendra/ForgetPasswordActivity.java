package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ForgetPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpassword);

        TextView backToLogin = findViewById(R.id.backToLoginText);
        backToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(0, 0);
        });

        TextView goToSignup = findViewById(R.id.goToSignupBtn);
        goToSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            overridePendingTransition(0, 0);
        });

       
        Button forgetContinueBtn = findViewById(R.id.ForgetBtn);
        forgetContinueBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
            overridePendingTransition(0, 0);
        });
    }
}
