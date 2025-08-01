package com.example.codekendra;

import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.Map;

public class PieSocketClient extends WebSocketClient {
    private static final String TAG = "PieSocketClient";

    public PieSocketClient(URI serverUri, Map<String, String> headers) {
        super(serverUri, headers);
        Log.d(TAG, "Creating WebSocket client with URI: " + serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        Log.d(TAG, "WebSocket Connected");
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "Received message: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "Disconnected - Code: " + code + ", Reason: " + reason + ", Remote: " + remote);
    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "WebSocket Error", ex);
    }
}