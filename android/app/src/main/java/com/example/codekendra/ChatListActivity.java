package com.example.codekendra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {

    private static final String TAG = "ChatListActivity";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChatListAdapter adapter;
    private SessionManager sessionManager;
    private String serverIp;
    private LinearLayout emptyStateLayout;
    private EditText searchEditText;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Initialize components
        sessionManager = new SessionManager(this);
        serverIp = getString(R.string.server_ip);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Messages");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        searchEditText = findViewById(R.id.search_edit_text);

        // Setup RecyclerView
        adapter = new ChatListAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        swipeRefreshLayout.setOnRefreshListener(this::loadChatList);

        // Set item click listener
        adapter.setOnItemClickListener((chatUser, position) -> {
            Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
            intent.putExtra("RECIPIENT_ID", chatUser.getUserId());
            intent.putExtra("RECIPIENT_USERNAME", chatUser.getUserName()); // Pass the username
            intent.putExtra("RECIPIENT_PROFILE_PIC", chatUser.getProfilePic()); // Pass the profile pic
            startActivity(intent);
        });

        // Load chat list
        loadChatList();

        Log.d(TAG, "ChatListActivity created successfully");
    }

    private void loadChatList() {
        if (isLoading) {
            return;
        }

        isLoading = true;
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        String url = "http://" + serverIp + "/codekendra/api/get_chat_list.php";

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(sessionManager.getUserId()));

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(params),
                response -> {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);

                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray chatListArray = response.getJSONArray("chat_list");
                            List<ChatUser> chatUsers = new Gson().fromJson(
                                    chatListArray.toString(),
                                    new TypeToken<List<ChatUser>>() {}.getType()
                            );

                            if (chatUsers.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                emptyStateLayout.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyStateLayout.setVisibility(View.GONE);
                                adapter.updateChatList(chatUsers);
                            }
                        } else {
                            Toast.makeText(this, "Failed to load chat list", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing chat list", e);
                        Toast.makeText(this, "Error loading chat list", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Log.e(TAG, "Error loading chat list", error);
                    Toast.makeText(this, "Error loading chat list", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh chat list when returning to this activity
        loadChatList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
