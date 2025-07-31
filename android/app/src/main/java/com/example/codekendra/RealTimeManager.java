package com.example.codekendra;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealTimeManager {
    private static final String TAG = "RealTimeManager";
    private static RealTimeManager instance;
    private PieSocketClient client;
    private final List<RealTimeListener> listeners = new ArrayList<>();
    private Handler mainHandler;
    private Handler reconnectHandler;
    private boolean isConnected = false;
    private boolean shouldReconnect = true;
    private int reconnectAttempts = 0;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final long RECONNECT_DELAY_MS = 3000;

    public interface RealTimeListener {
        void onEvent(String eventType, JSONObject data);
        void onConnectionStateChanged(boolean connected);
    }

    private RealTimeManager() {
        mainHandler = new Handler(Looper.getMainLooper());
        reconnectHandler = new Handler(Looper.getMainLooper());
        connect();
    }

    public static synchronized RealTimeManager getInstance() {
        if (instance == null) {
            instance = new RealTimeManager();
        }
        return instance;
    }

    private void connect() {
        try {
            if (client != null && !client.isClosed()) {
                client.close();
            }

            URI uri = new URI("wss://s15004.nyc1.piesocket.com/v3/CODEKENDRA_WEBSOCKET?notify_self=1");
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer EiQOSBF2l4E6OManqyUZOslqgBz75U0vPNBKQiAN");

            client = new PieSocketClient(uri, headers) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d(TAG, "WebSocket Connected Successfully");
                    isConnected = true;
                    reconnectAttempts = 0;

                    mainHandler.post(() -> {
                        for (RealTimeListener listener : new ArrayList<>(listeners)) {
                            try {
                                listener.onConnectionStateChanged(true);
                            } catch (Exception e) {
                                Log.e(TAG, "Error notifying connection state", e);
                            }
                        }
                    });
                }

                @Override
                public void onMessage(String message) {
                    Log.d(TAG, "Raw WebSocket message: " + message);

                    mainHandler.post(() -> {
                        try {
                            JSONObject obj = new JSONObject(message);
                            String event = obj.optString("event");
                            JSONObject data = obj.optJSONObject("data");

                            Log.d(TAG, "Parsed event: " + event);

                            for (RealTimeListener listener : new ArrayList<>(listeners)) {
                                try {
                                    listener.onEvent(event, data);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error notifying listener", e);
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing WebSocket message", e);
                        }
                    });
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.w(TAG, "WebSocket Disconnected - Code: " + code + ", Reason: " + reason);
                    isConnected = false;

                    mainHandler.post(() -> {
                        for (RealTimeListener listener : new ArrayList<>(listeners)) {
                            try {
                                listener.onConnectionStateChanged(false);
                            } catch (Exception e) {
                                Log.e(TAG, "Error notifying connection state", e);
                            }
                        }
                    });

                    if (shouldReconnect && reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                        scheduleReconnect();
                    }
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "WebSocket Error", ex);
                    isConnected = false;
                }
            };

            client.connect();
            Log.d(TAG, "Attempting WebSocket connection...");

        } catch (Exception e) {
            Log.e(TAG, "Error creating WebSocket connection", e);
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
        if (!shouldReconnect || reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            return;
        }

        reconnectAttempts++;
        long delay = RECONNECT_DELAY_MS * reconnectAttempts;

        Log.d(TAG, "Scheduling reconnection attempt " + reconnectAttempts + " in " + delay + "ms");

        reconnectHandler.postDelayed(() -> {
            if (shouldReconnect && !isConnected) {
                connect();
            }
        }, delay);
    }

    public void addListener(RealTimeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            Log.d(TAG, "Listener added. Total listeners: " + listeners.size());
            listener.onConnectionStateChanged(isConnected);
        }
    }

    public void removeListener(RealTimeListener listener) {
        listeners.remove(listener);
        Log.d(TAG, "Listener removed. Total listeners: " + listeners.size());
    }

    public void sendEvent(String event, JSONObject data) {
        if (!isConnected || client == null) {
            Log.w(TAG, "Cannot send event - WebSocket not connected");
            return;
        }

        try {
            JSONObject obj = new JSONObject();
            obj.put("event", event);
            obj.put("data", data);

            client.send(obj.toString());
            Log.d(TAG, "Sent event: " + event);
        } catch (JSONException e) {
            Log.e(TAG, "Error sending event", e);
        }
    }

    public boolean isConnected() {
        return isConnected && client != null && !client.isClosed();
    }
}