package com.example.chatapplication104;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.chatapplication104.model.userModel;
import com.example.chatapplication104.utils.AndroidUtil;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;


public class menuActivity extends AppCompatActivity {
    ImageView profileImg, backBtn;
    EditText userName, userPhone;
    Button updateBtn;
    ProgressBar progressBar;
    Uri imageUri;
    userModel uModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        profileImg = findViewById(R.id.profileImgId);
        userName = findViewById(R.id.profUserNameid);
        userPhone = findViewById(R.id.profUserPhoneid);
        updateBtn = findViewById(R.id.updateBtnid);
        progressBar = findViewById(R.id.profProgressBarId);
        backBtn = findViewById(R.id.progilebackBtnid);

        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        getUserData();
        profileImg.setOnClickListener(v -> {
            imageChooser();
        });
        updateBtn.setOnClickListener(v -> {
            String newUserName = userName.getText().toString();
            if(newUserName.isEmpty() || newUserName.length()<3){
                userName.setError("Username length should be at least 4 chars"); return;
            }
            setInProgress(true);

            uModel.setUsername(newUserName);
            if(imageUri != null){
                FirebaseUtil.getCurrentProfileImageStorageRf().putFile(imageUri).addOnCompleteListener(task -> {
                });
            }
            updateToFireStore(uModel);
        });
    }

    private void getUserData() {
        setInProgress(true);
        FirebaseUtil.getCurrentProfileImageStorageRf().getDownloadUrl().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Uri uri = task.getResult();
                AndroidUtil.setProfileImg(getApplicationContext(), uri, profileImg);
            }
        });
        FirebaseFirestore.getInstance().collection("users").document(FirebaseUtil.currentUserId()).get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()){
                AndroidUtil.showToast(getApplicationContext(), "get user data");
                uModel = task.getResult().toObject(userModel.class);
//                Log.d("getData : ", task.getResult().getData()+" : ");
                userName.setText(uModel.getUsername());
                userPhone.setText(uModel.getPhone());
            }
        });
    }

    private void updateToFireStore(userModel uModel) {
        FirebaseUtil.currentUserDetalils().set(uModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if(task.isSuccessful()){
                AndroidUtil.showToast(menuActivity.this,"profile update successfully");
            }else{
                AndroidUtil.showToast(menuActivity.this,"profile update failed");
            }
        });
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            updateBtn.setVisibility(View.VISIBLE);
        }

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
            profileImg.setImageURI(imageUri);
        }

    }
}