package com.example.BaiTuanTong_Frontend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BaiTuanTong_Frontend.club.ClubHomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ManageClubsActivity extends AppCompatActivity {

    private Toolbar mNavigation;
    private RecyclerView mRecyclerView;
    private ManageClubsAdapter mAdapter;

    private String userId;
    private SharedPreferences shared;

    // 与后端通信
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType STRING
            = MediaType.get("text/plain; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private static final int GET = 1;
    private static final int POST = 2;
    private static final String SERVERURL = "http://47.92.233.174:5000/";
    private static final String LOCALURL = "http://10.0.2.2:5000/";

    // 从后端拿来的数据
    public List<Integer> clubId = new ArrayList<>();       // 社团ID
    public List<String> clubName = new ArrayList<>();      // 社团名字
    public List<String> introduction = new ArrayList<>();  // 社团简介
    public List<String> president = new ArrayList<>();     // 社团主席

    // RecyclerView的适配器
    public class ManageClubsAdapter extends ClubListAdapter {

        public ManageClubsAdapter(Context mContext, List<String> clubName, List<String> introduction) {
            super(mContext, clubName, introduction);
        }
    }

    public void initToolBar() {
        mNavigation.setTitle("管理的社团");
        setSupportActionBar(mNavigation);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_clubs);
        shared = this.getSharedPreferences("share", MODE_PRIVATE);
        userId = shared.getString("userId", "");
        // 为了测试我需要拿到管理了社团的人的userId
        userId = "2";

        Intent intent = getIntent();

        mNavigation = findViewById(R.id.managed_club_title);
        initToolBar();
        mNavigation.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setResult(3);
                finish();
            }
        });

        // 设置布局管理器
        mRecyclerView = this.findViewById(R.id.managed_club_list);
        mAdapter = new ManageClubsAdapter(this, clubName, introduction);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (userId != null)
        getDataFromGet(SERVERURL + "club/query/admin?userId=" + userId);
    }

    /**
     * 启动社团主页
     * @param position
     */
    public void sendMessage(int position) {
        Intent intent = new Intent(this, ClubHomeActivity.class);
        intent.putExtra("clubId", clubId.get(position));
        startActivity(intent);
    }

    // 在获得GET请求返回的数据后更新UI
    private void updateView() {
        mAdapter = new ManageClubsAdapter(this, clubName, introduction);
        mRecyclerView.setAdapter(mAdapter);

        // 下面是为点击事件添加的代码
        mAdapter.setOnItemClickListener(new ManageClubsAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                sendMessage(position);
            }
        });
    }

    // 处理get请求与post请求的回调函数
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Log.e("TAG", (String)msg.obj);
            switch (msg.what){
                case GET:
                    try {
                        parseJsonPacket((String)msg.obj);
                        updateView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case POST:
                    break;
            }
            return true;
        }
    });

    /**
     * 解析get返回的json包
     * @param json get返回的json包
     * @throws JSONException 解析出错
     */
    private void parseJsonPacket(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        JSONArray clubList = jsonObject.getJSONArray("clubSummary");
        for (int i = 0; i < clubList.length(); i++) {
            clubId.add(clubList.getJSONObject(i).getInt("clubId"));
            clubName.add(clubList.getJSONObject(i).getString("clubName"));
            introduction.add(clubList.getJSONObject(i).getString("introduction"));
            president.add(clubList.getJSONObject(i).getString("president"));
        }
    }

    // 使用get获取数据
    private void getDataFromGet(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("URL", url);
                    String result = get(url);
                    Log.e("RES", result);
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
}