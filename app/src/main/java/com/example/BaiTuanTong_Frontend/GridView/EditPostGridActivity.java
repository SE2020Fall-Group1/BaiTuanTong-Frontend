package com.example.BaiTuanTong_Frontend.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.BaiTuanTong_Frontend.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditPostGridActivity extends AppCompatActivity {

    private GridView gridView;
    private Context mContext;
    private ArrayList<String> mPicList = new ArrayList<>();
    private MyGridViewAdapter myGridViewAdapter;
    private Button myButton;
    private String post_title;
    private String post_text;

    private int postId;

    private EditText postText;
    private EditText postTitle;

    private String userId;

    //以下为json和okhttp部分！

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType STRING
            = MediaType.get("text/plain; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private static final int GET = 1;
    private static final int POST = 2;
    private static final String SERVERURL = "http://47.92.233.174:5000/";//服务器用 port5000
    private static final String LOCALURL = "http://10.0.2.2:5000/";//本地测试用
    public static final int REQUEST_CODE_SUBMIT = 1;

    private int code;
    private String data;

    //在线测试 处理get和post
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            Log.e("TAG", (String)msg.obj);
            switch (msg.what){
                case GET:// used in this page,for get the post context.

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject((String) msg.obj);
                        Log.e("TAG_GET", (String)msg.obj);
                        post_text = jsonObject.getString("content");
                        post_title = jsonObject.getString("title");
                        Log.e("post_text", post_text);
                        Log.e("post_title", post_title);
                        postText.setText(post_text);
                        postTitle.setText(post_title);
                        /*
                        JSONArray jsonArray = jsonObject.getJSONArray("adminSummary");
                        for (int i = 0; i < jsonArray.length(); ++i)
                        {
                            JSONObject tmp = jsonArray.getJSONObject(i);
                            //   adminList.add("username：" + tmp.getString("username"));
                            //   mMyAdapter.notifyItemRangeChanged(adminList.size()-1, adminList.size());
                            Log.e("!!!", tmp.getString("username"));
                        }

                        //    Log.e("!!!", jsonObjString);

                        //    List<PurchaseOrder> purchaseOrders = (List<PurchaseOrder>) JSONArray.parseArray(jsonObjString, PurchaseOrder.class);
                        */
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //   club_profile.setText((String)msg.obj);
                    //   adminList.
                    break;
                case POST://posting result
                    Log.e("POST_RES", (String) msg.obj);
                    //300:fail, 200:success
                    //club_profile.setText((String)msg.obj);
                    try {
                        parseJsonPacket((String)msg.obj);
                        if (code == 200){
                            Toast.makeText(getApplicationContext(),
                                    "success",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if (code == 300){
                            Toast.makeText(getApplicationContext(),
                                    data,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    });


    /**
     * 解析post返回的json包
     * @param json post返回的json包
     * @throws JSONException 解析出错
     */
    private void parseJsonPacket(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        code = jsonObject.getInt("code");
        data = jsonObject.getString("data");
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

    private void getDataFromGet(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = get(url);
                    Log.e("result", result);
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

    //获取动态内容，填充到text里面
    void getOriginPost()
    {
        Log.e("debug", "I'm coming to get the post!");
        Log.e("userId",""+userId);
        Log.e("postId", ""+postId);
        getDataFromGet(SERVERURL + "post/view?" + "userId=" + userId + "&" + "postId=" + postId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post_grid);
        this.setTitle("编辑动态");

        postText = (EditText)findViewById(R.id.EditPostText);
        postTitle = (EditText)findViewById(R.id.EditPostTitle);

        postId = getIntent().getIntExtra("postId", -1);
        if (postId == -1)
        {
            Toast.makeText(getApplicationContext(),
                    "获取动态id失败，请返回上级页面！",
                    Toast.LENGTH_LONG).show();
        }

        SharedPreferences shared = getSharedPreferences("share", MODE_PRIVATE);
        userId = shared.getString("userId", "");

        Log.e("userId", ""+userId);


        getOriginPost();

    }


}