package com.example.BaiTuanTong_Frontend.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String msg;

    public LoggedInUser(String userId, String msg) {
        this.userId = userId;
        this.msg = msg;
    }

    public String getUserId() {
        return userId;
    }

    public String getMsg() {
        return msg;
    }
}