package com.example.chatapplication104;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import com.example.chatapplication104.adapter.ChatRecyclerAdapter;
import com.example.chatapplication104.model.ChatMessageModel;
import com.example.chatapplication104.model.ChatRoomModel;
import com.example.chatapplication104.model.userModel;
import com.example.chatapplication104.utils.AndroidUtil;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    ImageView backBtn, otherUserProfile;
    TextView otherUserName;
    RecyclerView recyclerView;
    EditText messageInput;
    ImageButton imgBtn, sendBtn;
    RelativeLayout imageViewfieldLayout;
    ImageView imageView, closeImageView;
    Uri imageUri;
    userModel otherUserModel;
    ChatRoomModel chatRoomModel;
    String chatRoomId;
    ChatRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        backBtn = findViewById(R.id.ChatbackBtnId);
        otherUserProfile = findViewById(R.id.profile_image_view_id);
        otherUserName = findViewById(R.id.OtherUserNameId);
        recyclerView = findViewById(R.id.chatRecyclerId);
        messageInput = findViewById(R.id.chat_messageInput);
        imgBtn = findViewById(R.id.chatImageview_btn);
        sendBtn = findViewById(R.id.message_send_btn);
        imageViewfieldLayout = findViewById(R.id.ImageViewField_layout);
        imageView = findViewById(R.id.imageViewFieldId);
        closeImageView = findViewById(R.id.closImageViewId);

        otherUserModel = AndroidUtil.getUserModelFromIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUserModel.getUserId());

        imgBtn.setOnClickListener(v -> {
            imageChooser();
        });
        closeImageView.setOnClickListener(v -> {
            imageUri = null;
            imageView.setImageURI(imageUri);
            setImgViewField(false);
        });
        backBtn.setOnClickListener(v -> { onBackPressed();});
        FirebaseUtil.getOtherProfileImageStorageRf(otherUserModel.getUserId()).getDownloadUrl().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Uri uri = task.getResult();
                AndroidUtil.setProfileImg(this,uri,otherUserProfile);
            }
        });
        otherUserName.setText(otherUserModel.getUsername());
        sendBtn.setOnClickListener(v -> {

            if(imageUri != null){
                sendImgToUser();
            }
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty()){
                return;
            }
            sendMessageToUser(message);
            adapter.notifyDataSetChanged();
        });
        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    private void sendImgToUser() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        FirebaseUtil.getChatmessageStorageRf(chatRoomId,"#img"+ dtf.format(now)).putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                AndroidUtil.showToast(getApplicationContext(), " image added");
            }
        });

        chatRoomModel.setLastMessage("#img"+dtf.format(now));
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());

        FirebaseUtil.getChatroomReference(chatRoomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel("#img"+dtf.format(now), FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatRoomId).add(chatMessageModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                setImgViewField(false);
            }
        });
    }

    private void setupChatRecyclerView() {

//        AndroidUtil.showToast(getApplicationContext(), "chat recycler");
        Query query = FirebaseUtil.getChatroomMessageReference(chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();
        adapter = new ChatRecyclerAdapter(options, getApplicationContext(), chatRoomId);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if(chatRoomModel == null){
                    chatRoomModel = new ChatRoomModel(
                            chatRoomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUserModel.getUserId()),
                            Timestamp.now(),
                            "",
                            FirebaseUtil.currentUserId());
                    FirebaseUtil.getChatroomReference(chatRoomId).set(chatRoomModel);
                }
            }
        });
    }

    private void sendMessageToUser(String message) {
        chatRoomModel.setLastMessage(message);
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        FirebaseUtil.getChatroomReference(chatRoomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatRoomId).add(chatMessageModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                messageInput.setText("");
            }
        });
    }
    private void imageChooser() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && data != null){
            imageUri = data.getData();
            setImgViewField(true);
            imageView.setImageURI(imageUri);
        }

    }
    private void setImgViewField(boolean b){
        if(b){
            imageViewfieldLayout.setVisibility(View.VISIBLE);
        }else{
            imageUri = null;
            imageView.setImageURI(imageUri);
            imageViewfieldLayout.setVisibility(View.GONE);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null){
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