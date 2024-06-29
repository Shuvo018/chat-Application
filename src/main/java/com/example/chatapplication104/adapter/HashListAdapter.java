package com.example.chatapplication104.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication104.R;
import com.example.chatapplication104.model.HashModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class HashListAdapter extends FirestoreRecyclerAdapter<HashModel, HashListAdapter.HashListViewHolder> {

    Context context;
    public HashListAdapter(@NonNull FirestoreRecyclerOptions<HashModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull HashListViewHolder holder, int position, @NonNull HashModel model) {
        holder.hashKey.setText("##g"+model.getKey());
    }

    @NonNull
    @Override
    public HashListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_chat_hashlis_row, parent, false);
        return new HashListViewHolder(view);
    }

    class HashListViewHolder extends RecyclerView.ViewHolder{
        TextView hashKey;

        public HashListViewHolder(@NonNull View itemView) {
            super(itemView);

            hashKey = itemView.findViewById(R.id.group_chat_hashkey_id);
        }
    }
}
