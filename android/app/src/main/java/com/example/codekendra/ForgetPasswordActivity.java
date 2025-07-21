package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private Button btnSendCode;
    private String SEND_CODE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpassword);

        SEND_CODE_URL = "http://"
                + getString(R.string.server_ip)
                + "/codekendra/api/send_reset_code.php";

        etEmail     = findViewById(R.id.forgotEmailInput);
        btnSendCode = findViewById(R.id.ForgetBtn);

        btnSendCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email).matches()) {
                etEmail.setError("Enter a valid email");
                return;
            }
            sendResetCode(email);
        });
    }

    private void sendResetCode(String email) {
        StringRequest req = new StringRequest(
                Request.Method.POST,
                SEND_CODE_URL,
                response -> {
                    Log.d("SEND_RAW", response);
                    try {
                        JSONObject o = new JSONObject(response);
                        String status = o.getString("status");
                        if ("reset_sent".equals(status)) {
                            Toast.makeText(this,
                                    "Check your email for the reset code",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this,
                                    EnterResetCodeActivity.class)
                                    .putExtra("email", email));
                            finish();
                        } else {
                            String err = o.optString("error", "Unknown error");
                            Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("SEND_PARSE_ERR", e.toString());
                        Toast.makeText(this,
                                "Could not parse server response",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String msg = "Network error";
                    if (error.networkResponse != null &&
                            error.networkResponse.data != null) {
                        msg = new String(error.networkResponse.data);
                        Log.e("VOLLEY_ERR",
                                "Code=" + error.networkResponse.statusCode +
                                        " Body=" + msg);
                    }
                    Toast.makeText(this,
                            "Network error: " + msg,
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

        req.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        Volley.newRequestQueue(this).add(req);
    }
}
