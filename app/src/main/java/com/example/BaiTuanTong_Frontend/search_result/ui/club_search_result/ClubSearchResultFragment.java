package com.example.BaiTuanTong_Frontend.search_result.ui.club_search_result;

import android.content.Context;
import android.content.Intent;
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

import com.example.BaiTuanTong_Frontend.ClubListAdapter;
import com.example.BaiTuanTong_Frontend.R;
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

public class ClubSearchResultFragment extends Fragment {

    private int item;
    private View mView;
    private RecyclerView mRecyclerView;
    private ClubSearchResultAdapter mClubSearchResultAdapter;

    // 与后端通信
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType STRING
            = MediaType.get("text/plain; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private static final int GET = 1;
    private static final int POST = 2;
    private static final int getImg = 3;
    private static final String SERVERURL = "http://47.92.233.174:5000/";
    private static final String LOCALURL = "http://10.0.2.2:5000/";

    // 从后端拿来的数据
    public List<Integer> clubId = new ArrayList<>();       // 社团ID
    public List<String> clubName = new ArrayList<>();      // 社团名字
    public List<String> introduction = new ArrayList<>();  // 社团简介
    public List<String> president = new ArrayList<>();     // 社团主席
    public List<String> imgUrl = new ArrayList<>();         // 社团头像URL
    public List<Bitmap> clubImg = new ArrayList<>();        // 社团头像

    // RecyclerView的适配器
    public class ClubSearchResultAdapter extends ClubListAdapter {

        public ClubSearchResultAdapter(Context mContext, List<String> clubName, List<String> introduction) {
            super(mContext, clubName, introduction);
        }

        public ClubSearchResultAdapter(Context mContext, List<String> clubName, List<String> introduction, List<Bitmap> clubImg) {
            super(mContext, clubName, introduction, clubImg);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        item = getArguments().getInt("item");

        mView = inflater.inflate(R.layout.fragment_club_search_result, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById((R.id.club_search_result_recyclerView));

        mClubSearchResultAdapter = new ClubSearchResultAdapter(getActivity(), clubName, introduction);
        mRecyclerView.setAdapter(mClubSearchResultAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return mView;
    }

    public static ClubSearchResultFragment newInstance(String text, int item) {
        Bundle bundle = new Bundle();
        bundle.putString("searchText", text);
        bundle.putInt("item", item);

        ClubSearchResultFragment fragment = new ClubSearchResultFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (clubId.isEmpty())
            getDataFromGet(SERVERURL + "club/search?keyword=" + getArguments().getString("searchText"), GET);
    }

    // 跳转到社团主页，传递参数position（该动态再列表中的位置）
    private void startClubHomeActivity(Integer position) {
        Intent intent = new Intent(getActivity(), ClubHomeActivity.class);
        intent.putExtra("clubId", clubId.get(position));
        startActivity(intent);
    }

    // 在获得GET请求返回的数据后更新UI
    private void updateView() {
        mClubSearchResultAdapter = new ClubSearchResultAdapter(getActivity(), clubName, introduction);
        mRecyclerView.setAdapter(mClubSearchResultAdapter);

        if (clubImg.isEmpty()) {
            for (int i = 0; i < imgUrl.size(); i++)
                clubImg.add(null);
        }
        // 通过url获得图片
        for (int i = 0; i < imgUrl.size(); i++) {
            getDataFromGet(SERVERURL + "static/images/tiny/" + imgUrl.get(i), getImg + i);
        }

        // 下面是为点击事件添加的代码
        mClubSearchResultAdapter.setOnItemClickListener(new ClubSearchResultAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                startClubHomeActivity(position);
            }
        });
        mView.invalidate();
    }

    private void updateClubImage(Bitmap bm, int position) {
        /*clubImg.set(position, bm);
        View view = mRecyclerView.getLayoutManager().findViewByPosition(position);
        if (null != view && null != mRecyclerView.getChildViewHolder(view)){
            ClubListAdapter.ClubListViewHolder viewHolder =
                    (ClubListAdapter.ClubListViewHolder) mRecyclerView.getChildViewHolder(view);
            viewHolder.club_img.setImageBitmap(bm);
        }*/

        while (position >= clubImg.size()){
            clubImg.add(null);
        }
        clubImg.set(position, bm);
        mClubSearchResultAdapter = new ClubSearchResultAdapter(getActivity(), clubName, introduction, clubImg);
        mRecyclerView.setAdapter(mClubSearchResultAdapter);

        // 下面是为点击事件添加的代码
        mClubSearchResultAdapter.setOnItemClickListener(new ClubSearchResultAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                startClubHomeActivity(position);
            }
        });
    }

    // 处理get请求与post请求的回调函数
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == GET)
                Log.e("TAG", (String)msg.obj);
            try {
                if (msg.what == GET) {
                    parseJsonPacket((String) msg.obj);
                    updateView();
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
            imgUrl.add(clubList.getJSONObject(i).getString("clubImage"));
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
                    if (what == GET) {
                        Log.e("URL", url);
                        String result = get(url);
                        Log.e("RES", result);
                        msg = Message.obtain();
                        msg.what = GET;
                        msg.obj = result;
                        getHandler.sendMessage(msg);
                    } else if (what >= getImg) {
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