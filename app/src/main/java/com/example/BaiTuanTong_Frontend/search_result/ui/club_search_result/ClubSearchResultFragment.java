package com.example.BaiTuanTong_Frontend.search_result.ui.club_search_result;

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

public class ClubSearchResultFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView;
    private List<String> mList;
    private ClubSearchResultAdapter mClubSearchResultAdapter;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_club_search_result, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById((R.id.club_search_result_recyclerView));

        String searchText = getArguments().getString("searchText");
        Log.e("searchText", searchText);
        getDataFromGet(SERVERURL + "club/search?keyword=" + getArguments().getString("searchText"));

        // mList = getList(getArguments().getString("searchText"));
        // Log.e("size", "" + clubId.size() + "");
        mClubSearchResultAdapter = new ClubSearchResultAdapter(getActivity(), clubName, introduction);
        mRecyclerView.setAdapter(mClubSearchResultAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return mView;
    }

    public static ClubSearchResultFragment newInstance(String text, int item) {
        Bundle bundle = new Bundle();
        bundle.putInt("item", item);
        bundle.putString("searchText", text);

        ClubSearchResultFragment fragment = new ClubSearchResultFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private List<String> getList(String text) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add("社团" + i + "：“" + text + "”的搜索结果");
        }
        return list;
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
                        mClubSearchResultAdapter = new ClubSearchResultAdapter(getActivity(), clubName, introduction);
                        mRecyclerView.setAdapter(mClubSearchResultAdapter);
                        mView.invalidate();
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