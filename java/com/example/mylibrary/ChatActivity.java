package com.example.mylibrary;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerChat;
    EditText etMessage;
    ImageView btnSend, btnBack;

    ArrayList<ChatMessage> list;
    ChatAdapter adapter;

    String API_KEY = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 STATUS BAR BIRU
        getWindow().setStatusBarColor(
                Color.parseColor("#1976D2")
        );

        setContentView(R.layout.activity_chat);

        recyclerChat = findViewById(R.id.recyclerChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);

        list = new ArrayList<>();
        adapter = new ChatAdapter(list);

        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(adapter);

        list.add(new ChatMessage("Halo 👋 Saya AI Buku. Tanya apa saja soal buku.", false));
        adapter.notifyDataSetChanged();

        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {

        String msg = etMessage.getText().toString().trim();

        if(msg.isEmpty()) return;

        list.add(new ChatMessage(msg, true));
        adapter.notifyDataSetChanged();

        etMessage.setText("");

        askAI(msg);
    }

    private void askAI(String userMsg){

        String url = "https://openrouter.ai/api/v1/chat/completions";

        try {

            JSONObject json = new JSONObject();

            json.put("model", "openrouter/free");

            JSONArray messages = new JSONArray();

            JSONObject system = new JSONObject();
            system.put("role","system");
            system.put("content",
                    "Kamu adalah AI khusus buku. " +
                            "Jawab hanya pertanyaan tentang buku, novel, penulis, rekomendasi buku, sinopsis buku. " +
                            "Jika user bertanya selain buku, jawab: Maaf saya hanya bisa membahas seputar buku.");

            JSONObject user = new JSONObject();
            user.put("role","user");
            user.put("content", userMsg);

            messages.put(system);
            messages.put(user);

            json.put("messages", messages);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    json,

                    response -> {
                        try {

                            JSONArray choices = response.getJSONArray("choices");

                            JSONObject obj = choices.getJSONObject(0);

                            JSONObject msgObj = obj.getJSONObject("message");

                            String reply = msgObj.getString("content");

                            list.add(new ChatMessage(reply,false));
                            adapter.notifyDataSetChanged();

                            recyclerChat.scrollToPosition(list.size()-1);

                        } catch (Exception e){
                            list.add(new ChatMessage("AI error membaca jawaban.", false));
                            adapter.notifyDataSetChanged();
                        }
                    },

                    error -> {
                        list.add(new ChatMessage("AI gagal terhubung.", false));
                        adapter.notifyDataSetChanged();
                    }

            ){

                @Override
                public java.util.Map<String, String> getHeaders(){

                    java.util.Map<String,String> headers = new java.util.HashMap<>();

                    headers.put("Authorization","Bearer " + API_KEY);
                    headers.put("Content-Type","application/json");

                    return headers;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);

        } catch (Exception e){

            list.add(new ChatMessage("Terjadi kesalahan.", false));
            adapter.notifyDataSetChanged();
        }
    }
}