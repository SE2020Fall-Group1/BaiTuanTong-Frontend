package com.example.BaiTuanTong_Frontend;

import com.example.BaiTuanTong_Frontend.R;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;

import com.squareup.okhttp.*;

import java.io.IOException;
import android.util.Log;
import java.util.List;
import java.util.ArrayList;


public class ManagerHomePage extends AppCompatActivity {
    private Toolbar mNavigation;
    private RecyclerView rv_manage_club;
    private LinearLayoutManager manager;
    private ManagerAdapter adapter;

    public void initToolBar() {
        mNavigation.setTitle("社团管理");
        setSupportActionBar(mNavigation);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_homepage);
        mNavigation = findViewById(R.id.manager_homepage);
        initToolBar();
        initRecyclerLinear();
    }

    // 调试用
    private List<String> mList;

    // List初始化，随意实现的
    private List<String> getList(){
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add("管理员测试\n"+"社团"+i+"");
        }
        return list;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manager_homepage_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                adapter.addData(1);
                break;
            case R.id.delete:
                adapter.removeData(1);
                break;
        }
        return true;
    }

    public void initRecyclerLinear() {
        rv_manage_club = findViewById(R.id.rv_manage_club);
        manager = new LinearLayoutManager(this);
        rv_manage_club.setLayoutManager(manager);

        mList = getList();
        adapter = new ManagerAdapter(this, mList);
        rv_manage_club.setAdapter(adapter);
        rv_manage_club.setItemAnimator(new DefaultItemAnimator());

        adapter.setOnItemClickListener(new ManagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(ManagerHomePage.this, position + " click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ManagerHomePage.this, ManageAdministrator.class);
                intent.putExtra("clubName", mList.get(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

                Toast.makeText(ManagerHomePage.this, position + " Long click", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder delete_confirm = new AlertDialog.Builder(ManagerHomePage.this);
                delete_confirm.setMessage("确认删除该社团吗？");
                delete_confirm.setTitle("提示");
                delete_confirm.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
                delete_confirm.setPositiveButton("确定", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        adapter.removeData(position);
                    }
                });

                delete_confirm.show();
            }
        });

    }
}
