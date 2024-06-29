package com.example.chatapplication104;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapplication104.adapter.createGroupUserRecyclerAdapter;
import com.example.chatapplication104.adapter.searchUserRecyclerAdapter;
import com.example.chatapplication104.model.ChatGroupModel;
import com.example.chatapplication104.model.ChatMessageModel;
import com.example.chatapplication104.model.ChatRoomModel;
import com.example.chatapplication104.model.userModel;
import com.example.chatapplication104.utils.AndroidUtil;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.Random;

public class createGroupActivity extends AppCompatActivity {
    ImageView backBtn, checkMarkId, searchBtn;
    EditText searchET, groupNameInput;
    RecyclerView recyclerView;
    String chatRoomId;
    createGroupUserRecyclerAdapter adapter;
    ChatGroupModel chatGroupModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);




        backBtn = findViewById(R.id.leftArrowMVId);
        checkMarkId = findViewById(R.id.checkMarkMVId);
        groupNameInput = findViewById(R.id.createGroupNameETId);
        searchET = findViewById(R.id.createGroupSearchUsersETId);
        searchBtn = findViewById(R.id.searchMVId);
        recyclerView = findViewById(R.id.recyclerCreateGroupId);

        backBtn.setOnClickListener(v -> onBackPressed());

        setupSearchRecyclerView("a");
        adapter.notifyDataSetChanged();
        searchBtn.setOnClickListener(v -> {
            if(searchET.getText().toString().isEmpty() || searchET.getText().length()<2){
                searchET.setError("Invalid username"); return;
            }
            setupSearchRecyclerView(searchET.getText().toString());
        });
        checkMarkId.setOnClickListener(v -> {
            if(!AndroidUtil.getcreateGroupUserList().isEmpty()){
//                AndroidUtil.showToast(createGroupActivity.this, "it's not empty ");
            }else{
//                AndroidUtil.showToast(createGroupActivity.this, "it's empty ");
            }
            if (groupNameInput.getText().toString().isEmpty()){
                groupNameInput.setError("pls, enter group name"); return;
            }
            int r = new Random().nextInt(100000);
            AndroidUtil.showToast(createGroupActivity.this, r+"");
            chatRoomId = FirebaseUtil.currentUserId()+r;
            AndroidUtil.setcreateGroupUserList(FirebaseUtil.currentUserId());

            chatGroupModel = new ChatGroupModel(
                    chatRoomId,
                    AndroidUtil.getcreateGroupUserList(),
                    Timestamp.now(),
                    FirebaseUtil.currentUserId(),
                    "hi",
                    groupNameInput.getText().toString()
            );
            FirebaseUtil.getChatGroupRoomReference(chatRoomId).set(chatGroupModel);

            ChatMessageModel chatMessageModel = new ChatMessageModel("hi", FirebaseUtil.currentUserId(), Timestamp.now());
            FirebaseUtil.getGroupChatRooMessageRef(chatRoomId).add(chatMessageModel);
            startActivity(new Intent(createGroupActivity.this, groupsActivity.class));
        });
    }

    private void setupSearchRecyclerView(String searchUser) {
        Query query = FirebaseUtil.allUserCollectionReference().whereGreaterThanOrEqualTo("username",searchUser);

        FirestoreRecyclerOptions<userModel> options = new FirestoreRecyclerOptions.Builder<userModel>()
                .setQuery(query, userModel.class).build();
        adapter = new createGroupUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }
}