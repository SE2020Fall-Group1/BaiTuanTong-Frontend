package com.example.BaiTuanTong_Frontend.club;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.BaiTuanTong_Frontend.R;

public class ClubHomeActivity extends AppCompatActivity {

    private Toolbar mNavigation;

    public void initToolBar() {
        mNavigation.setTitle("社团名称");
        setSupportActionBar(mNavigation);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_club_home);
        mNavigation = findViewById(R.id.club_title);
        initToolBar();
        //mNavigation.setTitle("社团名称");
        mNavigation.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setResult(3);
                finish();
            }
        });
        //setSupportActionBar(mNavigation);
        //ActionBar actionBar = getSupportActionBar();

        //actionBar.setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        //actionBar.setHomeButtonEnabled(true); //设置返回键可用
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuItem index = menu.add(Menu.NONE, Menu.FIRST, Menu.FIRST, "发布动态");
        //index.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        //menu.add(Menu.NONE, Menu.FIRST + 1, Menu.FIRST + 1, "管理社团");
        getMenuInflater().inflate(R.menu.club_home_menu, menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.release_post_menu_item:
                Toast.makeText(this,"setting is clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.club_admin_manage_menu_item:
                Toast.makeText(this,"favorite is clicked",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}