package com.example.codekendra;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

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

        fetchProfile(); // auto-fill previous values

        updateBtn.setOnClickListener(v -> {
            String displayName = displayNameInput.getText().toString().trim();
            String username    = usernameInput.getText().toString().trim();
            String bio         = bioInput.getText().toString().trim();

            if (displayName.isEmpty() && username.isEmpty() && bio.isEmpty()) {
                Toast.makeText(this, "Please modify at least one field", Toast.LENGTH_SHORT).show();
                return;
            }

            StringRequest request = new StringRequest(Request.Method.POST,
                    "http://"+getString(R.string.server_ip)+"/codekendra/api/update_profile.php",
                    response -> {
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // returns to ProfileActivity
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

    private void fetchProfile() {
        StringRequest request = new StringRequest(Request.Method.POST,
                "http://" +getString(R.string.server_ip)
                        +"/codekendra/api/get_profile_info.php",
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("status").equalsIgnoreCase("success")) {
                            JSONObject user = obj.getJSONObject("user");
                            String fullName = user.optString("first_name", "") + " " + user.optString("last_name", "");
                            displayNameInput.setText(fullName);
                            usernameInput.setText(user.optString("username", ""));
                            bioInput.setText(user.optString("bio", ""));
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load current data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading profile info", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", String.valueOf(sessionManager.getUserId()));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
