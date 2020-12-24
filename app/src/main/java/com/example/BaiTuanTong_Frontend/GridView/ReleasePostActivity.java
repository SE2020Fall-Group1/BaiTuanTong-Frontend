package com.example.BaiTuanTong_Frontend.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.BaiTuanTong_Frontend.R;

import com.bumptech.glide.Glide;

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

public class ReleasePostActivity extends AppCompatActivity {

    private GridView gridView;
    private Context mContext;
    private ArrayList<String> mPicList = new ArrayList<>();
    private MyGridViewAdapter myGridViewAdapter;
    private Button myButton;
    private EditText myTitle;
    private EditText myText;
    private String post_title;
    private String post_text;

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
                case GET://know undefined

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject((String) msg.obj);
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //   club_profile.setText((String)msg.obj);
                    //   adminList.
                    break;
                case POST:
                    //club_profile.setText((String)msg.obj);
                    try {
                        parseJsonPacket((String)msg.obj);
                        if (code == 200){
                            Toast.makeText(getApplicationContext(),
                                    "success",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if (code == 400){
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

    private void viewPluImg(int pos){
        Toast.makeText(this, "click a pic", Toast.LENGTH_SHORT).show();
    }
    private void selectPic(int maxTotal) {
        Toast.makeText(this, "want to update a pic", Toast.LENGTH_SHORT).show();
     //   PictureSelector.create(this, maxTotal);
    }

    private void initGridView()
    {
        myGridViewAdapter = new MyGridViewAdapter(mContext, mPicList);
        gridView.setAdapter(myGridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == parent.getChildCount() - 1) {
                    //如果“增加按钮形状的”图片的位置是最后一张，且添加了的图片的数量不超过9张，才能点击
                    //这个位置就是“加号”的位置。
                    if (mPicList.size() == 9) {
                        //最多添加5张图片
                        viewPluImg(position);
                    } else {
                        //添加凭证图片
                        selectPic(9 - mPicList.size());
                    }
                } else {
                    viewPluImg(position);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_post);

//        getActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();  //设置返回键功能,这样点击左上角返回按钮时才能返回到同一个社团主页
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        myOnClickListener = new MyOnClickListener();

        mContext = this;
        gridView = (GridView)findViewById(R.id.Gridview1);
     //   gridView.setAdapter(myGridViewAdapter);
        initGridView();
     //   gridView.setAdapter(new MyGridViewAdapter(ReleasePostActivity.this));

        //点击提交按钮，目前只支持传回的动态内容为文字和标题。
        myTitle = (EditText)findViewById(R.id.EditPostTitle);//title
        myText = (EditText)findViewById(R.id.EditPostText);//text
        myButton = (Button) findViewById(R.id.Submit_button);
        myButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "submitting", Toast.LENGTH_SHORT).show();
                post_title = myText.getText().toString();
                post_text = myText.getText().toString();//获取两部分的输入信息。

                ReleasePostActivity.this.finish();
            }
        });


    }


    // 处理选择的照片的地址
 /*   private void refreshAdapter(List<LocalMedia> picList) {
        for (LocalMedia localMedia : picList) {
            //被压缩后的图片路径
            if (localMedia.isCompressed()) {
                String compressPath = localMedia.getCompressPath(); //压缩后的图片路径
                mPicList.add(compressPath); //把图片添加到将要上传的图片数组中
                myGridViewAdapter.notifyDataSetChanged();
            }
        }
    }
*/
}