package com.example.BaiTuanTong_Frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.BaiTuanTong_Frontend.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class PostListDisplayActivity extends AppCompatActivity {
    public static final String PAGE_EXTRA_MESSAGE = "com.example.BaiTuanTong_Frontend.pageMESSAGE";

    private RecyclerView mRecyclerView;
    private MyAdapter mMyAdapter;
    private List<String> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list_display);

        Intent intent = getIntent();

        mRecyclerView = this.findViewById(R.id.followed_club_list);
        // 设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        // 设置 item 增加和删除时的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mList = getList();
        mMyAdapter = new MyAdapter(this, mList);
        mRecyclerView.setAdapter(mMyAdapter);

        // 下面是为点击事件添加的代码
        mMyAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                // Toast.makeText(getBaseContext(), mList.get(position), Toast.LENGTH_SHORT).show();
                sendMessage(position);
            }
        });
        mMyAdapter.setOnItemLongClickListener(new MyAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position) {
                //长按删除数据
                mList.remove(position);
                mMyAdapter.notifyDataSetChanged();
            }
        });
    }

    public void sendMessage(int position) {
        Intent intent = new Intent(this, LoginActivity.class);
        String message = mList.get(position);
        intent.putExtra(PAGE_EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private List<String> getList() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add("动态" + i + "\n今天是11月"+ i + "号" + "");
        }
        return list;
    }
}