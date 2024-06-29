package com.example.chatapplication104.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {
    public static String currentUserId(){ return FirebaseAuth.getInstance().getUid();}
    public static DocumentReference currentUserDetalils(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }
    public static boolean isLoggedIn(){
        return (currentUserId() != null)? true : false;
    }
    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }
    public static StorageReference getCurrentProfileImageStorageRf(){
        return FirebaseStorage.getInstance().getReference().child("profile_Image").child(currentUserId());
    }
    public static StorageReference getOtherProfileImageStorageRf(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_Image").child(otherUserId);
    }
    public static StorageReference getChatmessageStorageRf(String chatroomId, String childId){
        return FirebaseStorage.getInstance().getReference().child("chatrooms").child(chatroomId).child(childId);
    }
    public static StorageReference getGroupChatmessageStorageRf(String chatroomId, String childId){
        return FirebaseStorage.getInstance().getReference().child("chatGroupRooms").child(chatroomId).child(childId);
    }
    public static String getChatroomId(String userId1, String userId2){
        if(userId1.hashCode() < userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }
    public static  DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }
    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }
    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }
    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }
    public static  DocumentReference getChatGroupRoomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatGroupRooms").document(chatroomId);
    }
    public static  CollectionReference getallChatGroupRoomReference(){
        return FirebaseFirestore.getInstance().collection("chatGroupRooms");
    }

    public static CollectionReference getGroupChatRooMessageRef(String chatroomId){
        return getChatGroupRoomReference(chatroomId).collection("chats");
    }
    public static StorageReference getallChatGroupRoomStorageRf(String groupChatId){
        return FirebaseStorage.getInstance().getReference("chatGroupRooms").child(groupChatId);
    }
    public static CollectionReference getGroupChatRooMessageHashRef(String chatroomId){
        return getChatGroupRoomReference(chatroomId).collection("hash");
    }
}
