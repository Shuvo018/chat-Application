package com.example.chatapplication104.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication104.R;
import com.example.chatapplication104.groupChatActivity;
import com.example.chatapplication104.model.ChatGroupModel;
import com.example.chatapplication104.utils.AndroidUtil;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentGroupAdapter extends FirestoreRecyclerAdapter<ChatGroupModel, RecentGroupAdapter.groupChatRoomModelViewHolder> {
    Context context;

    public RecentGroupAdapter(@NonNull FirestoreRecyclerOptions<ChatGroupModel> options) {
        super(options);
    }

    public RecentGroupAdapter(@NonNull FirestoreRecyclerOptions<ChatGroupModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull groupChatRoomModelViewHolder holder, int position, @NonNull ChatGroupModel model) {
        holder.groupName.setText(model.getRoomName());
        char[] mc = model.getLastMessage().toString().toCharArray();
        boolean isImg = isImage(mc);
        if(isImg){
            holder.glastMessageImg.setVisibility(View.VISIBLE);
            if (model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId())){
                holder.glastMessage.setText("You : ");
            }else{
            }
        }else{
            holder.glastMessageImg.setVisibility(View.GONE);
            if (model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId())){
                holder.glastMessage.setText("You : "+model.getLastMessage());
            }else{
                holder.glastMessage.setText(model.getLastMessage());
            }
        }
        holder.glastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, groupChatActivity.class);
            AndroidUtil.passChatGroupModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }
    private boolean isImage(char[] mc) {
        if(mc.length>4){
            return mc[0] == '#' &&  mc[1] == 'i' && mc[2] == 'm' && mc[3] == 'g';
        }
        return false;
    }
    @NonNull
    @Override
    public groupChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_groupchat_recycler_row, parent, false);
        return new groupChatRoomModelViewHolder(view);
    }

    class groupChatRoomModelViewHolder extends RecyclerView.ViewHolder{
        ImageView gimg, glastMessageImg;
        TextView groupName, glastMessage, glastMessageTime;

        public groupChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            gimg = itemView.findViewById(R.id.profile_image_view_id);
            groupName = itemView.findViewById(R.id.groupNameTextView);
            glastMessage = itemView.findViewById(R.id.groupLastMessageTextView);
            glastMessageImg = itemView.findViewById(R.id.GroupLastMessageImageView);
            glastMessageTime = itemView.findViewById(R.id.grouplastMessageTimeTextView);
        }
    }
}
