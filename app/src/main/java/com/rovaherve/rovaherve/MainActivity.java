package com.rovaherve.rovaherve;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.WebSocketListener;
import okio.ByteString;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private OkHttpClient client;
    private TextView textView;
    private EditText editText;
    private Button buttonSend;
    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);
        buttonSend = findViewById(R.id.buttonSend);

        Request request = new Request.Builder().url("ws://192.168.165.63:8765").build();
        MyWebSocketListener listener = new MyWebSocketListener();
        // WebSocket ws = client.newWebSocket(request, listener);
        webSocket = client.newWebSocket(request, listener);

        buttonSend.setOnClickListener(v -> {
            String message = editText.getText().toString();
            if (!message.isEmpty()) {
                webSocket.send(message);
                editText.setText("");
            }
        });

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();
    }

    public void updateTextView(String message) {
        runOnUiThread(() -> textView.setText(message));
    }
}