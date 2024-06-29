package com.example.chatapplication104.model;

public class HashModel {
    String key, value, senderId;

    public HashModel() {
    }

    public HashModel(String key, String value, String senderId) {
        this.key = key;
        this.value = value;
        this.senderId = senderId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
