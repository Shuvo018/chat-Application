package com.example.chatapplication104;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.chatapplication104.adapter.RecentChatRecyclerAdapter;
import com.example.chatapplication104.model.ChatRoomModel;
import com.example.chatapplication104.utils.AndroidUtil;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    ImageView menuBtn, groupBtn, searchBtn;
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuBtn = findViewById(R.id.menuBtnId);
        groupBtn = findViewById(R.id.groupBtnId);
        searchBtn = findViewById(R.id.searchBtnId);
        recyclerView = findViewById(R.id.MainPageRecyclerViewId);

        menuBtn.setOnClickListener(v -> {
//            AndroidUtil.showToast(this, "menu ");
            startActivity(new Intent(this, menuActivity.class));
        });
        groupBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, groupsActivity.class));
        });
        searchBtn.setOnClickListener(v -> {
//            AndroidUtil.showToast(this, "menu ");
            startActivity(new Intent(this, searchActivity.class));

        });
        setupRecyclerView();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                Log.d("adapter : ", "run: ");
            }
        });

    }

    private void setupRecyclerView() {
        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query, ChatRoomModel.class).build();
        adapter = new RecentChatRecyclerAdapter(options,getApplicationContext());
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