package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EnterVerificationCodeActivity extends AppCompatActivity {

    EditText etCode;
    Button btnVerify;
    TextView resendCode;
    String email;
    String VERIFY_URL;
    String RESEND_URL;

    private boolean isCooldownActive = false;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_verification_code);

        etCode = findViewById(R.id.etCode);
        btnVerify = findViewById(R.id.btnVerifyCode);
        resendCode = findViewById(R.id.resend_code_verify);

        email = getIntent().getStringExtra("email");

        VERIFY_URL = "http://" + getString(R.string.server_ip) + "/codekendra/api/verify_code.php";
        RESEND_URL = "http://" + getString(R.string.server_ip) + "/codekendra/api/send_verification.php";

        btnVerify.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();
            if (code.length() != 6) {
                etCode.setError("Enter 6-digit code");
                return;
            }
            verifyAccountCode(email, code);
        });

        resendCode.setOnClickListener(v -> {
            if (isCooldownActive) {
                Toast.makeText(this, "Please wait 60 seconds before resending", Toast.LENGTH_SHORT).show();
            } else {
                resendVerificationCode(email);
                startCooldownTimer();
            }
        });

        // 🔒 Block back button until verified
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(getApplicationContext(), "Please verify your account to continue", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Missing email context", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void verifyAccountCode(String email, String code) {
        StringRequest request = new StringRequest(Request.Method.POST, VERIFY_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("status").equals("verified")) {
                            Toast.makeText(this, "Account Verified! Please log in.", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("email", email);
                map.put("code", code);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void resendVerificationCode(String email) {
        StringRequest request = new StringRequest(Request.Method.POST, RESEND_URL,
                response -> Toast.makeText(this, "Verification code has been resent", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(this, "Resend failed: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("purpose", "verify");
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void startCooldownTimer() {
        isCooldownActive = true;
        resendCode.setEnabled(false);
        resendCode.setAlpha(0.5f);
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                resendCode.setText("Resend in " + (millisUntilFinished / 1000) + "s");
            }

            public void onFinish() {
                isCooldownActive = false;
                resendCode.setEnabled(true);
                resendCode.setAlpha(1f);
                resendCode.setText("Resend code");
            }
        }.start();
    }
}
