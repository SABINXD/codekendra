package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final int MESSAGE_REFRESH_INTERVAL = 5000; // 5 seconds

    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageView backButton;
    private ImageView recipientProfilePic;
    private TextView recipientUsername;
    private MessageAdapter adapter;
    private SessionManager sessionManager;
    private int currentUserId;
    private int recipientId;
    private String recipientUserName;
    private String recipientProfileImageUrl;
    private String serverIp;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isLoadingMessages = false;

    // For periodic message refreshing
    private Handler messageRefreshHandler;
    private Runnable messageRefreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize components
        sessionManager = new SessionManager(this);
        serverIp = getString(R.string.server_ip);
        currentUserId = sessionManager.getUserId();

        // Get recipient ID, username, and profile pic from intent
        recipientId = getIntent().getIntExtra("RECIPIENT_ID", -1);
        recipientUserName = getIntent().getStringExtra("RECIPIENT_USERNAME");
        recipientProfileImageUrl = getIntent().getStringExtra("RECIPIENT_PROFILE_PIC");

        if (recipientId == -1) {
            Toast.makeText(this, "Invalid recipient", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Chat started with recipient ID: " + recipientId);

        // Initialize UI
        recyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        backButton = findViewById(R.id.back_button);
        recipientProfilePic = findViewById(R.id.profile_pic);
        recipientUsername = findViewById(R.id.username);
        swipeRefreshLayout = findViewById(R.id.chat_layout);

        // Set recipient username and load profile pic
        if (recipientUsername != null) {
            recipientUsername.setText(recipientUserName);
        }

        if (recipientProfileImageUrl != null && !recipientProfileImageUrl.isEmpty()) {
            String fullProfilePicUrl = "http://" + serverIp + "/codekendra/web/assets/img/profile/" + recipientProfileImageUrl;
            Glide.with(this)
                    .load(fullProfilePicUrl)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(recipientProfilePic);
        } else {
            recipientProfilePic.setImageResource(R.drawable.profile_placeholder);
        }

        // Setup RecyclerView
        adapter = new MessageAdapter(currentUserId);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ((LinearLayoutManager) recyclerView.getLayoutManager()).setStackFromEnd(true);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        swipeRefreshLayout.setOnRefreshListener(this::loadMessages);

        // Setup click listeners
        sendButton.setOnClickListener(v -> sendMessage());
        backButton.setOnClickListener(v -> finish());

        // Initialize message refresh handler
        messageRefreshHandler = new Handler();
        messageRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                messageRefreshHandler.postDelayed(this, MESSAGE_REFRESH_INTERVAL);
            }
        };

        // Load messages
        loadMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessages();
        // Start periodic message refresh
        messageRefreshHandler.postDelayed(messageRefreshRunnable, MESSAGE_REFRESH_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop periodic message refresh
        messageRefreshHandler.removeCallbacks(messageRefreshRunnable);
    }

    private void loadMessages() {
        if (isLoadingMessages) {
            return;
        }

        isLoadingMessages = true;
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        String url = "http://" + serverIp + "/codekendra/api/get_messages.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    isLoadingMessages = false;
                    swipeRefreshLayout.setRefreshing(false);

                    Log.d(TAG, "Messages response: " + response);

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray messagesArray = jsonResponse.getJSONArray("messages");
                            List<Message> messages = new Gson().fromJson(
                                    messagesArray.toString(),
                                    new TypeToken<List<Message>>() {}.getType()
                            );
                            adapter.updateMessages(messages);
                            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                            Log.d(TAG, "Loaded " + messages.size() + " messages");
                        } else {
                            String errorMsg = jsonResponse.optString("message", "Unknown error");
                            Toast.makeText(this, "Failed to load messages: " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing messages", e);
                        Toast.makeText(this, "Error parsing messages", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    isLoadingMessages = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Log.e(TAG, "Error loading messages", error);
                    Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("from_user_id", String.valueOf(currentUserId));
                params.put("to_user_id", String.valueOf(recipientId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }

        // Disable send button to prevent duplicate messages
        sendButton.setEnabled(false);
        sendButton.setAlpha(0.5f);

        String url = "http://" + serverIp + "/codekendra/api/send_message.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Re-enable send button
                    sendButton.setEnabled(true);
                    sendButton.setAlpha(1.0f);

                    Log.d(TAG, "Send message response: " + response);

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            // Clear input
                            messageInput.setText("");

                            // Create a new message object and add it locally
                            Message message = new Message(currentUserId, recipientId, messageText);
                            adapter.addMessage(message);
                            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

                            // Immediately refresh messages to get the server timestamp
                            loadMessages();
                        } else {
                            String errorMsg = jsonResponse.optString("message", "Unknown error");
                            Toast.makeText(this, "Failed to send message: " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error sending message", e);
                        Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Re-enable send button
                    sendButton.setEnabled(true);
                    sendButton.setAlpha(1.0f);

                    Log.e(TAG, "Error sending message", error);
                    Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("from_user_id", String.valueOf(currentUserId));
                params.put("to_user_id", String.valueOf(recipientId));
                params.put("message", messageText);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}