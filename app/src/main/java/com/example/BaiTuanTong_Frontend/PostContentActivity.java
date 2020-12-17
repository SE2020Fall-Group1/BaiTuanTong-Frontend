package com.example.BaiTuanTong_Frontend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.BaiTuanTong_Frontend.adapter.CommentAdapter;
import com.example.BaiTuanTong_Frontend.data.Comment;
import com.example.BaiTuanTong_Frontend.data.CommentDialogFragment;
import com.example.BaiTuanTong_Frontend.data.ListViewUnderScroll;
import com.example.BaiTuanTong_Frontend.ui.login.LoginActivity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostContentActivity extends AppCompatActivity {

    private Button likeButton;
    private Button commentButton;
    private ListView commentListView;
    private CommentAdapter commentAdapter;
    private CommentDialogFragment commentDialogFragment;
    private CollapsingToolbarLayout toolBarLayout;
    private TextView contentTextView;
    private List<Comment> commentList = new ArrayList<Comment>();
    public String userId;
    public String postId;
    private String baseUrl = "http://47.92.233.174:5000/";
    private String viewUrl = baseUrl+"post/view";
    public String getUrl;
    private int likeCnt;
    private boolean isliked;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient okHttpClient = new OkHttpClient();
    public static final int getContentMsg = 0;
    public static final int likeMsg = 1;

    //申请动态内容
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Log.e("handlerinPostContent",(String)msg.obj);
            //super.handleMessage(msg);
            try {
                switch (msg.what)
                {
                    case getContentMsg:
                        JSONObject jsonObject = new JSONObject((String)msg.obj);
                        String title = jsonObject.getString("title");
                        String content = jsonObject.getString("content");
                        String clubName = jsonObject.getString("clubName");
                        isliked = jsonObject.getBoolean("isLiked");
                        likeCnt = jsonObject.getInt("likeCnt");
                        JSONArray commentJSONArray = jsonObject.getJSONArray("comments");

                        Log.e("title",title);
                        //设置标题，动态内容
                        toolBarLayout.setTitle(title);
                        contentTextView.setText(content);
                        likeButton.setText("点赞（"+Integer.toString(likeCnt)+"）");

                        //创建一个评论列表
                        for(int i=0;i<commentJSONArray.length();i++) {
                            JSONObject commentJSONObject = commentJSONArray.getJSONObject(i);
                            String commentUserName = commentJSONObject.getString("commenterUserName");
                            String commentContent = commentJSONObject.getString("content");
                            commentList.add(new Comment(commentUserName+":", commentContent));
                        }

                        //设置评论list
                        commentListView = (ListViewUnderScroll)findViewById(R.id.comment_list);
                        //commentListView.addHeaderView(new ViewStub(this));
                        commentAdapter = new CommentAdapter(PostContentActivity.this, commentList);
                        commentListView.setAdapter(commentAdapter);
                        break;
                    case likeMsg:
                        String result = (String)msg.obj;
                        if(result.equals("success"))
                        {
                            isliked = !isliked;
                            if(isliked)
                            {
                                likeCnt++;
                            }else
                            {
                                likeCnt--;
                            }
                            likeButton.setText("点赞（"+Integer.toString(likeCnt)+"）");
                        }
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

            return true;
        }
    });

    //从post获取数据
    private void getDataFromPost(String url, String json, int what) {
        //Log.e("TAG", "Start getDataFromGet()");
        new Thread(){
            @Override
            public void run() {
                super.run();
                //Log.e("TAG", "new thread run.");
                try {
                    String result = post(url, json); //jason用于上传数据，目前不需要
                    Log.e("result", result);
                    Message msg = Message.obtain();
                    msg.obj = result;
                    msg.what = what;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                }
            }
        }.start();
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
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }
    public void getDataFromGet(String url, int what) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = get(url);
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.obj = result;
                    msg.what = what;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "get failed.");
                }
            }
        }.start();
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
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //找到组件
        setContentView(R.layout.activity_post_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        contentTextView = (TextView)findViewById(R.id.content_text);
        likeButton = (Button)findViewById(R.id.like_button);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        toolBarLayout.setTitle(" ");
        contentTextView.setText("");

        //获取postId和userId
        Intent intent = getIntent();
        Integer intPostId = intent.getIntExtra("postId", 0);
        SharedPreferences shared = getSharedPreferences("share",  MODE_PRIVATE);
        userId = shared.getString("userId", "");
        postId = Integer.toString(intPostId);
        Log.e("postId/userId",postId+"/"+userId);

        //申请动态内容
        getUrl = viewUrl + "?userId="+userId+"&postId="+postId;
        getDataFromGet(getUrl, getContentMsg);

        //创建dialogFragment
        commentDialogFragment = new CommentDialogFragment();
        commentButton = (Button)findViewById(R.id.comment_button);
        likeButton = (Button)findViewById(R.id.like_button);

        likeButton.setOnClickListener(new MyOnClickListener());
        commentButton.setOnClickListener(new MyOnClickListener());
    }
    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.comment_button:
                    commentDialogFragment.show(getSupportFragmentManager(),"dialog");
                    break;
                case R.id.like_button:
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("userId", userId);
                        jsonObject.put("postId", postId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getDataFromPost(viewUrl+"/like", jsonObject.toString(), likeMsg);
            }
        }
    }


}