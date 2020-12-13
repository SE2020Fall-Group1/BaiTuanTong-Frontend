/**
 * 这个activity是社团主页，在页面上方的工具栏中显示有社团名称，右上角的菜单键提供了社团管理员发布动态，
 * 社团总管理员管理社团的入口。在工具栏下方显示社团简介，再下方显示社团发布的动态的列表。
 * 作者：谷丰
 */
package com.example.BaiTuanTong_Frontend.club;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.BaiTuanTong_Frontend.PostPageActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.MyAdapter;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ClubHomeActivity extends AppCompatActivity {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private Toolbar mNavigation;
    private TextView get_result;
    private OkHttpClient client = new OkHttpClient();
    //private OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).connectTimeout(30, TimeUnit.SECONDS).build();
    private  static final int GET = 1;
    private  static final int POST = 2;

    private RecyclerView mRecyclerView;
    private MyAdapter mMyAdapter;
    private List<String> mList;

    /**
     * 处理get请求与post请求的回调函数
     */
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            Log.e("TAG", (String)msg.obj);
            switch (msg.what){
                case GET:
                    get_result.setText((String)msg.obj);
                    break;
                case POST:
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

        get_result = (TextView) findViewById(R.id.get_club_profile);

        mRecyclerView = this.findViewById(R.id.club_post_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mList = getList();
        mMyAdapter = new MyAdapter(this, mList);
        mRecyclerView.setAdapter(mMyAdapter);

        mMyAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
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

    /**
     * 使用get获取数据
     */
    private void getDataFromGet(String url) {
        //Log.e("TAG", "Start getDataFromGet()");
        new Thread(){
            @Override
            public void run() {
                super.run();
                //Log.e("TAG", "new thread run.");
                try {
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

    /**
     * 使用post获取数据
     */
    private void getDataFromPost(String url) {
        //Log.e("TAG", "Start getDataFromGet()");
        new Thread(){
            @Override
            public void run() {
                super.run();
                //Log.e("TAG", "new thread run.");
                try {
                    String result = post(url, ""); //jason用于上传数据，目前不需要
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = POST;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
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
                break;
            case R.id.club_admin_manage_menu_item:
                getDataFromPost("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
                //getDataFromGet("http://10.0.2.2:5000/hello");
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Okhttp的get请求
     * @param url
     * @return 服务器返回的字符串
     * @throws IOException
     */
    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Okhttp的post请求
     * @param url
     * @param json
     * @return 服务器返回的字符串
     * @throws IOException
     */
    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public void sendMessage(int position) {
        Intent intent = new Intent(this, PostPageActivity.class);
        String message = mList.get(position);
        //intent.putExtra(PAGE_EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private List<String> getList() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add("动态" + i + "\n社团发布的第"+ i + "条动态" + "");
        }
        return list;
    }
}