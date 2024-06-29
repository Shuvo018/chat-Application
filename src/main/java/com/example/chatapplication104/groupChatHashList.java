package com.example.chatapplication104;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.chatapplication104.adapter.HashListAdapter;
import com.example.chatapplication104.adapter.createGroupUserRecyclerAdapter;
import com.example.chatapplication104.model.ChatGroupModel;
import com.example.chatapplication104.model.HashModel;
import com.example.chatapplication104.model.userModel;
import com.example.chatapplication104.utils.AndroidUtil;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class groupChatHashList extends AppCompatActivity {
    RecyclerView recyclerView;
    HashListAdapter adapter;

    String gchatroomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_hash_list);

        recyclerView = findViewById(R.id.groupChatHashListRecycleID);

        gchatroomId = AndroidUtil.getGroupChatIdAsIntent(getIntent());

        setupSearchRecyclerView("A");
    }
    private void setupSearchRecyclerView(String k) {
        Query query = FirebaseUtil.getGroupChatRooMessageHashRef(gchatroomId).whereGreaterThanOrEqualTo("key",k);

        FirestoreRecyclerOptions<HashModel> options = new FirestoreRecyclerOptions.Builder<HashModel>()
                .setQuery(query, HashModel.class).build();
        adapter = new HashListAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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