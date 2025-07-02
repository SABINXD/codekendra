package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

public class signup_second_part extends AppCompatActivity {

    EditText firstName, lastName;
    ProgressBar progressBar;
    View continueBtn;
    boolean isFirstNameFilled = false;
    boolean isLastNameFilled = false;
    int baseProgress = 0;

    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_second_part);
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        firstName = findViewById(R.id.firstNameInput);
        lastName = findViewById(R.id.lastNameInput);
        continueBtn = findViewById(R.id.signupContinueBtn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(baseProgress);

        firstName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                isFirstNameFilled = !s.toString().trim().isEmpty();
                updateProgress();
            }
        });

        lastName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                isLastNameFilled = !s.toString().trim().isEmpty();
                updateProgress();
            }
        });

        continueBtn.setOnClickListener(v -> {
            String fname = firstName.getText().toString().trim();
            String lname = lastName.getText().toString().trim();

            if (fname.isEmpty()) {
                firstName.setError("Enter first name");
                return;
            }

            if (lname.isEmpty()) {
                lastName.setError("Enter last name");
                return;
            }

            new Thread(() -> {
                try {
                    URL url = new URL("http://192.168.1.8/codekendra/signup.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    String postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                            "&password=" + URLEncoder.encode(password, "UTF-8") +
                            "&firstName=" + URLEncoder.encode(fname, "UTF-8") +
                            "&lastName=" + URLEncoder.encode(lname, "UTF-8");

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
                        Toast.makeText(signup_second_part.this, "Signup Success: " + result.toString(), Toast.LENGTH_SHORT).show();
                        progressBar.setProgress(100);
                        startActivity(new Intent(signup_second_part.this, HomePage.class));
                        finish();
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(signup_second_part.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }).start();
        });

        TextView loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private void updateProgress() {
        int progress = baseProgress;
        if (isFirstNameFilled) progress = 70;
        if (isFirstNameFilled && isLastNameFilled) progress = 100;
        progressBar.setProgress(progress);
    }
}
