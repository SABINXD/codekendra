package com.example.codekendra;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealTimeManager {

    private static RealTimeManager instance;
    private PieSocketClient client;
    private final List<RealTimeListener> listeners = new ArrayList<>();

    public interface RealTimeListener {
        void onEvent(String eventType, JSONObject data);
    }

    private RealTimeManager() {
        try {
            URI uri = new URI("wss://s15004.nyc1.piesocket.com/v3/CODEKENDRA_WEBSOCKET?notify_self=1");

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer EiQOSBF2l4E6OManqyUZOslqgBz75U0vPNBKQiAN");  // Use Access Token for production

            client = new PieSocketClient(uri, headers) {
                @Override
                public void onMessage(String message) {
                    Log.d("RealTimeManager", "Raw WebSocket message: " + message);
                    try {
                        JSONObject obj = new JSONObject(message);
                        String event = obj.optString("event");
                        JSONObject data = obj.optJSONObject("data");

                        Log.d("RealTimeManager", "Parsed event: " + event);
                        Log.d("RealTimeManager", "Data payload: " + (data != null ? data.toString() : "null"));

                        for (RealTimeListener l : listeners) {
                            l.onEvent(event, data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            client.connect();
            Log.d("RealTimeManager", "WebSocket Connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RealTimeManager getInstance() {
        if (instance == null) {
            instance = new RealTimeManager();
        }
        return instance;
    }

    public void addListener(RealTimeListener l) {
        listeners.add(l);
    }

    public void removeListener(RealTimeListener l) {
        listeners.remove(l);
    }

    public void sendEvent(String event, JSONObject data) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("event", event);
        obj.put("data", data);
        client.send(obj.toString());
        Log.d("RealTimeManager", "Sent event: " + event + ", Data: " + data.toString());
    }
}
