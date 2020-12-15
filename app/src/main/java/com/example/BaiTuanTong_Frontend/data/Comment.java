package com.example.BaiTuanTong_Frontend.data;

public class Comment {
    String user;
    String comment;

    public Comment(String iuser, String icomment){
        this.user = iuser;
        this.comment = icomment;
    }
    public String getComment() {
        return comment;
    }

    public String getUser() {
        return user;
    }
}
