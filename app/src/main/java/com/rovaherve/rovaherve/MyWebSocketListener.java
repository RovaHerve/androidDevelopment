package com.rovaherve.rovaherve;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import android.util.Log;

public class MyWebSocketListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send("mobile phone connected");
        webSocket.send("bidirectionnal communication started: ");
        // webSocket.send("What's up?");
        // webSocket.send(ByteString.decodeHex("deadbeef"));
        // webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d("WebSocket", "Receiving: " + text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.d("WebSocket", "Receiving bytes: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        Log.d("WebSocket", "Closing: " + code + " / " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e("WebSocket", "Error: " + t.getMessage(), t);
    }
}
