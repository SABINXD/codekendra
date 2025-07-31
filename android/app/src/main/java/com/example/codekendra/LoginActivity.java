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
            Log.d("SESSION", "User already logged in with UID = " + sessionManager.getUserId());
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
                String serverIp = getString(R.string.server_ip); // Make sure this exists in strings.xml
                URL url = new URL("http://" + serverIp + "/codekendra/api/login.php");
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
                        Log.d("LOGIN_RESPONSE", "Raw JSON: " + resp);
                        JSONObject json = new JSONObject(resp);
                        String status = json.getString("status");

                        if (status.equalsIgnoreCase("success")) {
                            JSONObject user = json.getJSONObject("user");
                            Log.d("LOGIN_JSON", "User object: " + user.toString());
                            int userId = user.getInt("user_id");
                            String username = user.getString("username");
                            String firstName = user.getString("firstName");
                            String gender = user.optString("gender", "N/A");
                            // Extract profile_pic from the user JSON object
                            String profilePicUrl = user.optString("profile_pic", "");
                            Log.d("LOGIN_JSON", "Parsed user_id = " + userId);

                            SessionManager sessionManager = new SessionManager(LoginActivity.this);
                            // Corrected variable name and added profilePicUrl parameter
                            sessionManager.createSession(userId, email, username, profilePicUrl);
                            Log.d("SESSION", "Session saved with UID = " + sessionManager.getUserId() + ", Profile Pic: " + sessionManager.getProfilePic());

                            Toast.makeText(LoginActivity.this, "Welcome back, " + firstName, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomePage.class);
                            intent.putExtra("username", username);
                            intent.putExtra("firstName", firstName);
                            intent.putExtra("gender", gender);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMsg = json.optString("message", "Unknown error");
                            Toast.makeText(LoginActivity.this, "Login failed: " + errorMsg, Toast.LENGTH_LONG).show();
                            Log.w("LOGIN_FAIL", errorMsg);
                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "JSON error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("LOGIN_JSON", "Parse error", e);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("LOGIN_NETWORK", "Error", e);
                });
            }
        }).start();
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
