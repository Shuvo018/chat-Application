package com.example.chatapplication104.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {
    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {


        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                        userModel otherUserModel = task.getResult().toObject(userModel.class);
                        FirebaseUtil.getOtherProfileImageStorageRf(otherUserModel.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
                            if(t.isSuccessful()){
                                Uri uri = t.getResult();
                                AndroidUtil.setProfileImg(context,uri,holder.imageView);
                            }
                        });
                        holder.userName.setText(otherUserModel.getUsername());
                        char[] mc = model.getLastMessage().toString().toCharArray();
                        boolean isImg = isImage(mc);
                        if(isImg){
                            holder.lastMessageImgIcon.setVisibility(View.VISIBLE);
                            if (lastMessageSentByMe){
                                holder.lastMessage.setText("You : ");
                            }else{

                            }
                        }else{
                            holder.lastMessageImgIcon.setVisibility(View.GONE);
                            if (lastMessageSentByMe){
                                holder.lastMessage.setText("You : "+model.getLastMessage());
                            }else{
                                holder.lastMessage.setText(model.getLastMessage());
                            }
                        }
                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                    holder.itemView.setOnClickListener(v -> {
                   Intent intent = new Intent(context, ChatActivity.class);
                   AndroidUtil.passUserModelAsIntent(intent,otherUserModel);
                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   context.startActivity(intent);
               });
           }else{
                    }
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
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView, lastMessageImgIcon;
        TextView userName, lastMessage, lastMessageTime;
        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile_image_view_id);
            userName = itemView.findViewById(R.id.userNameTextView);
            lastMessage = itemView.findViewById(R.id.lastMessageTextView);
            lastMessageTime = itemView.findViewById(R.id.lastMessageTimeTextView);
            lastMessageImgIcon = itemView.findViewById(R.id.lastMessageImageView);
        }
    }
}
