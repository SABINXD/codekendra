package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class EnterResetCodeActivity extends AppCompatActivity {
    private EditText etCode;
    private Button btnVerify;
    private TextView tvResend;
    private String email;
    private String VERIFY_URL;
    private String RESEND_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_reset_code);

       
        email = getIntent().getStringExtra("email");

    
        VERIFY_URL = "http://" + getString(R.string.server_ip)
                + "/codekendra/api/verify_reset_code.php";
        RESEND_URL = "http://" + getString(R.string.server_ip)
                + "/codekendra/api/send_reset_code.php";

        etCode    = findViewById(R.id.etCode);
        btnVerify = findViewById(R.id.btnVerifyCode);
        tvResend  = findViewById(R.id.resend_code_reset);
        btnVerify.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();
            if (code.length() != 6) {
                etCode.setError("Enter the 6-digit code");
            } else {
                verifyCode(email, code);
            }
        });

        // Resend link
        tvResend.setOnClickListener(v -> resendCode(email));
    }

    private void verifyCode(String email, String code) {
        StringRequest req = new StringRequest(
                Request.Method.POST,
                VERIFY_URL,
                response -> {
                    Log.d("VERIFY_RAW", response);
                    try {
                        JSONObject o = new JSONObject(response);
                        String status = o.getString("status");
                        switch (status) {
                            case "verified":
                                Toast.makeText(this,
                                        "Code verified!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, ResetPasswordActivity.class)
                                        .putExtra("email", email));
                                finish();
                                break;
                            case "expired":
                                Toast.makeText(this,
                                        "Code expired. Request again.",
                                        Toast.LENGTH_LONG).show();
                                finish();
                                break;
                            default:
                                Toast.makeText(this,
                                        "Invalid code. Try again.",
                                        Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("VERIFY_PARSE_ERR", e.toString());
                        Toast.makeText(this,
                                "Server response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String msg = "Network error";
                    if (error.networkResponse != null &&
                            error.networkResponse.data != null) {
                        msg = new String(error.networkResponse.data,
                                StandardCharsets.UTF_8);
                        Log.e("VOLLEY_ERR",
                                "Code=" + error.networkResponse.statusCode +
                                        " Body=" + msg);
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("email", email);
                p.put("code", code);
                return p;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        Volley.newRequestQueue(this).add(req);
    }

    private void resendCode(String email) {
        StringRequest req = new StringRequest(
                Request.Method.POST,
                RESEND_URL,
                response -> {
                    Log.d("RESEND_RAW", response);
                    try {
                        JSONObject o = new JSONObject(response);
                        String status = o.getString("status");
                        if ("reset_sent".equals(status)) {
                            Toast.makeText(this,
                                    "New code sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this,
                                    o.optString("error","Could not resend"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("RESEND_PARSE_ERR", e.toString());
                        Toast.makeText(this,
                                "Server error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this,
                            "Network error resending code",
                            Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("email", email);
                return p;
            }
        };

        Volley.newRequestQueue(this).add(req);
    }
}
