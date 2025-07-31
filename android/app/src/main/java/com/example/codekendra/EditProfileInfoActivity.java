package com.example.codekendra;

import android.os.Bundle;
import android.util.Log;
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
    private static final String TAG = "EditProfile";

    EditText editFirstName, editLastName, editUsername, editBio;
    Button btnUpdateProfile;
    SessionManager sessionManager;
    String serverIp;
    String URL_UPDATE;
    String URL_GET_PROFILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_info);

        initializeComponents();
        loadCurrentProfile();
        setupClickListeners();
    }

    private void initializeComponents() {
        sessionManager = new SessionManager(this);
        serverIp = getString(R.string.server_ip);

        URL_UPDATE = "http://" + serverIp + "/codekendra/api/edit_profile.php";
        URL_GET_PROFILE = "http://" + serverIp + "/codekendra/api/get_profile_info.php";

        editFirstName = findViewById(R.id.edit_first_name);
        editLastName = findViewById(R.id.edit_last_name);
        editUsername = findViewById(R.id.edit_username);
        editBio = findViewById(R.id.edit_bio);
        btnUpdateProfile = findViewById(R.id.btn_update_profile);
    }

    private void setupClickListeners() {
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    private void loadCurrentProfile() {
        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_PROFILE,
                response -> {
                    try {
                        Log.d(TAG, "Profile response: " + response);
                        JSONObject obj = new JSONObject(response);

                        if ("success".equals(obj.optString("status"))) {
                            JSONObject user = obj.optJSONObject("user");
                            if (user != null) {
                                editFirstName.setText(user.optString("first_name", ""));
                                editLastName.setText(user.optString("last_name", ""));
                                editUsername.setText(user.optString("username", ""));
                                editBio.setText(user.optString("bio", ""));
                            }
                        } else {
                            Toast.makeText(this, "❌ Failed to load profile", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing profile", e);
                        Toast.makeText(this, "❌ Error loading profile", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error", error);
                    Toast.makeText(this, "❌ Network error", Toast.LENGTH_SHORT).show();
                }
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

    private void updateProfile() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String bio = editBio.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "❌ Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATE,
                response -> {
                    try {
                        Log.d(TAG, "Update response: " + response);
                        JSONObject obj = new JSONObject(response);

                        if ("success".equals(obj.optString("status"))) {
                            Toast.makeText(this, "✅ Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Go back to profile
                        } else {
                            String error = obj.optString("error", "Update failed");
                            Toast.makeText(this, "❌ " + error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing update response", e);
                        Toast.makeText(this, "❌ Error updating profile", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error during update", error);
                    Toast.makeText(this, "❌ Network error", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(sessionManager.getUserId()));
                params.put("email", sessionManager.getEmail());
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("username", username);
                params.put("gender", "0");
                params.put("bio", bio);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
