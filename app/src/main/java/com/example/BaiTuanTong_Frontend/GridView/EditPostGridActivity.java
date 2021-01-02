package com.example.BaiTuanTong_Frontend.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.BaiTuanTong_Frontend.HttpServer;
import com.example.BaiTuanTong_Frontend.R;

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
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditPostGridActivity extends AppCompatActivity {

    private GridView gridView;
    private Context mContext;
    private ArrayList<Uri> mPicList = new ArrayList<>();
    // 图片路径数组，路径可以直接用于传输给后端
    private ArrayList<String> picPathList = new ArrayList<>();
    private List<String> imageUrlList = new ArrayList<>();
    private MyGridViewAdapter myGridViewAdapter;
    private Button myButton;
    private String post_title;
    private String post_text;
    private EditText myTitle;
    private EditText myText;

    private int postId;

    private EditText postText;
    private EditText postTitle;

    // 最多发布图片数
    private final int MAX_IMG_NUM = 1;
    private String userId;

    //以下为json和okhttp部分！

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType STRING
            = MediaType.get("text/plain; charset=utf-8");
    private final OkHttpClient client = HttpServer.client;
    private static final int GET = 1;
    private static final int POST = 2;
    private static final String SERVERURL = HttpServer.CURRENTURL;//服务器用 port5000
    private static final String LOCALURL = "http://10.0.2.2:5000/";//本地测试用
    private String baseUrl = "http://47.92.233.174:5000/";
    private String imageBaseUrl = baseUrl + "static/images";
    public static final int REQUEST_CODE_SUBMIT = 1;

    private int code;
    private int post_code;
    private String post_data;
    private String imagePath;
    public static final int imgMsg = 3;

    private final int IMAGE_CODE = 0;
    private final String IMAGE_TYPE = "image/*";

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

                        //创建一个图像URL列表
                        JSONArray imageUrlJSONArray = jsonObject.getJSONArray("imageUrls");
                        imageUrlList.clear();
                        Log.e("imgeUrlList",imageUrlJSONArray.toString());
                        for(int i=0;i<imageUrlJSONArray.length();i++) {
                            Log.e("buliding",Integer.toString(i));
                            String imageUrl = imageBaseUrl+ "/" + imageUrlJSONArray.getString(i);
                            Log.e("url","any");
                            imageUrlList.add(imageUrl);
                        }
                        /*
                        if(imageUrlJSONArray.length()!=0)
                            contentImageView.setImageDrawable(getResources().getDrawable(R.drawable.post_content_pic));
                         */
                        //申请图片
                        Log.e("img","get img");
                        getImgFromUrl();
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
                    break;
                case POST://posting result
                    Log.e("POST_RES", (String) msg.obj);
                    Toast.makeText(getApplicationContext(),
                           (String) msg.obj,
                           Toast.LENGTH_SHORT).show();
                    //300:fail, 200:success
                    //club_profile.setText((String)msg.obj);
                   try {
                        parseJsonPacket((String)msg.obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    });

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
        Response response = client.newCall(request).execute();
        //将响应数据转化为输入流数据
        return response.body().byteStream();
    }



    /**
     * 解析post返回的json包
     * @param json post返回的json包
     * @throws JSONException 解析出错
     */
    private void parseJsonPacket(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
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
            Log.e("r_code", ""+response.code());
            post_code = response.code();
            Log.e("r_code_post", ""+post_code);
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
                    Log.e("POST_RESULT", result);
                    Message msg = Message.obtain();
                    msg.what = POST;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                }
                Log.e("post_code", ""+post_code);
                if(post_code == 200)
                    EditPostGridActivity.this.finish();

            }
        }.start();
    }


    /**
     * Okhttp的post请求
     * @param url 向服务器请求的url
     * @return 服务器返回的字符串
     * @throws IOException 请求出错
     */
    private String post2(String url) throws IOException {
        RequestBody body;
        if (picPathList.size() > 0) {
            Log.e("path", picPathList.get(0));
            File file = new File(picPathList.get(0));
            if (file == null)
                Log.e("file create wrong", "sad");
            Log.e("file name:", file.getName());
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("postId", Integer.toString(postId))
                    .addFormDataPart("title", post_title)
                    .addFormDataPart("text", post_text)
                    .addFormDataPart("image", file.getName(), fileBody)
                    .build();
        }
        else {
            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("postId", Integer.toString(postId))
                    .addFormDataPart("title", post_title)
                    .addFormDataPart("text", post_text)
                    .build();
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            code = response.code();
            return response.body().string();
        }
    }

    private void getDataFromPost2(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = post2(url); //json用于上传数据，目前不需要
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = POST;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                }
                if (code == 200)
                    EditPostGridActivity.this.finish();
            }
        }.start();
    }

    private void viewPluImg(int pos){
        Toast.makeText(this, "click a pic", Toast.LENGTH_SHORT).show();
    }


    private void selectPic(int maxTotal) {
        Toast.makeText(this, "目前尚不支持编辑图片", Toast.LENGTH_SHORT).show();
        //   PictureSelector.create(this, maxTotal);
        // TODO Auto-generated method stub
        /*
        boolean isKitKatO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Intent getAlbum;
        if (isKitKatO) {
            getAlbum = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        }
        getAlbum.setType(IMAGE_TYPE);

        startActivityForResult(getAlbum, IMAGE_CODE);

         */
    }


    // 选择头像后返回时执行
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {

            Log.e("TAG->onresult", "ActivityResult resultCode error");

            return;

        }
        ContentResolver resolver = getContentResolver();
        if (requestCode == IMAGE_CODE) {
            //获得图片的uri，可以解析出bitmap类型的图像
            Uri originalUri = data.getData();
            mPicList.add(originalUri);

            // 处理图像，获取路径，并且储存在picPathList中
            handleImageOnKitKat(data);
        }
        Log.e("picnum", Integer.toString(mPicList.size()));
        myGridViewAdapter = new MyGridViewAdapter(mContext, mPicList, picPathList);
        gridView.setAdapter(myGridViewAdapter);
    }


    // 通过uri获得图片本机路径，该方法适用于Android4.4以上
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection 来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data){
        Log.e("handleImageOnKitKat", "handleImageOnKitKat: " );
        Uri uri = data.getData();
        String imgPath = null;
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imgPath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imgPath = getImagePath(contentUri,null);
            }else if("content".equalsIgnoreCase(uri.getScheme())){
                //如果是content类型的Uri，则使用普通方式处理
                imgPath = getImagePath(uri,null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())){
                //如果是file类型的Uri,直接获取图片路径即可
                imgPath = uri.getPath();
            }
            // 添加文件的路径到picPathList，该路径是传给后端用
            picPathList.add(imgPath);
        }
    }


    private void initGridView()
    {
        myGridViewAdapter = new MyGridViewAdapter(mContext, mPicList, picPathList);
        gridView.setAdapter(myGridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == parent.getChildCount() - 1) {
                    //如果“增加按钮形状的”图片的位置是最后一张，且添加了的图片的数量不超过9张，才能点击
                    //这个位置就是“加号”的位置。
                    if (mPicList.size() == MAX_IMG_NUM) {
                        //最多添加5张图片
                        viewPluImg(position);
                    } else {
                        //添加凭证图片
                        selectPic(MAX_IMG_NUM - mPicList.size());
                    }
                } else {
                    viewPluImg(position);
                }
            }
        });
    }


    //获取动态内容，填充到text里面
    void getOriginPost()
    {
        Log.e("debug", "I'm coming to get the post!");
        Log.e("userId",""+userId);
        Log.e("postId", ""+postId);
        getDataFromGet(SERVERURL + "post/view?" + "userId=" + userId + "&" + "postId=" + postId);
    }


    void deletePost()
    {
        //以下为okhttp方法。
        JSONObject obj = new JSONObject();
        try {
            obj.put("postId", postId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getDataFromPost(SERVERURL + "post/delete", obj.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete_post_button:

                deletePost();

                break;


            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)//添加右上角三个点儿
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_post_menu, menu);
        //这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post_grid);
        this.setTitle("编辑动态");



        ActionBar actionBar = getSupportActionBar();  //设置返回键功能,这样点击左上角返回按钮时才能返回到同一个社团主页
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mContext = this;
        gridView = (GridView)findViewById(R.id.Gridview1);
     //目前编辑不支持编辑图片，就不显示了
        //   initGridView();


        /*
        获取动态部分（无图片）
         */
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
        /*
        获取动态部分（无图片）
         */
        myTitle = (EditText)findViewById(R.id.EditPostTitle);//title
        myText = (EditText)findViewById(R.id.EditPostText);//text
        myButton = (Button) findViewById(R.id.Submit_button);

        myButton = (Button) findViewById(R.id.Submit_button);
        myButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   Toast.makeText(getApplicationContext(), "submitting", Toast.LENGTH_SHORT).show();
                post_title = myTitle.getText().toString();
                post_text = myText.getText().toString();//获取两部分的输入信息。
                if (post_title.length() == 0)
                {
                    Toast.makeText(getApplicationContext(),
                            "动态标题不能为空！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (post_text.length() == 0)
                {
                    Toast.makeText(getApplicationContext(),
                            "动态内容不能为空！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 新的post方法，不是JSON了
                getDataFromPost2(SERVERURL + "post/edit");

            }
        });

    }

}