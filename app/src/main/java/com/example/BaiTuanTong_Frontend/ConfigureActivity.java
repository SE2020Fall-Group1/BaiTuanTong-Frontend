package com.example.BaiTuanTong_Frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConfigureActivity extends AppCompatActivity {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private ImageView imgShow = null;
    private String imgPath = null;
    private final int IMAGE_CODE = 0;
    Uri bitmapUri = null;
    private final String IMAGE_TYPE = "image/*";
    private Button btn_modify_touxiang;

    // 与后端通信部分
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private final OkHttpClient client = new OkHttpClient();
    private static final int GET = 1;
    private static final int POST = 2;
    private static final int POST_IMG = 3;
    private static final String SERVERURL = "http://47.92.233.174:5000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        imgShow = findViewById(R.id.iv_touxiang);
        btn_modify_touxiang = findViewById(R.id.btn_modify_touxiang);
        btn_modify_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        // TODO Auto-generated method stub
        boolean isKitKatO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Intent getAlbum;
        if (isKitKatO) {
            getAlbum = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        }
        getAlbum.setType(IMAGE_TYPE);

        startActivityForResult(getAlbum, IMAGE_CODE);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {

            Log.e("TAG->onresult", "ActivityResult resultCode error");

            return;

        }
        Bitmap bm = null;
        ContentResolver resolver = getContentResolver();
        if (requestCode == IMAGE_CODE) {
            try {

                Uri originalUri = data.getData();        //获得图片的uri
                Uri bitmapUri = originalUri;
                imgPath = bitmapUri.getPath();
                Log.e("uri", imgPath);

                //isSelectPic = true;
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                //显得到bitmap图片
                imgShow.setImageBitmap(bm);

                // 发送给后端
                //getDataFromPostImg(SERVERURL+"user/image/upload");

                try {
                    String img_base64 = bitmapToBase64(bm);
                    JSONObject json = new JSONObject();
                    SharedPreferences shared = getSharedPreferences("share",  MODE_PRIVATE);
                    String userId = shared.getString("userId", "");
                    json.put("userId", userId);
                    json.put("image", img_base64);
                    String jsondata = json.toString();
                    uploadImageFromPost(SERVERURL + "user/image/upload", jsondata);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Log.e("TAG-->Error", e.toString());
            }
        }
    }

    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 处理get请求与post请求的回调函数
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Log.e("TAG", (String)msg.obj);
            switch (msg.what){
                case POST_IMG:

                    break;
                default: return false;
            }
            return true;
        }
    });

    // 使用get获取数据
    private void getDataFromGet(String url, int what) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("URL", url);
                    String result = get(url);
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = what;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "get failed.");
                }
            }
        }.start();
    }

    // 使用postImg获取数据
    private void getDataFromPostImg(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    // userId参数放在这里设置
                    SharedPreferences shared = getSharedPreferences("share",  MODE_PRIVATE);
                    String userId = shared.getString("userId", "");
                    Log.e("url", url);
                    Log.e("userId",userId);
                    Log.e("imgPath", imgPath);
                    String result = postImg(url, userId, imgPath); //jason用于上传数据，目前不需要
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = POST_IMG;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    IOException.printStackTrace();
                    Log.e("post fail", IOException.toString());
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
     * @param path 图像路径
     * @return 服务器返回的字符串
     * @throws IOException 请求出错
     */
    private String postImg(String url, String userId, String path) throws IOException {
        // 创建发送头像请求
        Log.e("path",path);
        File file = new File(path);
        RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG, file);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", userId)
                .addFormDataPart("file", path, fileBody)
                .build();
        //RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    private void uploadImageFromPost(String url, String json) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = post(url, json); //json用于上传数据，目前不需要
                    Message msg = Message.obtain();
                    msg.what = POST_IMG;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("Fail", "upload failed.");
                }
            }
        }.start();
    }

    private String post(String url, String json) throws IOException {
        Log.e("post", "url: " + url);
        Log.e("post", "json: " + json);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Connection", "close")
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e){
            e.printStackTrace();
            return e.toString();
        }
    }
}