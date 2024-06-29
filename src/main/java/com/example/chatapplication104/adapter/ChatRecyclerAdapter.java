package com.example.chatapplication104.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication104.R;
import com.example.chatapplication104.model.ChatMessageModel;
import com.example.chatapplication104.utils.AndroidUtil;
import com.example.chatapplication104.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {
    Context context;
    String chatRoomId;
    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context, String chatRoomId) {
        super(options);
        this.context = context;
        this.chatRoomId = chatRoomId;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {

        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            // For current user side right
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);

            char[] mc = model.getMessage().toString().toCharArray();
            boolean isImg = isImage(mc);
            if(isImg){
                holder.rightChatTextView.setVisibility(View.GONE);
                holder.rightChatImgView.setVisibility(View.VISIBLE);
                FirebaseUtil.getChatmessageStorageRf(chatRoomId, model.getMessage()).getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setChatroomImg(context,uri, holder.rightChatImgView);
                    }
                });
            }else{
                holder.rightChatImgView.setVisibility(View.GONE);
                holder.rightChatTextView.setVisibility(View.VISIBLE);
                holder.rightChatTextView.setText(model.getMessage());
            }

        }else{
            // For other user side left
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);

            char[] mc = model.getMessage().toString().toCharArray();
            boolean isImg = isImage(mc);
            if(isImg){
                holder.leftChatTextView.setVisibility(View.GONE);
                holder.leftChatImgView.setVisibility(View.VISIBLE);
                FirebaseUtil.getChatmessageStorageRf(chatRoomId, model.getMessage()).getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setChatroomImg(context,uri, holder.leftChatImgView);
                    }
                });
            }else{
                holder.leftChatImgView.setVisibility(View.GONE);
                holder.leftChatTextView.setVisibility(View.VISIBLE);
                holder.leftChatTextView.setText(model.getMessage());
            }
        }
    }

    private boolean isImage(char[] mc) {
        if(mc.length>4){
            return mc[0] == '#' &&  mc[1] == 'i' && mc[2] == 'm' && mc[3] == 'g';
        }
        return false;
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextView, rightChatTextView;
        ImageView leftChatImgView, rightChatImgView;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextView = itemView.findViewById(R.id.right_chat_textview);
            leftChatImgView = itemView.findViewById(R.id.left_chat_imageview);
            rightChatImgView = itemView.findViewById(R.id.right_chat_imageview);
        }
    }
}
