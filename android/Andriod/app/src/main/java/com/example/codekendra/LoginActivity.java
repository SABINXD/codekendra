package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    TextView goToSignupBtn, forgotPasswordText;
    Button loginContinueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        goToSignupBtn = findViewById(R.id.signupBtn);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        emailInput = findViewById(R.id.loginEmailInput);
        passwordInput = findViewById(R.id.loginPasswordInput);
        loginContinueBtn = findViewById(R.id.loginContinueBtn);  // Use Button type here!

        goToSignupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        loginContinueBtn.setOnClickListener(v -> {
            String enteredEmail = emailInput.getText().toString().trim();
            String enteredPassword = passwordInput.getText().toString().trim();

            if (!isValidEmail(enteredEmail)) {
                emailInput.setError("Enter a valid email");
                return;
            }

            if (enteredPassword.length() < 6) {
                passwordInput.setError("Password must be at least 6 characters");
                return;
            }

            performLogin(enteredEmail, enteredPassword);
        });
    }

    private void performLogin(String email, String password) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.8/codekendra/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                is.close();
                conn.disconnect();

                runOnUiThread(() -> {
                    String resp = result.toString().trim();
                    Log.d("LoginPHP", "Response: " + resp);

                    if (resp.equalsIgnoreCase("Login successful")) {
                        Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomePage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + resp, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
