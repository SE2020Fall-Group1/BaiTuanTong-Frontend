/*
  文件名: ClubHomeActivity.java
  创建者: 谷丰
  描述: 这个activity是社团主页，在页面上方的工具栏中显示有社团名称，右上角的菜单键提供了社团管理员发布动态，
  社团总管理员管理社团的入口。在工具栏下方显示社团简介，再下方显示社团发布的动态的列表。
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

import com.example.BaiTuanTong_Frontend.GridView.ReleasePostActivity;
import com.example.BaiTuanTong_Frontend.PostPageActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.MyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;


public class ClubHomeActivity extends AppCompatActivity {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType STRING
            = MediaType.get("text/plain; charset=utf-8");

    private Toolbar mNavigation;  //顶部导航栏
    private TextView club_profile;   //社团简介文本框
    private TextView detail_button;  //"详情" "收起" 按钮
    private TextView empty_note;   //列表为空的提示信息
    private boolean extended;    //当前文本框是否展开
    private final OkHttpClient client = new OkHttpClient();
    private static final int GET = 1;
    private static final int POST = 2;
    private static final String SERVERURL = "http://47.92.233.174:5000/";
    private static final String LOCALURL = "http://10.0.2.2:5000/";

    private RecyclerView mRecyclerView;  //动态列表
    private MyAdapter mMyAdapter;

    private List<String> postList; //动态列表
    private String clubName;
    private String clubInfo;  //社团简介
    private String clubPresident;  //社长
    private int clubId;

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
                    //club_profile.setText((String)msg.obj);
                    //break;
                case POST:
                    //club_profile.setText((String)msg.obj);
                    try {
                        parseJsonPacket((String)msg.obj);
                        String print = clubInfo+"\n"+"社长: "+clubPresident;
                        club_profile.setText(print);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    });

    /**
     * 解析post返回的json包
     * @param json post返回的json包
     * @throws JSONException 解析出错
     */
    private void parseJsonPacket(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        int code = jsonObject.getInt("code");
        if(code == 403)
            return;
        clubName = jsonObject.getString("clubName");
        clubInfo = jsonObject.getString("introduction");
        clubPresident = jsonObject.getString("president");
        JSONArray jsonArray = jsonObject.getJSONArray("postSummary");
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject postObj = jsonArray.getJSONObject(i);
            postList.add(postObj.getString("title"));
            //todo 添加对postSummary中其他两项数据的处理
        }
    }

    /**
     * 初始化页面上方标题栏
     */
    public void initToolBar() {
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
        clubId = getIntent().getIntExtra("clubId", -1);
        //String clubName = "yuanhuo";
        postList = new ArrayList<>();
        getDataFromGet(SERVERURL + "club/homepage?" + "clubId=" + clubId);

        club_profile = (TextView) findViewById(R.id.get_club_profile);

        mNavigation = findViewById(R.id.club_title);
        initToolBar();
        mNavigation.setTitle(clubName);

        detail_button = (TextView) findViewById(R.id.details);
        extended = false;
        detail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extended = !extended;
                if(extended){
                    club_profile.setMaxLines(10);
                    detail_button.setText("[收起]");
                }
                else{
                    club_profile.setMaxLines(1);
                    detail_button.setText("[详情]");
                }
            }
        });

        empty_note = this.findViewById(R.id.empty_note);
        mRecyclerView = this.findViewById(R.id.club_post_list);
        if(postList.isEmpty()){ //如果动态列表为空,在屏幕中央显示提示信息
            mRecyclerView.setVisibility(GONE);
        }
        else{
            empty_note.setVisibility(GONE);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMyAdapter = new MyAdapter(this, postList);
        mRecyclerView.setAdapter(mMyAdapter);
        mMyAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                sendMessage(position);
            }
        });
        /*mMyAdapter.setOnItemLongClickListener(new MyAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position) {
                //长按删除数据
                mList.remove(position);
                mMyAdapter.notifyItemRemoved(position);
                mMyAdapter.notifyItemRangeChanged(position, mList.size());
            }
        });*/
    }

    /*@Override
    public void onStart(){
        super.onStart();
        Toast.makeText(this,"ClubHomePage is onStart",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume(){
        super.onResume();
        Toast.makeText(this,"ClubHomePage is onResume",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause(){
        super.onPause();
        Toast.makeText(this,"ClubHomePage is onPause",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop(){
        super.onStop();
        Toast.makeText(this,"ClubHomePage is onStop",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Toast.makeText(this,"ClubHomePage is onRestart",Toast.LENGTH_SHORT).show();
    }*/



    /**
     * 使用get获取数据
     */
    private void getDataFromGet(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
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
    private void getDataFromPost(String url, String json) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = post(url, json); //jason用于上传数据，目前不需要
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

    /**
     *初始化右上角菜单按钮
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.club_home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *菜单中按钮被点击时的回调函数
     * 目前用来测试okhttp
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.release_post_menu_item:
                //getDataFromGet("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
                //    getDataFromGet("http://47.92.233.174:5000/");
                //test release post page __ by tbw

                intent = new Intent(this, ReleasePostActivity.class);
            //    intent.putExtra("clubID", clubID);
                startActivity(intent);

                break;
            case R.id.club_admin_manage_menu_item:
                intent = new Intent(this, EditClubAdminActivity.class);
                intent.putExtra("club_id", clubId);
                startActivity(intent);
                //getDataFromPost("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
                //getDataFromGet("http://10.0.2.2:5000/hello");
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Okhttp的get请求
     * @param url 向服务器请求的url
     * @return 服务器返回的字符串
     * @throws IOException 请求出错
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
     * @param url 向服务器请求的url
     * @param json 向服务器发送的json包
     * @return 服务器返回的字符串
     * @throws IOException 请求出错
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

    /**
     * 点击列表中某一项时的处理函数
     * @param position 被点击项的序号
     */
    public void sendMessage(int position) {
        Intent intent = new Intent(this, PostPageActivity.class);
        String message = postList.get(position);
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