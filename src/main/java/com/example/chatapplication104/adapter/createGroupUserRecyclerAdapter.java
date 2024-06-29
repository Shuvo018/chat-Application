package com.example.chatapplication104.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication104.R;
import com.example.chatapplication104.model.userModel;
import com.example.chatapplication104.utils.AndroidUtil;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class createGroupUserRecyclerAdapter extends FirestoreRecyclerAdapter<userModel, createGroupUserRecyclerAdapter.createGroupViewHolder> {
    Context context;

    public createGroupUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<userModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull createGroupViewHolder holder, int position, @NonNull userModel model) {
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
        holder.itemView.setOnClickListener(v ->{
            if(!AndroidUtil.li.contains(model.getUserId())){
                holder.userNameText.setBackgroundColor(Color.BLUE);
                AndroidUtil.setcreateGroupUserList(model.getUserId());
            }else{
                holder.userNameText.setBackgroundColor(Color.WHITE);
                AndroidUtil.li.remove(model.getUserId());
            }

        });

    }

    @NonNull
    @Override
    public createGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new createGroupViewHolder(view);
    }

    class createGroupViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImg;
        TextView userNameText, phoneText;

        public createGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_image_view_id);
            userNameText = itemView.findViewById(R.id.otherUserNameTVid);
            phoneText = itemView.findViewById(R.id.otherUserPhoneTVid);
        }
    }
}
