package com.example.mylibrary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ChatMessage> list;

    public ChatAdapter(ArrayList<ChatMessage> list) {
        this.list = list;
    }

    int USER = 1;
    int AI = 2;

    @Override
    public int getItemViewType(int position) {
        return list.get(position).isUser() ? USER : AI;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == USER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user, parent, false);
            return new UserHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_ai, parent, false);
            return new AiHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ChatMessage chat = list.get(position);

        if (holder instanceof UserHolder) {
            ((UserHolder) holder).tv.setText(chat.getMessage());
        } else {
            ((AiHolder) holder).tv.setText(chat.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvUser);
        }
    }

    class AiHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public AiHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvAi);
        }
    }
}