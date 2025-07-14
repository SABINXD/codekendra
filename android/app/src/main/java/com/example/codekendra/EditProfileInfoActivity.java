package com.example.codekendra;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileInfoActivity extends AppCompatActivity {

    EditText displayNameInput, usernameInput;
    RadioGroup genderRadioGroup;
    Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_info);

        displayNameInput = findViewById(R.id.edit_display_name);
        usernameInput = findViewById(R.id.edit_username);
        genderRadioGroup = findViewById(R.id.gender_group);
        updateBtn = findViewById(R.id.btn_update_profile);

        updateBtn.setOnClickListener(v -> {
            String displayName = displayNameInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();

            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            String gender = selectedGenderId == R.id.radio_male ? "Male" : "Female";

            if (displayName.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Send update to PHP backend here

            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
