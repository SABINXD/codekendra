package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.*;

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

        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, HomePage.class));
            finish();
            return;
        }

        setContentView(R.layout.login);

        goToSignupBtn = findViewById(R.id.signupBtn);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        emailInput = findViewById(R.id.loginEmailInput);
        passwordInput = findViewById(R.id.loginPasswordInput);
        loginContinueBtn = findViewById(R.id.loginContinueBtn);

        goToSignupBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            overridePendingTransition(0, 0);
        });

        forgotPasswordText.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgetPasswordActivity.class));
            overridePendingTransition(0, 0);
        });

        loginContinueBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!isValidEmail(email)) {
                emailInput.setError("Enter a valid email");
                return;
            }

            if (password.length() < 6) {
                passwordInput.setError("Password must be at least 6 characters");
                return;
            }

            performLogin(email, password);
        });
    }

    private void performLogin(String email, String password) {
        new Thread(() -> {
            try {
                URL url = new URL("http://" + getString(R.string.server_ip) + "/codekendra/api/login.php");
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
                    try {
                        String resp = result.toString().trim();
                        Log.d("LoginPHP", "Response: " + resp);

                        JSONObject json = new JSONObject(resp);
                        String status = json.getString("status");

                        if (status.equalsIgnoreCase("success")) {
                            JSONObject user = json.getJSONObject("user");
                            String username = user.getString("username");
                            String firstName = user.getString("firstName");
                            String gender = user.getString("gender");

                            SessionManager sessionManager = new SessionManager(LoginActivity.this);
                            sessionManager.createSession(email, username);

                            Toast.makeText(LoginActivity.this, "Welcome back, " + firstName + "!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, HomePage.class);
                            intent.putExtra("username", username);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("gender", gender);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMsg = json.getString("message");
                            Toast.makeText(LoginActivity.this, "Login failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "JSON parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
