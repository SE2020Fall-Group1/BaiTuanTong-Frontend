package com.example.BaiTuanTong_Frontend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.BaiTuanTong_Frontend.adapter.CommentAdapter;
import com.example.BaiTuanTong_Frontend.club.ClubHomeActivity;
import com.example.BaiTuanTong_Frontend.data.Comment;
import com.example.BaiTuanTong_Frontend.data.CommentDialogFragment;
import com.example.BaiTuanTong_Frontend.data.ListViewUnderScroll;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sackcentury.shinebuttonlib.ShineButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostContentActivity extends AppCompatActivity {

    private ShineButton likeButton;
    private TextView likeButtonText;
    private ShineButton collectButton;
    private TextView collectButtonText;
    private ImageView commentButtonImage;
    private TextView commentButton;
    private ListView commentListView;
    private CommentAdapter commentAdapter;
    private CommentDialogFragment commentDialogFragment;
    private CollapsingToolbarLayout toolBarLayout;
    private Toolbar toolbar;
    private FloatingActionButton clubHeadButton;
    private TextView contentTextView;
    private TextView contentTimeView;
    private TextView contentTitleView;
    private ImageView contentImageView;
    private List<Comment> commentList = new ArrayList<Comment>();
    private List<String> imageUrlList = new ArrayList<>();
    public String userId;
    public String postId;
    private String baseUrl = "http://47.92.233.174:5000/";
    private String viewUrl = baseUrl + "post/view";
    private String imageBaseUrl = baseUrl + "static/images";
    public String getUrl;
    private int likeCnt;
    private boolean isliked;
    private int clubId;
    private boolean isCollected;
    private String imagePath;
    private String publishTime;
    private String clubName;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient okHttpClient = new OkHttpClient();
    public static final int getContentMsg = 0;
    public static final int likeMsg = 1;
    public static final int collectMsg = 2;
    public static final int imgMsg = 3;

    //申请动态内容
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            try {
                switch (msg.what)
                {
                    case getContentMsg:
                        JSONObject jsonObject = new JSONObject((String)msg.obj);
                        String title = jsonObject.getString("title");
                        String content = jsonObject.getString("content");
                        clubName = jsonObject.getString("clubName");
                        isliked = jsonObject.getBoolean("isLiked");
                        likeCnt = jsonObject.getInt("likeCnt");
                        clubId = jsonObject.getInt("clubId");
                        isCollected = jsonObject.getBoolean("isCollected");
                        publishTime = jsonObject.getString("publishTime");
                        JSONArray commentJSONArray = jsonObject.getJSONArray("comments");
                        JSONArray imageUrlJSONArray = jsonObject.getJSONArray("imageUrls");

                        Log.e("title",title);
                        Log.e("like",String.valueOf(isliked));
                        //设置标题，动态内容
                        toolBarLayout.setTitle(clubName);
                        contentTimeView.setText(publishTime);
                        contentTextView.setText(content);
                        contentTitleView.setText(title);
                        TextPaint tp = contentTitleView.getPaint();
                        tp.setFakeBoldText(true);
                        likeButtonText.setText("点赞("+Integer.toString(likeCnt)+")");
                        commentButton.setText("评论("+Integer.toString(commentJSONArray.length())+")");
                        commentList.clear();
                        //创建一个评论列表
                        for(int i=0;i<commentJSONArray.length();i++) {
                            JSONObject commentJSONObject = commentJSONArray.getJSONObject(i);
                            String commentUserName = commentJSONObject.getString("commenterUsername");
                            String commentContent = commentJSONObject.getString("content");
                            commentList.add(new Comment(commentUserName+":", commentContent));
                        }

                        //设置评论list
                        commentListView = (ListViewUnderScroll)findViewById(R.id.comment_list);
                        commentListView.addHeaderView(new ViewStub(PostContentActivity.this));
                        commentAdapter = new CommentAdapter(PostContentActivity.this, commentList);
                        commentListView.setAdapter(commentAdapter);

                        //创建一个图像URL列表
                        imageUrlList.clear();
                        Log.e("imgeUrlList",imageUrlJSONArray.toString());
                        for(int i=0;i<imageUrlJSONArray.length();i++) {
                            Log.e("buliding",Integer.toString(i));
                            String imageUrl = imageBaseUrl+ "/" + imageUrlJSONArray.getString(i);
                            Log.e("url","any");
                            imageUrlList.add(imageUrl);
                        }
                        //申请图片
                        Log.e("img","get img");
                        getImgFromUrl();

                        //点亮图标
                        if(isliked) {
                            Log.e("is!","yes!");
                            PostContentActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("like", String.valueOf(isliked));
                                    likeButton.setOnClickListener(null);
                                    likeButton.performClick();
                                    likeButton.setOnClickListener(new MyOnClickListener());
                                }
                            });
                        }

                        if(isCollected) {
                            PostContentActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("collect", String.valueOf(isCollected));
                                    collectButton.setOnClickListener(null);
                                    collectButton.performClick();
                                    collectButton.setOnClickListener(new MyOnClickListener());
                                }
                            });
                        }

                        break;
                    case imgMsg:
                        contentImageView.setImageBitmap((Bitmap) msg.obj);
                        break;
                    case likeMsg:

                        String result = (String)msg.obj;
                        if(result.equals("success"))
                        {
                            isliked = !isliked;
                            if(isliked)
                            {
                                likeCnt++;
                            }else
                            {
                                likeCnt--;
                            }
                            likeButtonText.setText("点赞("+Integer.toString(likeCnt)+")");
                        }
                        break;
                    case collectMsg:
                        break;
                    default:
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

            return true;
        }
    });

    private  void startClubHomeActivity(){
        Intent intentClub = new Intent(this, ClubHomeActivity.class);
        intentClub.putExtra("userId", userId);
        intentClub.putExtra("clubId",clubId);
        startActivity(intentClub);
    }
    //从post获取数据
    private void getDataFromPost(String url, String json, int what) {
        //Log.e("TAG", "Start getDataFromGet()");
        new Thread(){
            @Override
            public void run() {
                super.run();
                //Log.e("TAG", "new thread run.");
                try {
                    String result = post(url, json);
                    Log.e("result", result);
                    Message msg = Message.obtain();
                    msg.obj = result;
                    msg.what = what;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                }
            }
        }.start();
    }
    /**
     * Okhttp的post请求
     * @param url
     * @param json
     * @return 服务器返回的字符串
     * @throws IOException
     */
    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }
    public void getDataFromGet(String url, int what) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = get(url);
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.obj = result;
                    msg.what = what;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "get failed.");
                }
            }
        }.start();
    }
    /**
     * Okhttp的get请求
     * @param url
     * @return 服务器返回的字符串
     * @throws IOException
     */
    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.e("begin", "newCall");
        Response response = okHttpClient.newCall(request).execute();
        Log.e("end", "newCall");
        return response.body().string();
    }

    private void getImgFromUrl() {
        Log.e("start","start get");
        new Thread() {
            @Override
            public void run() {
                super.run();
                //设置图片
                // 获得图片url后请求图片
                Log.e("img","start getImg");
                for (int i = 0; i < imageUrlList.size(); i++) {
                    Log.e("in","in");
                    String url = imageUrlList.get(i);
                    InputStream inputStream = null;
                    try {
                        inputStream = getImg(url);
                        //将输入流数据转化为Bitmap位图数据
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        String savePath = imagePath + i + ".jpg";
                        Log.e("touxiang stores in ", savePath);
                        File file = new File(savePath);
                        file.createNewFile();
                        //创建文件输出流对象用来向文件中写入数据
                        FileOutputStream out = new FileOutputStream(file);
                        //将bitmap存储为jpg格式的图片
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        //刷新文件流
                        out.flush();
                        out.close();
                        Message msg = Message.obtain();
                        msg.what = imgMsg;
                        msg.obj = bitmap;
                        getHandler.sendMessage(msg);
                    } catch (IOException e) {
                        Log.e("img excep","excep");
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }

    private InputStream getImg(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        //将响应数据转化为输入流数据
        return response.body().byteStream();
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化图像位置
        imagePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/content_"+ postId +"_";
        //找到组件
        setContentView(R.layout.activity_post_content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        contentTitleView = (TextView)findViewById(R.id.content_title);
        contentTextView = (TextView)findViewById(R.id.content_text);
        contentTimeView = (TextView)findViewById(R.id.content_time);
        contentImageView = (ImageView)findViewById(R.id.content_image);
        likeButton = (ShineButton)findViewById(R.id.like_button);
        likeButtonText = (TextView)findViewById(R.id.like_button_text);
        commentButton = (TextView)findViewById(R.id.comment_button);
        commentButtonImage = (ImageView)findViewById(R.id.comment_button_image);
        collectButton = (ShineButton)findViewById(R.id.collect_button);
        collectButtonText = (TextView)findViewById(R.id.collect_button_text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new MyOnClickListener());
        fab.setVisibility(View.INVISIBLE);

        toolBarLayout.setTitle(" ");
        contentTextView.setText("");


        //获取postId和userId
        Intent intent = getIntent();
        Integer intPostId = intent.getIntExtra("postId", 0);
        SharedPreferences shared = getSharedPreferences("share",  MODE_PRIVATE);
        userId = shared.getString("userId", "");
        postId = Integer.toString(intPostId);
        Log.e("postId/userId",postId+"/"+userId);

        //申请动态内容
        getUrl = viewUrl + "?userId="+userId+"&postId="+postId;
        getDataFromGet(getUrl, getContentMsg);


        //创建dialogFragment
        commentDialogFragment = new CommentDialogFragment();

        //按钮设置监听
        likeButton.setOnClickListener(new MyOnClickListener());
        likeButtonText.setOnClickListener(new MyOnClickListener());
        commentButton.setOnClickListener(new MyOnClickListener());
        commentButtonImage.setOnClickListener(new MyOnClickListener());
        collectButton.setOnClickListener(new MyOnClickListener());
        collectButtonText.setOnClickListener(new MyOnClickListener());
        toolbar.setNavigationOnClickListener(new MyOnClickListener());
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Log.e("id",Integer.toString(v.getId()));
            switch(v.getId()){
                case -1:
                    finish();
                    break;
                case R.id.like_button:
                    JSONObject jsonObjectLike = new JSONObject();
                    try {
                        jsonObjectLike.put("userId", userId);
                        jsonObjectLike.put("postId", postId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getDataFromPost(viewUrl+"/like", jsonObjectLike.toString(), likeMsg);
                    break;
                case R.id.like_button_text:
                    PostContentActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            likeButton.performClick();
                        }
                    });
                    break;
                case R.id.comment_button:
                    commentDialogFragment.show(getSupportFragmentManager(),"dialog");
                    break;
                case R.id.comment_button_image:
                    PostContentActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commentButton.performClick();
                        }
                    });
                    break;
                case R.id.collect_button:
                    JSONObject jsonObjectCollect = new JSONObject();
                    try {
                        jsonObjectCollect.put("userId", userId);
                        jsonObjectCollect.put("postId", postId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getDataFromPost(viewUrl+"/collect", jsonObjectCollect.toString(), collectMsg);
                    break;
                case R.id.collect_button_text:
                    PostContentActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            collectButton.performClick();
                        }
                    });
                    break;
                case R.id.fab:
                default:
                    Toast.makeText(PostContentActivity.this, "not implemented", Toast.LENGTH_SHORT);
            }
        }
    }


}