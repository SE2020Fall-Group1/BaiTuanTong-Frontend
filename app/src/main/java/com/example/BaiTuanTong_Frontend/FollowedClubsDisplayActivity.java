package com.example.BaiTuanTong_Frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class FollowedClubsDisplayActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FollowedClubsDisplayAdapter mAdapter;
    private List<String> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed_clubs_display);

        Intent intent = getIntent();

        mRecyclerView = this.findViewById(R.id.recyclerView);
        // 设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mList = getList();
        mAdapter = new FollowedClubsDisplayAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<String> getList() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add("社团" + i + "");
        }
        return list;
    }
}