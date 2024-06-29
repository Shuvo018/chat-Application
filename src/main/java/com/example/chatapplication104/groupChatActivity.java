package com.example.chatapplication104;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chatapplication104.adapter.ChatRecyclerAdapter;
import com.example.chatapplication104.model.ChatGroupModel;
import com.example.chatapplication104.model.ChatMessageModel;
import com.example.chatapplication104.model.HashModel;
import com.example.chatapplication104.model.userModel;
import com.example.chatapplication104.utils.AndroidUtil;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class groupChatActivity extends AppCompatActivity {
    ImageView backBtn, groupProfile,hashBtn, GCsettingBtn;
    TextView groupName;
    RecyclerView recyclerView;
    EditText messageInput;
    ImageButton imgBtn, sendBtn;
    RelativeLayout imageViewfieldLayout;
    ImageView imageView, closeImageView;
    Uri imageUri;
    ChatGroupModel chatGroupModel;
    String gchatroomId;
    ChatRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        backBtn = findViewById(R.id.gChatbackBtnId);
        groupProfile = findViewById(R.id.profile_image_view_id);
        groupName = findViewById(R.id.groupNameId);
        hashBtn =findViewById(R.id.group_chat_hashBtnId);
        GCsettingBtn = findViewById(R.id.group_chat_settingsBtnId);
        recyclerView = findViewById(R.id.GchatRecyclerId);
        messageInput = findViewById(R.id.groupchat_messageInput);
        imgBtn = findViewById(R.id.groupchatImageview_btn);
        sendBtn = findViewById(R.id.groupmessage_send_btn);
        imageViewfieldLayout = findViewById(R.id.gImageViewField_layout);
        imageView = findViewById(R.id.gimageViewFieldId);
        closeImageView = findViewById(R.id.gclosImageViewId);

        backBtn.setOnClickListener(v -> {onBackPressed();});
        hashBtn.setOnClickListener(v -> {
            Intent intent = new Intent(groupChatActivity.this, groupChatHashList.class);
            AndroidUtil.passGroupChatIdAsIntent(intent, gchatroomId);
            startActivity(intent);
        });
        GCsettingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(groupChatActivity.this, groupSettings.class);
            AndroidUtil.passChatGroupModelAsIntent(intent, chatGroupModel);
            startActivity(intent);
        });
        imgBtn.setOnClickListener(v -> {
            imageChooser();
        });
        closeImageView.setOnClickListener(v -> {
            imageUri = null;
            imageView.setImageURI(imageUri);
            setImgViewField(false);
        });
        sendBtn.setOnClickListener(v -> {
            if(imageUri != null){
                sendImgToUser();
            }
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty()){
                return;
            }
            sendMessageToUser(message, FirebaseUtil.currentUserId());
            //sethash
            char[] ms = message.toCharArray();
            if(ms.length > 1 && ms[0] == '#' && ms[1] == '#' && ms[2] == 's'){

                int k=3;
                String setHashKey = new String();
                for(int i=3; i<ms.length; i++){
                    if(ms[i] == ' ' || ms[i] == '\n'){
                        k = i;
                        break;
                    }
                    setHashKey += ms[i];
                }
                String setHashValue = new String();
                for (int i=k; i<ms.length; i++){
                    setHashValue += ms[i];
                }
                Log.d("key, value   ", setHashKey+" "+setHashValue);

                sendMessageToHash(setHashKey,setHashValue,FirebaseUtil.currentUserId());
            }else{
                //gethash
                if(ms.length > 1 && ms[0] == '#' && ms[1] == '#' && ms[2] == 'g'){
                    String getHash = new String();
                    for (int i=3; i<ms.length; i++){
                        getHash += ms[i];
                    }
                    String finalGetHash = getHash;
                    FirebaseUtil.getGroupChatRooMessageHashRef(gchatroomId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                            if(finalGetHash.equals(documentSnapshot.getData().get("key").toString())){
                                sendMessageToUser(documentSnapshot.getData().get("value").toString(), documentSnapshot.getData().get("senderId").toString());
                            }
                        }
                    });
                }
            }

            adapter.notifyDataSetChanged();
        });
        chatGroupModel = AndroidUtil.getChatGroupModelAsIntent(getIntent());
        groupName.setText(chatGroupModel.getRoomName());
        gchatroomId = chatGroupModel.getChatroomId();
        setupChatRecyclerView();
    }
    private void sendMessageToHash(String key, String value, String senderId){
        HashModel hashModel = new HashModel(key, value, senderId);
        FirebaseUtil.getGroupChatRooMessageHashRef(gchatroomId).add(hashModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                messageInput.setText("");
            }
        });
    }

    private void sendMessageToUser(String message, String senderId) {
        chatGroupModel.setLastMessage(message);
        chatGroupModel.setLastMessageSenderId(senderId);
        chatGroupModel.setLastMessageTimestamp(Timestamp.now());
        FirebaseUtil.getChatGroupRoomReference(gchatroomId).set(chatGroupModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, senderId,Timestamp.now());
        FirebaseUtil.getGroupChatRooMessageRef(gchatroomId).add(chatMessageModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                AndroidUtil.showToast(getApplicationContext(),"message send");
                messageInput.setText("");
            }
        });
    }
    private void sendImgToUser() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        FirebaseUtil.getChatmessageStorageRf(gchatroomId,"#img"+ dtf.format(now)).putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                AndroidUtil.showToast(getApplicationContext(), " image added");
            }
        });

        chatGroupModel.setLastMessage("#img"+dtf.format(now));
        chatGroupModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatGroupModel.setLastMessageTimestamp(Timestamp.now());

        FirebaseUtil.getChatGroupRoomReference(gchatroomId).set(chatGroupModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel("#img"+dtf.format(now), FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getGroupChatRooMessageRef(gchatroomId).add(chatMessageModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                setImgViewField(false);
            }
        });
    }

    private void setupChatRecyclerView() {
        Query query = FirebaseUtil.getGroupChatRooMessageRef(gchatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();
        adapter = new ChatRecyclerAdapter(options, getApplicationContext(), gchatroomId);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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