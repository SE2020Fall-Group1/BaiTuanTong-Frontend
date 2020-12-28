package com.example.BaiTuanTong_Frontend;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConfigureActivity extends AppCompatActivity {

    private ImageView imgShow = null;
    private String imgPath = null;
    private Button btn_modify_touxiang;
    private final int IMAGE_CODE = 0;
    private final String IMAGE_TYPE = "image/*";

    // 与后端通信部分
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private final OkHttpClient client = new OkHttpClient();
    private static final int GET = 1;
    private static final int POST = 2;
    private static final int POST_IMG = 3;
    private static final int GET_IMG = 4;
    private static final String SERVERURL = "http://47.92.233.174:5000";
    private static String userId_str = null;

    // 本地存储头像路径，在安卓手机里
    private String txPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        // 检查读写权限
        checkStoragePermissions(this);
        // userId参数放在这里设置
        SharedPreferences shared = getSharedPreferences("share",  MODE_PRIVATE);
        userId_str = shared.getString("userId", "");

        // 头像路径，即/storage/emulated/0/Android/data/com.example.BaiTuanTong_Frontend/files/Download/touxiang.jpg
        txPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/touxiang.jpg";
        imgShow = findViewById(R.id.iv_touxiang);
        // 首先尝试从本地路径获取头像，没有的话使用默认的“照相机”icon
        getTouxiang();
        // 从后端刷新头像
        getDataFromGet(SERVERURL + "/user/image/download?userId="+userId_str, GET);

        btn_modify_touxiang = findViewById(R.id.btn_modify_touxiang);

        btn_modify_touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    // 检查读写权限
    private static final int REQUEST_EXTERNAL_STORAGE=1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static void checkStoragePermissions(Activity activity) {
        try {
            //监测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permission1 = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                //没有写的权限，去申请写的权限，或弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
            if (permission1 != PackageManager.PERMISSION_GRANTED) {
                //没有读的权限，去申请写的权限，或弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 从本地文件读取头像，没有的话直接返回，imgShow会显示默认的头像
    // 默认的头像地址是/storage/emulated/0/Android/data/com.example.BaiTuanTong_Frontend/files/Download/touxiang.jpg
    // 该地址是APP的私有存储空间
    private void getTouxiang() {
        Bitmap bitmap = null;
        try{
            File file = new File(txPath);
            // 本地没有保存的头像
            if (file.length() == 0) {
                Log.e("no touxiang picture: ", "local touxiang picture is null");
                return;
            }
            // 根据指定文件路径构建缓存输入流对象，文件不存在则会出现一个异常
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(txPath));
            // 从缓存输入流中解码位图数据
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
            imgShow.setImageBitmap(bitmap);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // 打开图片管理器，选择新头像
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
    // 选择头像后返回时执行
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                //显得到bitmap图片
                imgShow.setImageBitmap(bm);

                // 处理图像，获取路径
                handleImageOnKitKat(data);
                Log.e("img path", imgPath);
                // 发送给后端
                getDataFromPostImg(SERVERURL+"/user/image/upload");

            } catch (IOException e) {
                Log.e("TAG-->Error", e.toString());
            }
        }
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
        }
    }
    // 处理get请求与post请求的回调函数
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case POST_IMG:
                    Toast.makeText(getBaseContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case GET:
                    if (((String) msg.obj).contains("invalid userId") ||((String) msg.obj).contains("no user image"))
                        Toast.makeText(getBaseContext(), "加载头像失败！", Toast.LENGTH_SHORT).show();
                    else
                        getDataFromGet(SERVERURL + (String) msg.obj, GET_IMG);
                    break;
                case GET_IMG:
                    imgShow.setImageBitmap((Bitmap) msg.obj);
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
                    if (what == GET) {
                        // 请求一个图片的url
                        String result = get(url);
                        Log.e("TAG", result);
                        Message msg = Message.obtain();
                        msg.what = what;
                        msg.obj = result;
                        getHandler.sendMessage(msg);
                    }
                    else if (what == GET_IMG) {
                        // 获得图片url后请求图片
                        InputStream inputStream = getImg(url);
                        //将输入流数据转化为Bitmap位图数据
                        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                        Log.e("touxiang stores in ",txPath);
                        File file=new File(txPath);
                        file.createNewFile();
                        //创建文件输出流对象用来向文件中写入数据
                        FileOutputStream out=new FileOutputStream(file);
                        //将bitmap存储为jpg格式的图片
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                        //刷新文件流
                        out.flush();
                        out.close();
                        Message msg=Message.obtain();
                        msg.what = what;
                        msg.obj = bitmap;
                        getHandler.sendMessage(msg);
                    }
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "get failed.");
                    Log.e("IOException", IOException.toString());
                }
            }
        }.start();
    }

    // 使用postImg上传修改的头像
    private void getDataFromPostImg(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("url", url);
                    Log.e("userId",userId_str);
                    Log.e("imgPath", imgPath);
                    String result = postImg(url, userId_str, imgPath); //jason用于上传数据，目前不需要
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = POST_IMG;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                    Log.e("Exception: ", IOException.getMessage());
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
     * Okhttp的postImg请求
     * @param url 向服务器请求的url
     * @param path 图像路径
     * @return 服务器返回的字符串
     * @throws IOException 请求出错
     */
    private String postImg(String url, String userId, String path) throws IOException {
        // 创建发送头像请求
        Log.e("path",path);
        File file = new File(path);
        if (file == null)
            Log.e("file create wrong", "sad");
        Log.e("file name:", file.getName());
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", userId)
                .addFormDataPart("image", file.getName(), fileBody)
                .build();
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