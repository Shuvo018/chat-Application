package com.example.chatapplication104;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.chatapplication104.model.ChatGroupModel;
import com.example.chatapplication104.model.userModel;
import com.example.chatapplication104.utils.AndroidUtil;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

public class groupSettings extends AppCompatActivity {
    ImageView gbackbtn, groupImg;
    EditText groupName;
    Button gupdate;
    ProgressBar progressBar;
    Uri imageUri;
    String oldGname,newGname;

    ChatGroupModel chatGroupModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        chatGroupModel = AndroidUtil.getChatGroupModelAsIntent(getIntent());

        gbackbtn = findViewById(R.id.gsbackBtnid);
        groupImg = findViewById(R.id.gprofileImgId);
        groupName = findViewById(R.id.groupNameid);
        gupdate = findViewById(R.id.gupdateBtnid);
        progressBar = findViewById(R.id.gProgressBarId);

        getUserData();
        gbackbtn.setOnClickListener(v -> onBackPressed());
        groupImg.setOnClickListener(v -> imageChooser());
        gupdate.setOnClickListener(v -> {
            if (imageUri != null){
                FirebaseUtil.getallChatGroupRoomStorageRf(chatGroupModel.getChatroomId()).putFile(imageUri);
            }
            newGname = groupName.getText().toString();
            oldGname = chatGroupModel.getRoomName();
            if(newGname.equals(oldGname)) return;
            if(newGname.length()<3){groupName.setError("pls, enter more than 3 character "); return;}

            chatGroupModel.setRoomName(newGname);
            chatGroupModel.setLastMessageTimestamp(Timestamp.now());
            updateToFireStore(chatGroupModel);
        });

    }
    private void updateToFireStore(ChatGroupModel chatGroupModel) {
        FirebaseUtil.getChatGroupRoomReference(chatGroupModel.getChatroomId()).set(chatGroupModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if(task.isSuccessful()){
                AndroidUtil.showToast(groupSettings.this,"group update successfully");
            }else{
                AndroidUtil.showToast(groupSettings.this,"group update failed");
            }
        });
    }
    private void getUserData() {
        setInProgress(true);
        FirebaseUtil.getallChatGroupRoomStorageRf(chatGroupModel.getChatroomId()).getDownloadUrl().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Uri uri = task.getResult();
                AndroidUtil.setProfileImg(getApplicationContext(), uri, groupImg);
            }
        });
        groupName.setText(chatGroupModel.getRoomName());
        setInProgress(false);
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
            groupImg.setImageURI(imageUri);
        }
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            gupdate.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            gupdate.setVisibility(View.VISIBLE);
        }

    }
}