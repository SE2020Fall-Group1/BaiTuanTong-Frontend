package com.example.BaiTuanTong_Frontend.home.ui.Personal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.BaiTuanTong_Frontend.CollectedPostsActivity;
import com.example.BaiTuanTong_Frontend.ConfigureActivity;
import com.example.BaiTuanTong_Frontend.FollowedClubsDisplayActivity;
import com.example.BaiTuanTong_Frontend.HttpServer;
import com.example.BaiTuanTong_Frontend.ManageClubsActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.home.HomePageActivity;
import com.example.BaiTuanTong_Frontend.ui.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class PersonalFragment extends Fragment {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private PersonalViewModel personalViewModel;
    private Button manageClubButton;
    private Button followClubButton;
    private Button collectedPostButton;
    private Button configureButton;
    private Button signOutButton;
    private TextView tv_username;
    private String username;
    private SharedPreferences shared;
    private String userId_str;
    private int userId;
    private ImageView touxiang;
    private String txPath;
    // 与后端通信部分
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private final OkHttpClient client = HttpServer.client;
    private static final int GET = 1;
    private static final int POST = 2;
    private static final int POST_IMG = 3;
    private static final int GET_IMG = 4;
    private static final int LOG_OUT = 5;
    private static final String SERVERURL = HttpServer.CURRENTURL;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        shared = getActivity().getSharedPreferences("share", MODE_PRIVATE);
        username = shared.getString("userName","");
        userId_str = shared.getString("userId","");
        if (username == ""){
            Toast.makeText(getActivity(),
                    "用户名获取失败，请返回上级页面！",
                    Toast.LENGTH_LONG
            ).show();
        }
        if (userId_str == ""){
            Toast.makeText(getActivity(),
                    "用户ID获取失败，请返回上级页面！",
                    Toast.LENGTH_LONG
            ).show();
        }
        Log.e("userId", userId_str);
        userId = Integer.parseInt(userId_str);
        Log.e("userId", "" + userId);

        // 头像路径
        txPath = getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/touxiang.jpg";
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        personalViewModel =
                new ViewModelProvider(this).get(PersonalViewModel.class);
        View root = inflater.inflate(R.layout.fragment_personal, container, false);

        manageClubButton = (Button)root.findViewById(R.id.manage_club);
        followClubButton = (Button)root.findViewById(R.id.follow_club);
        signOutButton = (Button)root.findViewById((R.id.sign_out));
        collectedPostButton = (Button)root.findViewById(R.id.collect_post);
        configureButton = (Button)root.findViewById(R.id.configuration);
        tv_username = (TextView)root.findViewById(R.id.personal_id);
        tv_username.setText(username);
        touxiang = (ImageView)root.findViewById(R.id.personal_img);
        // 检查读写权限
        checkStoragePermissions(getActivity());

        getTouxiang();
        // 从后端刷新头像
        getDataFromGet(SERVERURL + "user/image/download?userId="+userId_str, GET);
        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        // 从后端刷新头像
        getDataFromGet(SERVERURL + "user/image/download?userId="+userId_str, GET);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) { //如果点击关注社团按钮，就跳转到关注社团页面
        super.onActivityCreated(savedInstanceState);

        manageClubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManageClubsActivity.class);
                startActivityForResult(intent, 3);
            }
        });
        followClubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowedClubsDisplayActivity.class);
                startActivityForResult(intent, 3);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId", userId);
                    logOutFromPost(SERVERURL + "user/logout", jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        collectedPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CollectedPostsActivity.class);
                startActivity(intent);
            }
        });
        configureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConfigureActivity.class);
                startActivity(intent);
            }
        });
    }

    private void performExit() {
        SharedPreferences.Editor editor = shared.edit();
        editor.remove("userId");
        editor.remove("userName");
        editor.remove("logged");
        editor.commit();
        getActivity().finish();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3 && resultCode == 3){
            HomePageActivity homePageActivity = (HomePageActivity)getActivity();
            assert homePageActivity != null;
            FragmentManager fragmentManager = homePageActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.replace(R.id.personal_manage, new PersonalFragment());
            fragmentTransaction.commit();
        }
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

    // 从本地文件读取头像
    private void getTouxiang() {
        Bitmap bitmap = null;
        try{
            File file = new File(txPath);
            if (file.length() == 0) {
                Log.e("no touxiang picture: ", "local touxiang picture is null");
                return;
            }
            // 根据指定文件路径构建缓存输入流对象
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(txPath));
            // 从缓存输入流中解码位图数据
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
            touxiang.setImageBitmap(bitmap);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // 处理get请求与post请求的回调函数
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case GET:
                    if (((String) msg.obj).contains("invalid userId") ||((String) msg.obj).contains("no user image"))
                        Toast.makeText(getActivity().getBaseContext(), "加载头像失败！", Toast.LENGTH_SHORT).show();
                    else
                        getDataFromGet(SERVERURL + "static/images/" +(String) msg.obj, GET_IMG);
                    break;
                case GET_IMG:
                    touxiang.setImageBitmap((Bitmap) msg.obj);
                    break;
                case LOG_OUT:
                    performExit();
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
                        String result = get(url);
                        Log.e("TAG", result);
                        Message msg = Message.obtain();
                        msg.what = what;
                        msg.obj = result;
                        getHandler.sendMessage(msg);
                    }
                    else if (what == GET_IMG) {
                        InputStream inputStream = getImg(url);
                        //将输入流数据转化为Bitmap位图数据
                        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                        Log.e("touxiang stores in ",txPath);
                        File file = new File(txPath);
                        file.createNewFile();
                        //创建文件输出流对象用来向文件中写入数据
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            //将bitmap存储为jpg格式的图片
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            //刷新文件流
                            out.flush();
                            out.close();
                        } catch (Exception e){
                            Log.e("File error", e.toString());
                        }
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

    private void logOutFromPost(String url, String json) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("json", json);
                    String result = post(url, json); //json用于上传数据，目前不需要
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = LOG_OUT;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "log out failed.");
                }
            }
        }.start();
    }

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