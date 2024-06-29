package com.example.chatapplication104.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chatapplication104.model.ChatGroupModel;
import com.example.chatapplication104.model.userModel;

import java.util.ArrayList;
import java.util.List;

public class AndroidUtil {
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void setProfileImg(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
    public static void setChatroomImg(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).into(imageView);
    }
    public static void passUserModelAsIntent(Intent intent, userModel model){
        intent.putExtra("userName", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());
    }

    public static userModel getUserModelFromIntent(Intent intent){
        userModel userModel = new userModel();
        userModel.setUsername(intent.getStringExtra("userName"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;
    }
    public static void passChatGroupModelAsIntent(Intent intent, ChatGroupModel chatGroupModel){
        intent.putExtra("groupId", chatGroupModel.getChatroomId());
        intent.putExtra("lastMessage", chatGroupModel.getLastMessage());
        intent.putExtra("lastMessageSenderId", chatGroupModel.getLastMessageSenderId());
        intent.putExtra("groupName", chatGroupModel.getRoomName());
        intent.putStringArrayListExtra("userIds", (ArrayList<String>) chatGroupModel.getUserIds());
    }
    public static ChatGroupModel getChatGroupModelAsIntent(Intent intent){
        ChatGroupModel gmodel = new ChatGroupModel();
        gmodel.setChatroomId(intent.getStringExtra("groupId"));
        gmodel.setLastMessage(intent.getStringExtra("lastMessage"));
        gmodel.setLastMessageSenderId(intent.getStringExtra("lastMessageSenderId"));
        gmodel.setRoomName(intent.getStringExtra("groupName"));
        gmodel.setUserIds(intent.getStringArrayListExtra("userIds"));
        return gmodel;
    }

    public static void passGroupChatIdAsIntent(Intent intent, String id){
        intent.putExtra("groupChatId", id);
    }
    public static String getGroupChatIdAsIntent(Intent intent){
        return intent.getStringExtra("groupChatId");
    }

    public static List<String> li = new ArrayList<>();
    public static void setcreateGroupUserList(String userId){
        li.add(userId);
    }
    public static List<String> getcreateGroupUserList(){
        return li;
    }
}
