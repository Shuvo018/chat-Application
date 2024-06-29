package com.example.chatapplication104;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.example.chatapplication104.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;

public class splashActivity extends AppCompatActivity {
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseUtil.isLoggedIn()){
                    startActivity(new Intent(splashActivity.this, MainActivity.class));
                }else{
                    startActivity(new Intent(splashActivity.this, loginEmail.class));
                }
                finish();
            }
        }, 2000);
    }
}