package com.example.chatapplication104;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chatapplication104.model.userModel;
import com.example.chatapplication104.utils.AndroidUtil;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class loginEmail extends AppCompatActivity {
    EditText userName, userPhone, userEmail, userPassword;
    Button signInBtn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    userModel uModel;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        userName = findViewById(R.id.userNameEditTextid);
        userPhone = findViewById(R.id.userPhoneEditTextid);
        userEmail = findViewById(R.id.userEmailEditTextid);
        userPassword = findViewById(R.id.userPasswordEditTextid);
        signInBtn = findViewById(R.id.userSignInBtnid);
        progressBar = findViewById(R.id.loginProgressBarid);

//        getUserDetails();
        signInBtn.setOnClickListener(v -> {
            if(userName.getText().toString().isEmpty() || userName.getText().length()<3 ){
                userName.setError("Please enter a Valid name!"); return;
            }
            if(userPhone.getText().toString().isEmpty() || !Patterns.PHONE.matcher(userPhone.getText().toString()).matches()){
                userPhone.setError("Please enter a Valid phone number!"); return;
            }
            if (userEmail.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(userEmail.getText().toString()).matches()){
                userEmail.setError("Please enter a Valid E-Mail Address!"); return;
            }
            if(userPassword.getText().toString().isEmpty()){
                userPassword.setError("Enter password"); return;
            }

            String uName = userName.getText().toString();
            String uPhone = userPhone.getText().toString();
            String uEmail = userEmail.getText().toString();
            String uPassword = userPassword.getText().toString();
            signIn(uName, uPhone, uEmail, uPassword);
        });
    }

    private void getUserDetails() {
        setInProgress(true);
        FirebaseUtil.currentUserDetalils().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    uModel = task.getResult().toObject(userModel.class);
                    if(uModel != null){
                        userName.setText(uModel.getUsername());
                        userPhone.setText(uModel.getPhone());
                        userEmail.setText(uModel.getEmail());
                        setInProgress(false);
                    }
                    setInProgress(false);
                }
            }
        });
    }

    private void signIn(String uName,String uPhone,String uEmail, String uPassword) {
        setInProgress(true);

        if(uModel != null){
            setUserDetails(uName, uPhone, uEmail);
        }else{
//            AndroidUtil.showToast(getApplicationContext(), "sign in");
            mAuth.createUserWithEmailAndPassword(uEmail,uPassword).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    AndroidUtil.showToast(getApplicationContext(), "sign in successfull");
                    setUserDetails(uName, uPhone, uEmail);
                    setInProgress(false);
                    startActivity(new Intent(loginEmail.this, MainActivity.class));
                    finish();
                }else{
                    AndroidUtil.showToast(getApplicationContext(), "sign in failed");
                    setInProgress(false);
                }
            });
        }

    }
    private  void setUserDetails(String uName,String uPhone,String uEmail){
        setInProgress(true);
        uModel = new userModel(uName, uPhone, uEmail, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.currentUserDetalils().set(uModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    setInProgress(false);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            signInBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            signInBtn.setVisibility(View.VISIBLE);
        }

    }
}