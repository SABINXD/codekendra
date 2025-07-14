package com.example.codekendra;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText oldPass, newPass, confirmPass;
    Button updatePasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);

        oldPass = findViewById(R.id.oldPasswordInput);
        newPass = findViewById(R.id.newPasswordInput);
        confirmPass = findViewById(R.id.confirmPasswordInput);
        updatePasswordBtn = findViewById(R.id.updatePasswordBtn);

        updatePasswordBtn.setOnClickListener(v -> {
            String oldPassword = oldPass.getText().toString().trim();
            String newPassword = newPass.getText().toString().trim();
            String confirmPassword = confirmPass.getText().toString().trim();

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "New passwords don't match!", Toast.LENGTH_SHORT).show();
                return;
            }

       
            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
