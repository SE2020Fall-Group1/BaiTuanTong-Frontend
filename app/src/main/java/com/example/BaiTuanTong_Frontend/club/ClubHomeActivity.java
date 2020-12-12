package com.example.BaiTuanTong_Frontend.club;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BaiTuanTong_Frontend.R;
import com.squareup.okhttp.*;

import java.io.IOException;


public class ClubHomeActivity extends AppCompatActivity {

    private Toolbar mNavigation;
    private TextView get_result;
    private OkHttpClient client = new OkHttpClient();
    private  static final int GET = 1;
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            Log.e("TAG", (String)msg.obj);
            switch (msg.what){
                case GET:
                    get_result.setText((String)msg.obj);
                    break;
            }
            return true;
        }
    });

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

        get_result = (TextView) findViewById(R.id.get_result);
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
        //getDataFromGet();
    }

    private void getDataFromGet(String url) {
        //Log.e("TAG", "Start getDataFromGet()");
        new Thread(){
            @Override
            public void run() {
                super.run();
                //Log.e("TAG", "new thread run.");
                try {
                    //String result = get("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
                    //String result = get("http://127.0.0.1:5000/");
                    String result = get(url);
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = GET;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "get failed.");
                }
            }
        }.start();
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
                getDataFromGet("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
                //Toast.makeText(this,"setting is clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.club_admin_manage_menu_item:
                getDataFromGet("http://10.0.2.2:5000/hello");
                //Toast.makeText(this,"favorite is clicked",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}