package com.example.BaiTuanTong_Frontend.club;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.BaiTuanTong_Frontend.MainActivity;
import com.example.BaiTuanTong_Frontend.MyAdapter;
import com.example.BaiTuanTong_Frontend.R;

import java.util.ArrayList;
import java.util.List;

public class EditClubAdminActivity extends AppCompatActivity {

    private RecyclerView adminListView;
    private List<String> adminList;;
    private MyAdapter mMyAdapter;


    private List<String> getList()
    {
        List<String> ret = new ArrayList<>();
        for (int i = 1; i <= 10; ++i)
        {
            ret.add("username：李子恒" + "\nid:"+ i + "" + "");
        }
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_club_admin);

//        Intent intent = getIntent();
//        String message =intent.getStringExtra();//MainActivity.EXTRA_MESSAGE);


        adminListView = findViewById(R.id.recyclerView2);
        // 设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(adminListView.VERTICAL);
        adminListView.setLayoutManager(linearLayoutManager);
        // 设置 item 增加和删除时的动画
        adminListView.setItemAnimator(new DefaultItemAnimator());

        adminList = getList();

        mMyAdapter = new MyAdapter(this, adminList);
        adminListView.setAdapter(mMyAdapter);

        mMyAdapter.setOnItemLongClickListener(new MyAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position) {
                //长按删除一个成员
                adminList.remove(position);
                mMyAdapter.notifyDataSetChanged();
            }
        });
    }


}