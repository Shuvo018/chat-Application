package com.example.chatapplication104;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.chatapplication104.adapter.searchUserRecyclerAdapter;
import com.example.chatapplication104.model.userModel;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class searchActivity extends AppCompatActivity {
    ImageView backBtn, searchBtn;
    EditText searchET;
    RecyclerView recyclerView;
    searchUserRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        backBtn = findViewById(R.id.leftArrowMVId);
        searchET = findViewById(R.id.searchUsersETId);
        searchBtn = findViewById(R.id.searchMVId);
        recyclerView = findViewById(R.id.recyclerSearchId);

        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        setupSearchRecyclerView("a");
        adapter.notifyDataSetChanged();
        searchBtn.setOnClickListener(v -> {
            if(searchET.getText().toString().isEmpty() || searchET.getText().length()<2){
                searchET.setError("Invalid username"); return;
            }
            setupSearchRecyclerView(searchET.getText().toString());
        });
    }

    private void setupSearchRecyclerView(String searchUser) {
        Query query = FirebaseUtil.allUserCollectionReference().whereGreaterThanOrEqualTo("username",searchUser);

        FirestoreRecyclerOptions<userModel> options = new FirestoreRecyclerOptions.Builder<userModel>()
                .setQuery(query, userModel.class).build();
        adapter = new searchUserRecyclerAdapter(options, getApplicationContext());
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