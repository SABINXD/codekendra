package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class SignupActivity extends AppCompatActivity {

    EditText email, password;
    ProgressBar progressBar;
    TextView goToLoginBtn;
    View continueBtn;
    boolean isPasswordVisible = false;
    Animation fadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        email = findViewById(R.id.signupEmailInput);
        password = findViewById(R.id.signupUsernameInput);
        continueBtn = findViewById(R.id.signupContinueBtn);
        progressBar = findViewById(R.id.progressBar);
        goToLoginBtn = findViewById(R.id.loginBtn);

        password.setVisibility(View.GONE);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        progressBar.setProgress(0);

        goToLoginBtn.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            overridePendingTransition(0, 0);
        });

        continueBtn.setOnClickListener(v -> {
            String enteredEmail = email.getText().toString().trim();

            if (!isPasswordVisible) {
                if (!isValidEmail(enteredEmail)) {
                    email.setError("Enter a valid email (like yourname@gmail.com)");
                    return;
                }

        
                checkEmailAndProceed(enteredEmail);

            } else {
                String enteredPassword = password.getText().toString().trim();

                if (!isValidEmail(enteredEmail)) {
                    email.setError("Enter a valid email (like yourname@gmail.com)");
                    return;
                }

                if (!isStrongPassword(enteredPassword)) {
                    password.setError("Password must be 6+ chars with letters & numbers");
                    return;
                }

                progressBar.setProgress(50);
                Intent intent = new Intent(SignupActivity.this, signup_second_part.class);
                intent.putExtra("email", enteredEmail);
                intent.putExtra("password", enteredPassword);
                intent.putExtra("progress", 50);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void checkEmailAndProceed(String enteredEmail) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.2/codekendra/api/signup.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String postData = "email=" + URLEncoder.encode(enteredEmail, "UTF-8");

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

                    if (resp.equalsIgnoreCase("exists")) {
                        email.setError("Email already in use!");
                        Toast.makeText(SignupActivity.this, "This email is already registered.", Toast.LENGTH_SHORT).show();
                    } else if (resp.equalsIgnoreCase("available")) {
                        password.setVisibility(View.VISIBLE);
                        password.startAnimation(fadeIn);
                        isPasswordVisible = true;
                        progressBar.setProgress(25);
                    } else {
                        Toast.makeText(SignupActivity.this, "Unexpected server response: " + resp, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(SignupActivity.this, "Error checking email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                (email.endsWith(".com") || email.endsWith(".net") || email.endsWith(".org")) &&
                email.matches(".*[a-zA-Z].*");
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 6 &&
                password.matches(".*[a-zA-Z].*") &&
                password.matches(".*[0-9].*");
    }
}
