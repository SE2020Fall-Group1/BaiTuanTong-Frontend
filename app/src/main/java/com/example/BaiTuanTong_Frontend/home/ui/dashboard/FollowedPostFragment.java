package com.example.BaiTuanTong_Frontend.home.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.BaiTuanTong_Frontend.HttpServer;
import com.example.BaiTuanTong_Frontend.PostContentActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.club.ClubHomeActivity;
import com.example.BaiTuanTong_Frontend.home.ui.home.HomeFragment;
import com.example.BaiTuanTong_Frontend.home.ui.home.PostAdapter;
import com.example.BaiTuanTong_Frontend.PostListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class FollowedPostFragment extends Fragment {
    private View mView;
    // recyclerview
    private RecyclerView rv_post_list;
    private boolean recycleViewInitiated = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRefreshing = false; //是否正在刷新
    private int retry_time = 0;
    private PostAdapter myAdapter;
    private HomeFragment.MyListener ac;

    public String userId;

    // 与后端通信
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType STRING
            = MediaType.get("text/plain; charset=utf-8");
    private final OkHttpClient client = HttpServer.client;
    private static final int GET = 1;
    private static final int POST = 2;
    private static final int GETFAIL = -1;
    private static final int getResult = 0;
    private static final int getPostInfo = 1;
    private static final int getImg = 3;
    private static final String SERVERURL = HttpServer.CURRENTURL;
    private static final String LOCALURL = "http://10.0.2.2:5000/";
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_followed_post, container, false);
        rv_post_list = (RecyclerView)mView.findViewById(R.id.rv_post_list_followed_post);
        mSwipeRefreshLayout = mView.findViewById(R.id.swipe_refresh_layout_followed_post);
        SharedPreferences shared = getActivity().getSharedPreferences("share",  MODE_PRIVATE);
        userId = shared.getString("userId", "");
        setHasOptionsMenu(true);
        initRefreshLayout();
        // 初始RecyclerView
        //initRecyclerLinear();
        return mView;
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        ac = (HomeFragment.MyListener)getActivity();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (clickedPosition != -1) {
            String strPostId = postId.get(clickedPosition).toString();
            getDataFromGet(SERVERURL + "post/view/info?userId=" + userId + "&postId=" + strPostId, getPostInfo);
        }
    }
    // 实现一个接口，搜索框给SearchResultActivity传输搜索字符串info
    public interface MyListener{
        public void sendContent(String info);//发送给SearchResultActivity
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

    protected void clearData(){
        mIsRefreshing = true;
        int size = postId.size();
        postId.clear();
        title.clear();
        clubName.clear();
        text.clear();
        likeCnt.clear();
        commentCnt.clear();
        clubId.clear();
        imgUrl.clear();
        clubImg.clear();
        myAdapter.notifyItemRangeRemoved(0, size);
    }


    protected void loadData(){
        getDataFromGet(SERVERURL + "post/followed?userId="+userId, getResult);
    }

    // 跳转到社团主页，传递参数position（该动态再列表中的位置）
    private void startClubHomeActivity(Integer position) {
        Intent intent = new Intent(getActivity(), ClubHomeActivity.class);
        intent.putExtra("clubId", clubId.get(position));
        intent.putExtra("imageUrl", imgUrl.get(position));
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            clubImg.get(position).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bitmapByte = baos.toByteArray();
            intent.putExtra("picture", bitmapByte);
        }catch (Exception e){
            e.printStackTrace();
        }
        intent.putExtra("permission", 0);
        startActivity(intent);
    }
    // 跳转到动态内容界面，传递参数post_id
    private void startPostContentActivity(Integer position){
        clickedPosition = position;
        Intent intent = new Intent(getActivity(),PostContentActivity.class);
        intent.putExtra("postId", postId.get(position));
        startActivity(intent);
    }
    // 初始化线性布局的循环视图
    private void initRecyclerLinear() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rv_post_list.setLayoutManager(manager);
        updateView();

        rv_post_list.setOnTouchListener((v, event) -> mIsRefreshing);
    }

    // 在获得GET请求返回的数据后更新UI
    private void updateView() {
        myAdapter = new PostAdapter(getActivity(), title, clubName, text, likeCnt, commentCnt);
        rv_post_list.setAdapter(myAdapter);

        if (clubImg.size() < imgUrl.size()) {
            for (int i = clubImg.size(); i < imgUrl.size(); i++)
                clubImg.add(null);
        }
        // 通过url获得图片
        for (int i = 0; i < imgUrl.size(); i++) {
            getDataFromGet(SERVERURL + "static/images/tiny/" + imgUrl.get(i), getImg + i);
        }

        // 下面是为点击事件添加的代码
        myAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.post_clubName:
                    case R.id.club_img:
                        startClubHomeActivity(position); break;
                    default: startPostContentActivity(position);
                }
            }
        });
        // mView.invalidate();
    }
    // 在获得GET请求返回的数据后更新点赞数、评论数和点赞状态
    private void updatePostInfo() {
        View view = rv_post_list.getLayoutManager().findViewByPosition(clickedPosition);
        if (null != view && null != rv_post_list.getChildViewHolder(view)){
            PostListAdapter.PostListViewHolder viewHolder =
                    (PostListAdapter.PostListViewHolder) rv_post_list.getChildViewHolder(view);
            viewHolder.post_likeCnt.setText(likeCnt.get(clickedPosition));
            viewHolder.post_commentCnt.setText(commentCnt.get(clickedPosition));
            //viewHolder.post_likeCnt.invalidate();
            //viewHolder.post_commentCnt.invalidate();
        }
        clickedPosition = -1;
    }

    private void updateClubImage(Bitmap bm, int position) {
        while (position >= clubImg.size()){
            clubImg.add(null);
        }
        clubImg.set(position, bm);
        myAdapter = new PostAdapter(getActivity(), title, clubName, text, likeCnt, commentCnt, clubImg);
        rv_post_list.setAdapter(myAdapter);

        // 下面是为点击事件添加的代码
        myAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.post_clubName:
                    case R.id.club_img:
                        startClubHomeActivity(position); break;
                    default: startPostContentActivity(position);
                }
            }
        });
        /*View view = rv_post_list.getLayoutManager().findViewByPosition(position);
        if (null != view && null != rv_post_list.getChildViewHolder(view)){
            PostListAdapter.PostListViewHolder viewHolder =
                    (PostListAdapter.PostListViewHolder) rv_post_list.getChildViewHolder(view);
            viewHolder.club_img.setImageBitmap(bm);
            viewHolder.club_img.invalidate();
        }*/
    }
    // 处理get请求与post请求的回调函数
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            try {
                if (msg.what == getResult) {
                    parseJsonPacketForView((String) msg.obj);
                    if (recycleViewInitiated)
                        updateView();
                    retry_time = 0;
                } else if (msg.what == getPostInfo) {
                    parseJsonPacketForInfo((String) msg.obj);
                    updatePostInfo();
                } else if (msg.what >= getImg) {
                    int position = msg.what - getImg;
                    updateClubImage((Bitmap) msg.obj, position);
                } else if (msg.what == GETFAIL) {
                    if (retry_time < 10) {
                        retry_time++;
                        loadData();
                        return true;
                    } else {
                        retry_time = 0;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(!recycleViewInitiated) { //无论成功与否，第一次必须初始化RecycleView
                initRecyclerLinear();
                recycleViewInitiated = true;
            }
            refreshComplete();
            return true;
        }
    });


    private void refreshComplete() {
        myAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
    }

    /**
     * 解析get返回的json包
     * @param json get返回的json包
     * @throws JSONException 解析出错
     */
    private void parseJsonPacketForView(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        JSONArray postList = jsonObject.getJSONArray("postSummary");
        if (postList.length()==0){
            Toast.makeText(getActivity().getBaseContext(), "你关注的社团未发布动态！", Toast.LENGTH_SHORT).show();
        }
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
                    if (what == getResult || what == getPostInfo) {
                        Log.e("URL", url);
                        String result = get(url);
                        Log.e("TAG", result);
                        msg = Message.obtain();
                        msg.what = what;
                        msg.obj = result;
                        getHandler.sendMessage(msg);
                    }
                    else if (what >= getImg) {
                        InputStream inputStream = getImg(url);
                        //将输入流数据转化为Bitmap位图数据
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        msg = Message.obtain();
                        msg.what = what;
                        msg.obj = bitmap;
                        getHandler.sendMessage(msg);
                    }
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "get failed.");
                    msg = Message.obtain();
                    msg.what = GETFAIL;
                    getHandler.sendMessage(msg);
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