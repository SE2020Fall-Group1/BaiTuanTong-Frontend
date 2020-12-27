/*
  文件名: ClubHomeActivity.java
  创建者: 谷丰
  描述: 这个activity是社团主页，在页面上方的工具栏中显示有社团名称，右上角的菜单键提供了社团管理员发布动态，
  社团总管理员管理社团的入口。在工具栏下方显示社团简介，再下方显示社团发布的动态的列表。
 */
package com.example.BaiTuanTong_Frontend.club;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.example.BaiTuanTong_Frontend.data.SetClubinfoDialogFragment;
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
    @BindView(R.id.follow_club_button)
    Button followClubButton; //关注社团按钮

    private Menu mMenu;
    private SetClubinfoDialogFragment setClubinfoDialogFragment;

    private boolean extended = false;    //当前文本框是否展开
    private boolean mIsRefreshing = false; //是否正在刷新
    private boolean recycleViewInitiated = false;
    private final OkHttpClient client = new OkHttpClient();
    private static final int GET = 1;
    private static final int POST = 2;
    private static final int GETFAIL = 3;
    private static final int PICTURE = 4;
    private static final int FOLLOW = 5;
    private int retry_time = 0;
    private static final String SERVERURL = "http://47.92.233.174:5000/";
    private static final String LOCALURL = "http://10.0.2.2:5000/";


    private PostAdapter mMyAdapter;

    public List<Integer> postId = new ArrayList<>();        // 动态ID
    public List<String> title = new ArrayList<>();          // 动态标题
    public List<String> PostClubName = new ArrayList<>();       // 社团名字
    public List<String> text = new ArrayList<>();           // 动态内容
    public List<String> likeCnt = new ArrayList<>();        // 动态点赞数
    public List<String> commentCnt = new ArrayList<>();     // 动态评论数
    public List<Integer> PostClubId = new ArrayList<>();        // 社团ID
    private String clubName;
    public String clubInfo;  //社团简介
    private String clubPresident;  //社长
    public int clubId;
    private String userId;
    private int permission;
    private boolean isFollowed;

    private String userName;

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
                        Log.e("President", clubPresident);
                        clubNameView.setText(clubName);
                        clubProfile.setText(print);
                        initFollowButton();
                        retry_time = 0;
                    } catch (JSONException e) {
                        initEmptyView();
                        e.printStackTrace();
                    }
                    break;
                case PICTURE:
                    byte[] picture = (byte[])msg.obj;
                    Log.e("Picture", picture.toString() + "  Length=" + picture.length);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                    //mCircleImageView.setImageBitmap(bitmap);
                    return true;
                case GETFAIL:
                    if(retry_time < 3) { //尝试三次，如果不行就放弃
                        retry_time++;
                        loadData();
                        return true;
                    }
                    else {
                        initEmptyView();
                        retry_time = 0;
                        break;
                    }
                case FOLLOW:
                    String res = (String)msg.obj;
                    if(res.equals("follow committed")){
                        isFollowed = true;
                    }
                    if(res.equals("follow cancelled")){
                        isFollowed = false;
                    }
                    initFollowButton();
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
        isFollowed = jsonObject.getBoolean("isFollowed");
        //isFollowed = false;
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

    private void initFollowButton(){
        if(isFollowed){
            followClubButton.setText("已关注");
            followClubButton.setTextColor(getResources().getColor(R.color.deepgrey));
            followClubButton.setBackgroundColor(getResources().getColor(R.color.lightgrey));
        }
        else{
            followClubButton.setText("＋ 关注");
            followClubButton.setTextColor(getResources().getColor(R.color.white));
            followClubButton.setBackgroundColor(getResources().getColor(R.color.cherryred));
        }
    }

    public void followClubButtonClickListener(View view){
        followClubFromPost(SERVERURL + "club/follow");
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        clubId = getIntent().getIntExtra("clubId", -1);
        permission = getIntent().getIntExtra("permission", 0);
        SharedPreferences shared = getSharedPreferences("share", MODE_PRIVATE);
        userId = shared.getString("userId", "");
        userName = shared.getString("userName", "");
        setClubinfoDialogFragment = new SetClubinfoDialogFragment();
        loadData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_home);
        bind = ButterKnife.bind(this);

        initRefreshLayout();

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
        getDataFromGet(SERVERURL + "club/homepage?" + "clubId=" + clubId + "&" + "userId=" + userId);
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
            clearData();
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
                    //Log.e("TAG", result);
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
                    /*Message msg = Message.obtain();
                    msg.what = GETFAIL;
                    getHandler.sendMessage(msg);*/
                }
            }
        }.start();
    }

    private void followClubFromPost(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String json = "{\"userId\":" + userId + ",\"clubId\":" + clubId + "}";
                    String result = post(url, json);
                    Log.e("follow club response", result);
                    Message msg = Message.obtain();
                    msg.what = FOLLOW;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "follow failed.");
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        mMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }
    /**
     *初始化右上角菜单按钮
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //目前permission似乎有点儿问题，president和普通人一样 就很气，先特判一波儿
        Log.e("permission", "" + permission);
        Log.e("presidentname", "" + clubPresident);
        Log.e("myname", "" + userName);
        /*if (userName.equals(clubPresident))
        {
            Log.e("debug", "" + "进来了！我是president");
            followClubButton.setVisibility(GONE);
            getMenuInflater().inflate(R.menu.club_home_menu, menu);
        }
        else
        {
            Log.e("debug", "" + "进来了个锤子！我不是president");
            if (permission == 1) {
                followClubButton.setVisibility(GONE);
                getMenuInflater().inflate(R.menu.club_home_menu, menu);
                menu.getItem(2).setVisible(false);
                menu.getItem(2).setEnabled(false);
            }
            if (permission == 2) {
                followClubButton.setVisibility(GONE);
                getMenuInflater().inflate(R.menu.club_home_menu, menu);
            }
        }*/

        if (permission == 1) {
            followClubButton.setVisibility(GONE);
            getMenuInflater().inflate(R.menu.club_home_menu, menu);
            menu.getItem(2).setVisible(false);
            menu.getItem(2).setEnabled(false);
        }
        else if(permission == 2){
            followClubButton.setVisibility(GONE);
            getMenuInflater().inflate(R.menu.club_home_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /*private void hideMenu() {
        for(int i = 0; i < mMenu.size(); i++){
            mMenu.getItem().setVisible(false);
            mMenu.getItem().setEnabled(false);
        }
    }*/

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
            case R.id.set_clubinfo_menu_item:
                setClubinfoDialogFragment.show(getSupportFragmentManager(),"dialog");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    public void setClubProfile(String s) {
        String print = s +"\n"+"社长: "+clubPresident;
        clubProfile.setText(print);
    }
}