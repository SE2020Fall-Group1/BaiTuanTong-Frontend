package com.example.BaiTuanTong_Frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.BaiTuanTong_Frontend.club.ClubHomeActivity;

import java.util.ArrayList;
import java.util.List;

public class FollowedClubsDisplayActivity extends AppCompatActivity {

    private Toolbar mNavigation;
    private RecyclerView mRecyclerView;
    private FollowedClubsDisplayAdapter mAdapter;
    private List<String> mList;

    public void initToolBar() {
        mNavigation.setTitle("关注的社团");
        setSupportActionBar(mNavigation);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed_clubs_display);

        Intent intent = getIntent();

        mNavigation = findViewById(R.id.followed_club_title);
        initToolBar();
        mNavigation.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setResult(3);
                finish();
            }
        });

        mRecyclerView = this.findViewById(R.id.followed_club_list);
        // 设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mList = getList();
        mAdapter = new FollowedClubsDisplayAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                sendMessage(position);
            }
        });
    }

    public void sendMessage(int position) {
        Intent intent = new Intent(this, ClubHomeActivity.class);
        intent.putExtra("club_name", mList.get(position));                 //通过Intent传递社团名称
        intent.putExtra("club_profile", "社团" + (position + 1) + "的简介");    //传递社团简介
        intent.putExtra("club_id", position);             //社团id，目前临时用position代替
        startActivity(intent);
    }

    private List<String> getList() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add("社团" + i + "");
        }
        return list;
    }
}