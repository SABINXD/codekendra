package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    EditText firstName, lastName, username;
    RadioGroup genderRadioGroup;
    ProgressBar progressBar;
    View continueBtn;

    boolean isFirstNameFilled = false;
    boolean isLastNameFilled = false;
    boolean isUsernameFilled = false;
    boolean isGenderSelected = false;

    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_second_part);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        firstName = findViewById(R.id.firstNameInput);
        lastName = findViewById(R.id.lastNameInput);
        username = findViewById(R.id.userNameInput);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        continueBtn = findViewById(R.id.signupContinueBtn);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setProgress(0);

        firstName.addTextChangedListener(getTextWatcher(() -> {
            isFirstNameFilled = !firstName.getText().toString().trim().isEmpty();
            updateProgress();
        }));

        lastName.addTextChangedListener(getTextWatcher(() -> {
            isLastNameFilled = !lastName.getText().toString().trim().isEmpty();
            updateProgress();
        }));

        username.addTextChangedListener(getTextWatcher(() -> {
            isUsernameFilled = !username.getText().toString().trim().isEmpty();
            updateProgress();
        }));

        genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            isGenderSelected = checkedId != -1;
            updateProgress();
        });

        continueBtn.setOnClickListener(v -> {
            String fname = firstName.getText().toString().trim();
            String lname = lastName.getText().toString().trim();
            String uname = username.getText().toString().trim();

            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            if (selectedGenderId == -1) {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = ((RadioButton) findViewById(selectedGenderId)).getText().toString();

            // Validate
            if (fname.isEmpty()) {
                firstName.setError("Enter first name");
                return;
            }
            if (lname.isEmpty()) {
                lastName.setError("Enter last name");
                return;
            }
            if (uname.isEmpty()) {
                username.setError("Enter a username");
                return;
            }

            new Thread(() -> {
                try {
                    URL url = new URL("http://"+getString(R.string.server_ip) +"/CodeKendra/api/signup.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    String postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                            "&password=" + URLEncoder.encode(password, "UTF-8") +
                            "&firstName=" + URLEncoder.encode(fname, "UTF-8") +
                            "&lastName=" + URLEncoder.encode(lname, "UTF-8") +
                            "&username=" + URLEncoder.encode(uname, "UTF-8") +
                            "&gender=" + URLEncoder.encode(gender, "UTF-8");

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

    private TextWatcher getTextWatcher(Runnable callback) {
        return new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                callback.run();
            }
        };
    }

    private void updateProgress() {
        int progress = 0;
        if (isFirstNameFilled) progress += 25;
        if (isLastNameFilled) progress += 25;
        if (isUsernameFilled) progress += 25;
        if (isGenderSelected) progress += 25;
        progressBar.setProgress(progress);
    }
}
