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

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText etPass, etConfirm;
    private Button btnReset;
    private String RESET_URL;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = getIntent().getStringExtra("email");
        RESET_URL = "http://"
                + getString(R.string.server_ip)
                + "/codekendra/api/reset_password.php";

        etPass    = findViewById(R.id.etNewPassword);
        etConfirm = findViewById(R.id.ConfirmPassword);
        btnReset  = findViewById(R.id.btnReset);

        btnReset.setOnClickListener(v -> {
            String p1 = etPass.getText().toString().trim();
            String p2 = etConfirm.getText().toString().trim();
            if (p1.length() < 6) {
                etPass.setError("At least 6 characters");
                return;
            }
            if (!p1.equals(p2)) {
                etConfirm.setError("Passwords don’t match");
                return;
            }
            resetPassword(email, p1);
        });
    }

    private void resetPassword(String email, String pass) {
        StringRequest req = new StringRequest(
                Request.Method.POST,
                RESET_URL,
                response -> {
                    Log.d("RESET_RAW", response);
                    try {
                        JSONObject o = new JSONObject(response);
                        String status = o.getString("status");
                        if ("success".equals(status)) {
                            Toast.makeText(this,
                                    "Password changed!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String err = o.optString("error","Unknown error");
                            Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("RESET_PARSE_ERR", e.toString());
                        Toast.makeText(this,
                                "Server parse error",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this,
                            "Network error resetting password",
                            Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("email", email);
                p.put("password", pass);
                return p;
            }
        };
        Volley.newRequestQueue(this).add(req);
    }
}
