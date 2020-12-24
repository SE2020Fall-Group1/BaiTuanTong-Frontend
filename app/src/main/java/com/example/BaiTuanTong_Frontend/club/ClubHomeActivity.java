/*
  文件名: ClubHomeActivity.java
  创建者: 谷丰
  描述: 这个activity是社团主页，在页面上方的工具栏中显示有社团名称，右上角的菜单键提供了社团管理员发布动态，
  社团总管理员管理社团的入口。在工具栏下方显示社团简介，再下方显示社团发布的动态的列表。
 */
package com.example.BaiTuanTong_Frontend.club;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout ;

import com.example.BaiTuanTong_Frontend.GridView.ReleasePostActivity;
import com.example.BaiTuanTong_Frontend.PostContentActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.home.ui.home.PostAdapter;
import com.example.BaiTuanTong_Frontend.widget.CircleImageView;
import com.example.BaiTuanTong_Frontend.widget.CustomEmptyView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class ClubHomeActivity extends AppCompatActivity {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType STRING
            = MediaType.get("text/plain; charset=utf-8");

    private Unbinder bind;
    @BindView(R.id.club_title)
    Toolbar mNavigation;  //顶部导航栏
    @BindView(R.id.get_club_profile)
    TextView clubProfile;   //社团简介文本框
    @BindView(R.id.details)
    TextView detailButton;  //"详情" "收起" 按钮
    @BindView(R.id.empty_note)
    TextView emptyNote;   //列表为空的提示信息
    @BindView(R.id.club_post_list)
    RecyclerView mRecyclerView;  //动态列表
    @BindView(R.id.toolbar_club_picture)
    CircleImageView mCircleImageView; //圆形头像
    @BindView(R.id.toolbar_club_name)
    TextView clubNameView;  //社团名称
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout; //下拉刷新布局
    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView; //加载失败页面

    private boolean extended = false;    //当前文本框是否展开
    private boolean mIsRefreshing = false; //是否正在刷新
    private boolean recycleViewInitiated = false;
    private final OkHttpClient client = new OkHttpClient();
    private static final int GET = 1;
    private static final int POST = 2;
    private static final int GETFAIL = 3;
    private static final int PICTURE = 4;
    private static final String SERVERURL = "http://47.92.233.174:5000/";
    private static final String LOCALURL = "http://10.0.2.2:5000/";


    private PostAdapter mMyAdapter;

    //private List<String> postList = new ArrayList<>(); //动态列表
    public List<Integer> postId = new ArrayList<>();        // 动态ID
    public List<String> title = new ArrayList<>();          // 动态标题
    public List<String> PostClubName = new ArrayList<>();       // 社团名字
    public List<String> text = new ArrayList<>();           // 动态内容
    public List<String> likeCnt = new ArrayList<>();        // 动态点赞数
    public List<String> commentCnt = new ArrayList<>();     // 动态评论数
    public List<Integer> PostClubId = new ArrayList<>();        // 社团ID
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
            //Log.e("TAG", (String)msg.obj);
            switch (msg.what){
                case GET:
                case POST:
                    try {
                        String message = (String)msg.obj;
                        if(!message.equals("club do not exist"))
                            parseJsonPacket((String)msg.obj);
                        String print = clubInfo+"\n"+"社长: "+clubPresident;
                        clubNameView.setText(clubName);
                        clubProfile.setText(print);
                    } catch (JSONException e) {
                        initEmptyView();
                        e.printStackTrace();
                    }
                    break;
                case PICTURE:
                    byte[] picture = (byte[])msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                    mCircleImageView.setImageBitmap(bitmap);
                    break;
                case GETFAIL:
                    initEmptyView();
                    return true;
            }
            if(!recycleViewInitiated) { //无论成功与否，第一次必须初始化RecycleView
                initRecycleView();
                recycleViewInitiated = true;
            }
            refreshComplete();
            return true;
        }
    });

    /**
     * 刷新完成，更新动态列表
     */
    private void refreshComplete() {
        Log.e("Re", "refreshComplete");
        hideEmptyView();
        mMyAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        if(postId.isEmpty()){ //如果动态列表为空,在屏幕中央显示提示信息
            emptyNote.setVisibility(VISIBLE);
            mRecyclerView.setVisibility(GONE);
        }
        else{
            mRecyclerView.setVisibility(VISIBLE);
            emptyNote.setVisibility(GONE);
        }
    }

    /**
     * 解析post返回的json包
     * @param json post返回的json包
     * @throws JSONException 解析出错
     */
    private void parseJsonPacket(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        clubName = jsonObject.getString("clubName");
        clubInfo = jsonObject.getString("introduction");
        clubPresident = jsonObject.getString("president");
        JSONArray postList = jsonObject.getJSONArray("postSummary");
        for (int i = 0; i < postList.length(); i++) {
            postId.add(postList.getJSONObject(i).getInt("postId"));
            title.add(postList.getJSONObject(i).getString("title"));
            PostClubName.add(postList.getJSONObject(i).getString("clubName"));
            text.add(postList.getJSONObject(i).getString("text"));
            likeCnt.add("" + postList.getJSONObject(i).getInt("likeCnt"));
            commentCnt.add("" + postList.getJSONObject(i).getInt("commentCnt"));
            PostClubId.add(postList.getJSONObject(i).getInt("clubId"));
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
        mCircleImageView.setImageResource(R.drawable.ic_hotbitmapgg_avatar);
        getPicture(SERVERURL + "static/images/2.jpg");
        //TODO 从后端获取社团图标后更新图标
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_home);
        bind = ButterKnife.bind(this);
        clubId = getIntent().getIntExtra("clubId", -1);

        initRefreshLayout();
        //initEmptyView();

        initDetailButton();

        initToolBar();
    }

    /**
     * 开始刷新前先清除信息，目前只清除动态列表，因为一般来说社团名称与简介不会发生变化
     */
    protected void clearData(){
        mIsRefreshing = true;
        postId.clear();
        title.clear();
        PostClubName.clear();
        text.clear();
        likeCnt.clear();
        commentCnt.clear();
        PostClubId.clear();
    }


    protected void loadData(){
        getDataFromGet(SERVERURL + "club/homepage?" + "clubId=" + clubId);
        getPicture(SERVERURL + "static/images/2.jpg");
    }

    private void initDetailButton() {
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extended = !extended;
                if(extended){
                    clubProfile.setMaxLines(10);
                    detailButton.setText("[收起]");
                }
                else{
                    clubProfile.setMaxLines(1);
                    detailButton.setText("[详情]");
                }
            }
        });
    }

    public void initRecycleView(){
        //mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setNestedScrollingEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMyAdapter = new PostAdapter(this, title, PostClubName, text, likeCnt, commentCnt);
        mRecyclerView.setAdapter(mMyAdapter);
        /*mMyAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                sendMessage(position);
            }
        });*/
        mMyAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startPostContentActivity(position);
            }
        });
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }

    protected void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_light);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            loadData();
        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            clearData();
            loadData();
        });
    }

    public void initEmptyView() {
        mIsRefreshing = false;
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        emptyNote.setVisibility(GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText("加载失败~(≧▽≦)~请刷新.");
        Snackbar.make(mRecyclerView, "数据加载失败,请重新加载或者检查网络是否链接", Snackbar.LENGTH_SHORT).show();
    }

    public void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
    }

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
                    Message msg = Message.obtain();
                    msg.what = GETFAIL;
                    getHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    private void getPicture(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    byte[] result =  response.body().bytes();
                    Message msg = Message.obtain();
                    msg.what = PICTURE;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "get picture failed.");
                    mCircleImageView.setImageResource(R.drawable.ic_hotbitmapgg_avatar);
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
                    String result = post(url, json); //json用于上传数据，目前不需要
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
                Intent intent = new Intent(this, ReleasePostActivity.class);
                intent.putExtra("clubId", clubId);
                startActivity(intent);
                break;
            case R.id.club_admin_manage_menu_item:
                intent = new Intent(this, EditClubAdminActivity.class);
                intent.putExtra("clubId", clubId);
                startActivity(intent);
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
    private void startPostContentActivity(Integer position){
        //clickedPosition = position;
        Intent intent = new Intent(this, PostContentActivity.class);
        intent.putExtra("postId", postId.get(position));
        startActivity(intent);
    }

    private List<String> getList() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add("动态" + i + "\n社团发布的第"+ i + "条动态" + "");
        }
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}