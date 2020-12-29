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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.BaiTuanTong_Frontend.GridView.EditPostGridActivity;
import com.example.BaiTuanTong_Frontend.GridView.ReleasePostActivity;
import com.example.BaiTuanTong_Frontend.PostContentActivity;
import com.example.BaiTuanTong_Frontend.PostListAdapter;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.home.ui.home.PostAdapter;
import com.example.BaiTuanTong_Frontend.widget.CircleImageView;
import com.example.BaiTuanTong_Frontend.widget.CustomEmptyView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    private SetClubinfoDialogFragment setClubinfoDialogFragment;
    private SetClubImageDialogFragment setClubImageDialogFragment;

    private boolean extended = false;    //当前文本框是否展开
    private boolean mIsRefreshing = false; //是否正在刷新
    private boolean recycleViewInitiated = false;
    private final OkHttpClient client = new OkHttpClient();
    private static final int GET = 1;
    private static final int POST = 2;
    private static final int GETFAIL = 3;
    private static final int PICTURE = 4;
    private static final int FOLLOW = 5;
    private static final int GET_IMG = 6;
    private static final int DELETE_POST = 101;
    private static final int EDIT_POST = 102;
    private int retry_time = 0;
    private static final String SERVERURL = "http://47.92.233.174:5000/";

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
    public Bitmap clubImageBitmap;

    private String clubImageUrl;

    /**
     * 处理get请求与post请求的回调函数
     */
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case GET:
                case POST:
                    try {
                        String message = (String)msg.obj;
                        if(!message.equals("club do not exist"))
                            parseJsonPacket((String)msg.obj);
                        Log.e("Post num", "" + postId.size());
                        if(clubInfo.equals("null"))
                            clubInfo = "社团还没有更新简介哦~";
                        String print = clubInfo+"\n"+"社长: "+clubPresident;
                        clubNameView.setText(clubName);
                        clubProfile.setText(print);
                        initFollowButton();
                        retry_time = 0;
                    } catch (JSONException e) {
                        initEmptyView();
                        e.printStackTrace();
                    }
                    break;
                case GET_IMG:
                    clubImageBitmap = ((Bitmap)msg.obj); 
                    mCircleImageView.setImageBitmap((Bitmap)msg.obj);
                    for (int i = 0; i < postId.size(); i++) {
                        View view = mRecyclerView.getLayoutManager().findViewByPosition(i);
                        if (null != view && null != mRecyclerView.getChildViewHolder(view)){
                            PostListAdapter.PostListViewHolder viewHolder =
                                    (PostListAdapter.PostListViewHolder) mRecyclerView.getChildViewHolder(view);
                            viewHolder.club_img.setImageBitmap((Bitmap)msg.obj);
                        }
                    }
                    return true;
                case GETFAIL:
                    if(retry_time < 5) { //尝试5次，如果不行就放弃
                        retry_time++;
                        if(((String)msg.obj).equals("clubdata"))
                            getDataFromGet(SERVERURL + "club/homepage?" + "clubId=" + clubId + "&" + "userId=" + userId);
                        else
                            getPicture(SERVERURL + "static/images/tiny/" + clubImageUrl);
                    }
                    else {
                        initEmptyView();
                        retry_time = 0;
                    }
                    return true;
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_home);
        bind = ButterKnife.bind(this);
        clubId = getIntent().getIntExtra("clubId", -1);
        permission = getIntent().getIntExtra("permission", 0);
        clubImageUrl = getIntent().getStringExtra("imageUrl");
        byte[] bis = getIntent().getByteArrayExtra("picture");
        clubImageBitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
        mCircleImageView.setImageBitmap(clubImageBitmap);

        SharedPreferences shared = getSharedPreferences("share", MODE_PRIVATE);
        userId = shared.getString("userId", "");

        setClubinfoDialogFragment = new SetClubinfoDialogFragment();
        setClubImageDialogFragment = new SetClubImageDialogFragment();

        initRefreshLayout();

        initDetailButton();

        initToolBar();
    }

    /**
     * 开始刷新前先清除信息，目前只清除动态列表，因为一般来说社团名称与简介不会发生变化
     */
    protected void clearData(){
        mIsRefreshing = true;
        int size = postId.size();
        postId.clear();
        title.clear();
        PostClubName.clear();
        text.clear();
        likeCnt.clear();
        commentCnt.clear();
        PostClubId.clear();
        if(mMyAdapter != null)
            mMyAdapter.notifyItemRangeRemoved(0, size);
    }


    protected void loadData(){
        getDataFromGet(SERVERURL + "club/homepage?" + "clubId=" + clubId + "&" + "userId=" + userId);
        getPicture(SERVERURL + "static/images/tiny/" + clubImageUrl);
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
        mMyAdapter = new PostAdapter(this, title, PostClubName, text, likeCnt, commentCnt);
        mRecyclerView.setAdapter(mMyAdapter);
        mMyAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startPostContentActivity(position);
            }
        });
        mMyAdapter.setOnItemLongClickListener(new PostAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position) {
                if(permission == 0)
                    return;
                startEditPostActivity(position);
            }
        });
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }

    protected void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_light);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            getDataFromGet(SERVERURL + "club/homepage?" + "clubId=" + clubId + "&" + "userId=" + userId);
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
                    Message msg = Message.obtain();
                    msg.what = GET;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "get failed.");
                    Message msg = Message.obtain();
                    msg.what = GETFAIL;
                    msg.obj = "clubdata";
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
                    // 获得图片url后请求图片
                    Log.e("ImgUrl", url);
                    InputStream inputStream = getImg(url);
                    //将输入流数据转化为Bitmap位图数据
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap == null) {
                        Log.e("null bitmap", "123");
                        return;
                    }
                    Message msg=Message.obtain();
                    msg.what = GET_IMG;
                    msg.obj = bitmap;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "get picture failed.");
                    Message msg = Message.obtain();
                    msg.what = GETFAIL;
                    msg.obj = "picture";
                    getHandler.sendMessage(msg);
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
    /**
     *初始化右上角菜单按钮
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (permission == 1) {
            followClubButton.setVisibility(GONE);
            getMenuInflater().inflate(R.menu.club_home_menu, menu);
            menu.getItem(3).setVisible(false);
            menu.getItem(3).setEnabled(false);
        }
        else if(permission == 2){
            followClubButton.setVisibility(GONE);
            getMenuInflater().inflate(R.menu.club_home_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *菜单中按钮被点击时的回调函数
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.release_post_menu_item:
                Intent intent = new Intent(this, ReleasePostActivity.class);
                intent.putExtra("clubId", clubId);
                startActivityForResult(intent, EDIT_POST);
                break;
            case R.id.set_clubinfo_menu_item:
                setClubinfoDialogFragment.show(getSupportFragmentManager(),"dialog");
                break;
            case R.id.set_clubimage_menu_item:
                setClubImageDialogFragment.show(getSupportFragmentManager(),"dialog");
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
    /**
     * 点击列表中某一项时的处理函数
     * @param position 被点击项的序号
     */
    private void startPostContentActivity(Integer position){
        Intent intent = new Intent(this, PostContentActivity.class);
        intent.putExtra("postId", postId.get(position));
        startActivity(intent);
    }

    private void startEditPostActivity(int position){
        Intent intent = new Intent(this, EditPostGridActivity.class);
        intent.putExtra("postId", postId.get(position));
        startActivityForResult(intent, EDIT_POST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("re", "resultCode="+resultCode+"requestCode="+requestCode);
        if(requestCode == DELETE_POST || requestCode == EDIT_POST){
            mSwipeRefreshLayout.setRefreshing(true);
            clearData();
            loadData();
        }
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

    public void setClubImage(Bitmap bm){
        mCircleImageView.setImageBitmap(bm);
        /*for (int i = 0; i < postId.size(); i++) {
            View view = mRecyclerView.getLayoutManager().findViewByPosition(i);
            if (null != view && null != mRecyclerView.getChildViewHolder(view)){
                PostListAdapter.PostListViewHolder viewHolder =
                        (PostListAdapter.PostListViewHolder) mRecyclerView.getChildViewHolder(view);
                viewHolder.club_img.setImageBitmap(bm);
            }
        }*/
    }
}