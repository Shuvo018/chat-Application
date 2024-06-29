package com.example.chatapplication104;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapplication104.adapter.RecentGroupAdapter;
import com.example.chatapplication104.model.ChatGroupModel;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class groupsActivity extends AppCompatActivity {
    ImageView menuBtn, singleChatBtn;
    RecyclerView recyclerView;
    RecentGroupAdapter adapter;
    Button createGroupBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        menuBtn = findViewById(R.id.gmenuBtnId);
        singleChatBtn = findViewById(R.id.gsingleChatBtnId);
        recyclerView = findViewById(R.id.groupRecyclerViewId);
        createGroupBtn = findViewById(R.id.creaateGroupBtnid);

        menuBtn.setOnClickListener(v -> {
            startActivity(new Intent(groupsActivity.this, menuActivity.class));
        });
        singleChatBtn.setOnClickListener(v -> {
            startActivity(new Intent(groupsActivity.this, MainActivity.class));
        });

        createGroupBtn.setOnClickListener(v -> {
            startActivity(new Intent(groupsActivity.this, createGroupActivity.class));
        });
        setupRecyclerView(FirebaseUtil.currentUserId());
        adapter.notifyDataSetChanged();
    }
    private void setupRecyclerView(String id) {
        Query query = FirebaseUtil.getallChatGroupRoomReference()
                .whereArrayContains("userIds", id)
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatGroupModel> options = new FirestoreRecyclerOptions.Builder<ChatGroupModel>()
                .setQuery(query, ChatGroupModel.class).build();
        adapter = new RecentGroupAdapter(options,getApplicationContext());
//        runOnUiThread(() -> adapter.notifyDataSetChanged());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null){
//            AndroidUtil.showToast(this, "on start");
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter != null){
//            AndroidUtil.showToast(this, "on stop");
            adapter.stopListening();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null){
//            AndroidUtil.showToast(this, "on resume");
            adapter.notifyDataSetChanged();
        }
    }
}