package com.example.chatapplication104.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication104.ChatActivity;
import com.example.chatapplication104.R;
import com.example.chatapplication104.model.ChatRoomModel;
import com.example.chatapplication104.model.userModel;
import com.example.chatapplication104.utils.AndroidUtil;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class searchUserRecyclerAdapter extends FirestoreRecyclerAdapter<userModel, searchUserRecyclerAdapter.userModelViewHolder> {

    Context context;
    public searchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<userModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull userModelViewHolder holder, int position, @NonNull userModel model) {
        FirebaseUtil.getOtherProfileImageStorageRf(model.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
                 if(t.isSuccessful()){
                     Uri uri = t.getResult();
                     AndroidUtil.setProfileImg(context, uri, holder.profileImg);
                 }
        });
        holder.userNameText.setText(model.getUsername());
        holder.phoneText.setText(model.getPhone());
        if(model.getUserId().equals(FirebaseUtil.currentUserId())){
            holder.userNameText.setText(model.getUsername()+" (me)");
        }
        holder.itemView.setOnClickListener(v -> {
//            AndroidUtil.showToast(context, model.getUsername());
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public userModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new userModelViewHolder(view);
    }

    class userModelViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImg;
        TextView userNameText, phoneText;

        public userModelViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_image_view_id);
            userNameText = itemView.findViewById(R.id.otherUserNameTVid);
            phoneText = itemView.findViewById(R.id.otherUserPhoneTVid);
        }
    }
}
