package com.example.codekendra;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class EditProfileInfoActivity extends AppCompatActivity {

    EditText displayNameInput, usernameInput, bioInput;
    Button updateBtn;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_info);

        sessionManager = new SessionManager(this);

        displayNameInput = findViewById(R.id.edit_display_name);
        usernameInput    = findViewById(R.id.edit_username);
        bioInput         = findViewById(R.id.edit_bio);
        updateBtn        = findViewById(R.id.btn_update_profile);

        updateBtn.setOnClickListener(v -> {
            String displayName = displayNameInput.getText().toString().trim();
            String username     = usernameInput.getText().toString().trim();
            String bio          = bioInput.getText().toString().trim();

            if (displayName.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            StringRequest request = new StringRequest(Request.Method.POST,
                    "http://192.168.1.3/codekendra/api/update_profile.php",
                    response -> {
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    error -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("uid", String.valueOf(sessionManager.getUserId()));
                    params.put("display_name", displayName);
                    params.put("username", username);
                    params.put("bio", bio);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });
    }
}
