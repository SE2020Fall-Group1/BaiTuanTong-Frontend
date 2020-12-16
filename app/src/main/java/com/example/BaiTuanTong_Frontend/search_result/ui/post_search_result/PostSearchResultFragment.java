package com.example.BaiTuanTong_Frontend.search_result.ui.post_search_result;

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

import com.example.BaiTuanTong_Frontend.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.BaiTuanTong_Frontend.search_result.SearchResultActivity.flag;

public class PostSearchResultFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView;
    private PostSearchResultAdapter mPostSearchResultAdapter;

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
    public List<Integer> postId = new ArrayList<>();        // 动态ID
    public List<String> title = new ArrayList<>();          // 动态标题
    public List<String> clubName = new ArrayList<>();       // 社团名字
    public List<String> text = new ArrayList<>();           // 动态内容
    public List<String> likeCnt = new ArrayList<>();        // 动态点赞数
    public List<String> commentCnt = new ArrayList<>();     // 动态评论数

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
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
        if ((flag == 1) && (postId.isEmpty()))
        getDataFromGet(SERVERURL + "post/search?keyword=" + getArguments().getString("searchText"));
    }

    // 在获得GET请求返回的数据后更新UI
    private void updateView() {
        mPostSearchResultAdapter = new PostSearchResultAdapter(getActivity(), title, clubName, text, likeCnt, commentCnt);
        mRecyclerView.setAdapter(mPostSearchResultAdapter);
        mView.invalidate();
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
                        while (flag != 1);
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

        JSONArray postList = jsonObject.getJSONArray("postSummary");
        for (int i = 0; i < postList.length(); i++) {
            postId.add(postList.getJSONObject(i).getInt("postId"));
            title.add(postList.getJSONObject(i).getString("title"));
            clubName.add(postList.getJSONObject(i).getString("clubName"));
            text.add(postList.getJSONObject(i).getString("text"));
            likeCnt.add("" + postList.getJSONObject(i).getInt("likeCnt"));
            // 目前没有回传评论数，伪造一个0
            commentCnt.add("0");
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
}