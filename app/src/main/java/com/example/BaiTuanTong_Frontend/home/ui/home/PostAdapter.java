package com.example.BaiTuanTong_Frontend.home.ui.home;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.BaiTuanTong_Frontend.PostListAdapter;

import java.util.List;

public class PostAdapter extends PostListAdapter {

    public PostAdapter(Context mContext, List<String> title, List<String> clubName, List<String> text, List<String> likeCnt, List<String> commentCnt) {
        super(mContext, title, clubName, text, likeCnt, commentCnt);
    }

    public class PostViewHolder extends PostListAdapter.PostListViewHolder {
        public PostViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
        }
    }
}