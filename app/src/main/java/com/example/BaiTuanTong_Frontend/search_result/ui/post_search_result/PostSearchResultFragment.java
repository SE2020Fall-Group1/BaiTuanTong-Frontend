package com.example.BaiTuanTong_Frontend.search_result.ui.post_search_result;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BaiTuanTong_Frontend.HttpServer;
import com.example.BaiTuanTong_Frontend.PostContentActivity;
import com.example.BaiTuanTong_Frontend.PostListAdapter;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.club.ClubHomeActivity;
import com.example.BaiTuanTong_Frontend.home.ui.home.HomeFragment;

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

import static android.content.Context.MODE_PRIVATE;

public class PostSearchResultFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView;
    private PostSearchResultAdapter mPostSearchResultAdapter;

    public String userId;

    // 与后端通信
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType STRING
            = MediaType.get("text/plain; charset=utf-8");
    private final OkHttpClient client = HttpServer.client;
    private static final int GET = 1;
    private static final int POST = 2;
    private static final int getSearchResult = 0;
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

    /**
     * 用户点击的动态的ID
     * 由此页面点入某一条动态后，若从该动态回退到此页面，需要刷新该动态对应搜索结果列表项的点赞评论数
     * 为此，在startActivity()调用前设置clickedPosition为对应列表位置
     * 当回退时调用此Fragment的onResume()查询clickedPosition，若不是默认值-1则更新对应动态信息
     * 最后将clickedPosition重新设置为默认值防止其他情况onResume()被调用时进行信息更新
     */
    private int clickedPosition = -1;

    // RecyclerView的适配器
    public class PostSearchResultAdapter extends PostListAdapter {

        public PostSearchResultAdapter(Context mContext, List<String> title, List<String> clubName,
                                       List<String> text, List<String> likeCnt, List<String> commentCnt) {
            super(mContext, title, clubName, text, likeCnt, commentCnt);
        }

        public PostSearchResultAdapter(Context mContext, List<String> title, List<String> clubName,
                                       List<String> text, List<String> likeCnt, List<String> commentCnt, List<Bitmap> clubImg) {
            super(mContext, title, clubName, text, likeCnt, commentCnt, clubImg);
        }

        class PostSearchResultViewHolder extends PostListAdapter.PostListViewHolder {
            public PostSearchResultViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
                super(itemView, onItemClickListener);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences shared = getActivity().getSharedPreferences("share",  MODE_PRIVATE);
        userId = shared.getString("userId", "");

        mView = inflater.inflate(R.layout.fragment_post_search_result, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById((R.id.post_search_result_recyclerView));

        mPostSearchResultAdapter = new PostSearchResultAdapter(getActivity(), title, clubName, text, likeCnt, commentCnt);
        mRecyclerView.setAdapter(mPostSearchResultAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return mView;
    }

    public static PostSearchResultFragment newInstance(String text, int item) {
        Bundle bundle = new Bundle();
        bundle.putString("searchText", text);
        bundle.putInt("item", item);

        PostSearchResultFragment fragment = new PostSearchResultFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("msg", "onResume");
        if (postId.isEmpty()){
            Log.e("msg", "empty");
            getDataFromGet(SERVERURL + "post/search?keyword=" + getArguments().getString("searchText"), getSearchResult);
        } else if (clickedPosition != -1) {
            String strPostId = postId.get(clickedPosition).toString();
            getDataFromGet(SERVERURL + "post/view/info?userId=" + userId + "&postId=" + strPostId, getPostInfo);
        }
    }

    // 跳转到社团主页，传递参数position（该动态再列表中的位置）
    private void startClubHomeActivity(Integer position) {
        Intent intent = new Intent(getActivity(), ClubHomeActivity.class);
        intent.putExtra("clubId", clubId.get(position));
        startActivity(intent);
    }

    // 跳转到动态内容界面，传递参数position（该动态在列表中的位置）
    private void startPostContentActivity(Integer position) {
        clickedPosition = position;
        Intent intent = new Intent(getActivity(), PostContentActivity.class);
        intent.putExtra("postId", postId.get(position));
        startActivity(intent);
    }

    // 在获得GET请求返回的数据后更新UI
    private void updateView() {
        mPostSearchResultAdapter = new PostSearchResultAdapter(getActivity(), title, clubName, text, likeCnt, commentCnt);
        mRecyclerView.setAdapter(mPostSearchResultAdapter);

        if (clubImg.isEmpty()) {
            for (int i = 0; i < imgUrl.size(); i++)
                clubImg.add(null);
        }

        // 通过url获得图片
        for (int i = 0; i < imgUrl.size(); i++) {
            getDataFromGet(SERVERURL + "static/images/tiny/" + imgUrl.get(i), getImg + i);
        }

        // 下面是为点击事件添加的代码
        mPostSearchResultAdapter.setOnItemClickListener(new PostSearchResultAdapter.OnItemClickListener() {
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
        /*clubImg.set(position, bm);
        View view = mRecyclerView.getLayoutManager().findViewByPosition(position);
        if (null != view && null != mRecyclerView.getChildViewHolder(view)){
            PostSearchResultAdapter.PostSearchResultViewHolder viewHolder =
                    (PostSearchResultAdapter.PostSearchResultViewHolder) mRecyclerView.getChildViewHolder(view);
            viewHolder.club_img.setImageBitmap(bm);
        }*/

        while (position >= clubImg.size()){
            clubImg.add(null);
        }
        clubImg.set(position, bm);
        mPostSearchResultAdapter = new PostSearchResultAdapter(getActivity(), title, clubName, text, likeCnt, commentCnt, clubImg);
        mRecyclerView.setAdapter(mPostSearchResultAdapter);

        // 下面是为点击事件添加的代码
        mPostSearchResultAdapter.setOnItemClickListener(new HomeFragment.HomeFragmentAdapter.OnItemClickListener() {
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
    }

    // 处理get请求与post请求的回调函数
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            // Log.e("TAG", (String)msg.obj);
            try {
                if (msg.what == getSearchResult) {
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