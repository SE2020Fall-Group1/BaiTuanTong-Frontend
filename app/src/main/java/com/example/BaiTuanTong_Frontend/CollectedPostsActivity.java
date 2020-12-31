package com.example.BaiTuanTong_Frontend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CollectedPostsActivity extends AppCompatActivity {

    private Toolbar mNavigation;
    private RecyclerView mRecyclerView;
    private CollectedPostsAdapter mAdapter;

    private String userId;
    private SharedPreferences shared;

    // 与后端通信
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType STRING
            = MediaType.get("text/plain; charset=utf-8");
    private final OkHttpClient client = HttpServer.client;
    private static final int GET = 1;
    private static final int POST = 2;
    private static final int getResult = 0;
    private static final int getPostInfo = 1;
    private static final int getImg = 3;
    private static final String SERVERURL = HttpServer.CURRENTURL;
    private static final String LOCALURL = "http://10.0.2.2:5000/";
    private int retry_time = 0;

    // 从后端拿来的数据
    public List<Integer> postId = new ArrayList<>();        // 动态ID
    public List<String> title = new ArrayList<>();          // 动态标题
    public List<String> clubName = new ArrayList<>();       // 社团名字
    public List<String> text = new ArrayList<>();           // 动态内容
    public List<String> likeCnt = new ArrayList<>();        // 动态点赞数
    public List<String> commentCnt = new ArrayList<>();     // 动态评论数
    public List<Integer> clubId = new ArrayList<>();        // 社团ID
    public List<String> imgUrl = new ArrayList<>();         // 社团头像URL
    public List<Bitmap> clubImg = new ArrayList<>();        // 社团头像

    private int clickedPosition = -1;

    // RecyclerView的适配器
    public class CollectedPostsAdapter extends PostListAdapter {

        public CollectedPostsAdapter(Context mContext, List<String> title, List<String> clubName, List<String> text, List<String> likeCnt, List<String> commentCnt) {
            super(mContext, title, clubName, text, likeCnt, commentCnt);
        }

        class CollectedPostsViewHolder extends PostListAdapter.PostListViewHolder {
            public CollectedPostsViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
                super(itemView, onItemClickListener);
            }
        }
    }

    public void initToolBar() {
        mNavigation.setTitle("收藏的动态");
        setSupportActionBar(mNavigation);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collected_posts);

        shared = this.getSharedPreferences("share", MODE_PRIVATE);
        userId = shared.getString("userId", "");

        Intent intent = getIntent();

        mNavigation = findViewById(R.id.collected_posts_title);
        initToolBar();
        mNavigation.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setResult(3);
                finish();
            }
        });

        // 设置布局管理器
        mRecyclerView = this.findViewById(R.id.collected_posts_list);
        mAdapter = new CollectedPostsAdapter(this, title, clubName, text, likeCnt, commentCnt);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (userId != null)
            getDataFromGet(SERVERURL + "post/collection?userId=" + userId, getResult);
    }

    public void onResume() {
        super.onResume();
        Log.e("msg", "onResume");
        if (clickedPosition != -1) {
            String strPostId = postId.get(clickedPosition).toString();
            getDataFromGet(SERVERURL + "post/view/info?userId=" + userId + "&postId=" + strPostId, getPostInfo);
        }
    }

    // 跳转到社团主页，传递参数position（该动态再列表中的位置）
    private void startClubHomeActivity(Integer position) {
        Intent intent = new Intent(this, ClubHomeActivity.class);
        intent.putExtra("clubId", clubId.get(position));
        startActivity(intent);
    }

    // 跳转到动态内容界面，传递参数position（该动态在列表中的位置）
    private void startPostContentActivity(Integer position) {
        clickedPosition = position;
        Intent intent = new Intent(this, PostContentActivity.class);
        intent.putExtra("postId", postId.get(position));
        startActivity(intent);
    }

    // 在获得GET请求返回的数据后更新UI
    private void updateView() {
        mAdapter = new CollectedPostsAdapter(this, title, clubName, text, likeCnt, commentCnt);
        mRecyclerView.setAdapter(mAdapter);

        if (clubImg.isEmpty()) {
            for (int i = 0; i < imgUrl.size(); i++)
                clubImg.add(null);
        }

        // 通过url获得图片
        for (int i = 0; i < imgUrl.size(); i++) {
            getDataFromGet(SERVERURL + "static/images/tiny/" + imgUrl.get(i), getImg + i);
        }

        // 下面是为点击事件添加的代码
        mAdapter.setOnItemClickListener(new CollectedPostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.post_clubName: startClubHomeActivity(position); break;
                    case R.id.club_img: startClubHomeActivity(position); break;
                    default: startPostContentActivity(position);
                }
            }
        });
        Log.e("msg", "invalidate");
        //mView.invalidate();
    }

    // 在获得GET请求返回的数据后更新点赞数、评论数和点赞状态
    private void updatePostInfo() {
        View view = mRecyclerView.getLayoutManager().findViewByPosition(clickedPosition);
        if (null != view && null != mRecyclerView.getChildViewHolder(view)){
            PostListAdapter.PostListViewHolder viewHolder =
                    (PostListAdapter.PostListViewHolder) mRecyclerView.getChildViewHolder(view);
            viewHolder.post_likeCnt.setText(likeCnt.get(clickedPosition));
            viewHolder.post_commentCnt.setText(commentCnt.get(clickedPosition));
            //viewHolder.post_likeCnt.invalidate();
            //viewHolder.post_commentCnt.invalidate();
        }
        clickedPosition = -1;
    }

    private void updateClubImage(Bitmap bm, int position) {
        clubImg.set(position, bm);
        View view = mRecyclerView.getLayoutManager().findViewByPosition(position);
        if (null != view && null != mRecyclerView.getChildViewHolder(view)){
            PostListAdapter.PostListViewHolder viewHolder =
                    (PostListAdapter.PostListViewHolder) mRecyclerView.getChildViewHolder(view);
            viewHolder.club_img.setImageBitmap(bm);
        }
    }

    // 处理get请求与post请求的回调函数
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            //Log.e("TAG", (String)msg.obj);
            try {
                if (msg.what == getResult) {
                    Log.e("msg", "startParsing");
                    parseJsonPacketForView((String) msg.obj);
                    updateView();
                } else if (msg.what == getPostInfo) {
                    Log.e("msg", "startParsing");
                    parseJsonPacketForInfo((String) msg.obj);
                    updatePostInfo();
                } else if (msg.what >= getImg) {
                    int position = msg.what - getImg;
                    updateClubImage((Bitmap) msg.obj, position);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    });

    /**
     * 解析为了更新整个列表而get返回的json包
     * @param json get返回的json包
     * @throws JSONException 解析出错
     */
    private void parseJsonPacketForView(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        JSONArray postList = jsonObject.getJSONArray("postSummary");
        for (int i = 0; i < postList.length(); i++) {
            postId.add(postList.getJSONObject(i).getInt("postId"));
            title.add(postList.getJSONObject(i).getString("title"));
            clubName.add(postList.getJSONObject(i).getString("clubName"));
            text.add(postList.getJSONObject(i).getString("text"));
            likeCnt.add("" + postList.getJSONObject(i).getInt("likeCnt"));
            commentCnt.add("" + postList.getJSONObject(i).getInt("commentCnt"));
            clubId.add(postList.getJSONObject(i).getInt("clubId"));
            imgUrl.add(postList.getJSONObject(i).getString("clubImage"));
        }
    }

    /**
     * 解析为了更新动态信息而get返回的json包
     * @param json get返回的json包
     * @throws JSONException 解析出错
     */
    private void parseJsonPacketForInfo(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        // int code = jsonObject.getInt("code");
        //if (code == 200 && clickedPosition != -1) {
        if (clickedPosition != -1) {
            //isLiked.set(clickedPosition, (jsonObject.getBool("isLiked"));
            likeCnt.set(clickedPosition, ((Integer)jsonObject.getInt("likeCnt")).toString());
            commentCnt.set(clickedPosition, ((Integer)jsonObject.getInt("commentCnt")).toString());
        }
    }

    // 使用get获取数据
    private void getDataFromGet(String url, int what) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Message msg;
                try {
                    if (what >= getImg) {
                        InputStream inputStream = getImg(url);
                        //将输入流数据转化为Bitmap位图数据
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        msg = Message.obtain();
                        msg.what = what;
                        msg.obj = bitmap;
                        getHandler.sendMessage(msg);
                    } else {
                        Log.e("URL", url);
                        String result = get(url);
                        Log.e("RES", result);
                        msg = Message.obtain();
                        msg.what = what;
                        msg.obj = result;
                        getHandler.sendMessage(msg);
                    }
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "get failed.");
                }
            }
        }.start();
    }

    // 使用post获取数据
    private void getDataFromPost(String url, String json) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = post(url, json); //json用于上传数据，目前不需要
                    Log.e("RES", result);
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
     * Okhttp的getImg请求
     * @param url 向服务器请求的url
     * @return 服务器返回的字符串
     * @throws IOException 请求出错
     */
    private InputStream getImg(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        //将响应数据转化为输入流数据
        return response.body().byteStream();
    }
}