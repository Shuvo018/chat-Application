package com.example.chatapplication104.model;

import com.google.firebase.Timestamp;

public class userModel {
    private String username, phone,  email, userId;
    private Timestamp createdTimestamp;

    public userModel() {
    }

    public userModel(String username, String phone, String email, String userId, Timestamp createdTimestamp) {
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.userId = userId;
        this.createdTimestamp = createdTimestamp;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
