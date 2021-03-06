package com.example.BaiTuanTong_Frontend.SystemAdministrator;

import com.example.BaiTuanTong_Frontend.SystemAdministrator.EditClubDialogFragment.CreateClubListener;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.SystemAdministrator.EditAdminDialogFragment.ChangeAdminListener;
import com.example.BaiTuanTong_Frontend.home.HomePageActivity;
import com.example.BaiTuanTong_Frontend.ui.login.LoginActivity;
import com.example.BaiTuanTong_Frontend.HttpServer;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.view.View.GONE;


public class ManagerHomePage extends AppCompatActivity implements ChangeAdminListener, CreateClubListener {

    private Toolbar mNavigation;
    private RecyclerView rv_manage_club;
    private LinearLayoutManager manager;
    private ManageClubAdapter adapter;
    private List<ClubData> mClubData;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType STRING
            = MediaType.get("text/plain; charset=utf-8");
    private static final int GET = 1;
    private static final int ADD_CLUB_POST = 2;
    private static final int DELETE_CLUB_POST = 3;
    private static final int CHANGE_PRESIDENT_POST = 4;
    private static final int LOGOUT_POST = 5;
    private static final int GETFAIL = 6;
    private int retry_time = 0;

    /**
     * Okhttp的get请求
     * @param url 向服务器请求的url
     * @return 服务器返回的字符串
     * @throws IOException 请求出错
     */
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = HttpServer.client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Okhttp的post请求
     * @param url 向服务器请求的url
     * @param json 向服务器发送的json包
     * @return 服务器返回的字符串
     * @throws IOException 请求出错
     */
    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = HttpServer.client.newCall(request).execute()) {
            return response.body().string();
        }
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
                    Message msg = Message.obtain();
                    msg.what = GETFAIL;
                    msg.obj = "clubdata";
                    getHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    private void addClubPost(String url, String clubName, String adminName) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String json = "{\"clubName\":\"" + clubName + "\",\"president\":\"" + adminName +"\"}";
                    String result = post(url, json); //jason用于上传数据，目前不需要
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = ADD_CLUB_POST;
                    msg.obj = "{\"clubName\":\"" + clubName + "\",\"president\":\"" + adminName +"\",\"data\":\"" + result + "\"}";
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                }
            }
        }.start();
    }

    private void deleteClubPost(String url, int position) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String clubName = adapter.getClubOnPosition(position).getClubName();
                    String json = "{\"clubName\":\"" + clubName + "\"}";
                    String result = post(url, json); //jason用于上传数据，目前不需要
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = DELETE_CLUB_POST;
                    msg.obj = "{\"position\":\"" + position +"\",\"data\":\"" + result + "\"}";
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                }
            }
        }.start();
    }

    private void changePresidentPost(String url, int position, String newAdminName) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String clubName = adapter.getClubOnPosition(position).getClubName();
                    String json = "{\"clubName\":\"" + clubName + "\",\"president\":\"" + newAdminName +"\"}";
                    String result = post(url, json);
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = CHANGE_PRESIDENT_POST;
                    msg.obj = "{\"position\":\"" + position + "\",\"president\":\"" + newAdminName +"\",\"data\":\"" + result + "\"}";
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                }
            }
        }.start();
    }

    private void logoutPost(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = post(url, "");
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = LOGOUT_POST;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                }
            }
        }.start();
    }

    /**
     * 处理get请求与post请求的回调函数
     */
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            Log.e("TAG", (String)msg.obj);
            switch (msg.what){
                case GET:
                    try {
                        parseJsonPacket((String)msg.obj, GET);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case ADD_CLUB_POST:
                    try {
                        parseJsonPacket((String)msg.obj, ADD_CLUB_POST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case DELETE_CLUB_POST:
                    try {
                        parseJsonPacket((String)msg.obj, DELETE_CLUB_POST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case CHANGE_PRESIDENT_POST:
                    //adapter.addData(1, (String)msg.obj, "");
                    try {
                        parseJsonPacket((String)msg.obj, CHANGE_PRESIDENT_POST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case LOGOUT_POST:
                    try {
                        Log.e("LOGOUT2", msg.obj.toString());
                        parseJsonPacket((String)msg.obj, LOGOUT_POST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case GETFAIL:
                    if (retry_time < 5) { //总共尝试5次，如果不行就放弃
                        retry_time++;
                        if (((String)msg.obj).equals("clubdata")) //判断请求失败的社团信息还是头像
                            getDataFromGet(HttpServer.CURRENTURL+"systemAdmin/homepage");
                    }
                    else {
                        initEmptyView(); //如果5次全部失败，就显示加载失败页面
                        retry_time = 0;
                    }
                default:
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
    private void parseJsonPacket(String json, int request_type) throws  JSONException {
        switch (request_type) {
            case GET:
                JSONObject jsonObject = new JSONObject(json);
                // adapter.addData(1, jsonObject.getString("clubSummary"), "");
                JSONArray jsonArray = jsonObject.getJSONArray("clubSummary");
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObjectClubSummary = jsonArray.getJSONObject(i);
                    adapter.addData(i, jsonObjectClubSummary.getString("clubName"),
                            jsonObjectClubSummary.getString("president"));
                }
                break;
            case ADD_CLUB_POST:
                JSONObject jsonObject1 = new JSONObject(json);
                String data1 = jsonObject1.getString("data");
                switch (data1) {
                    case "invalid username":
                        Toast.makeText(this, "该用户不存在", Toast.LENGTH_LONG).show();
                        break;
                    case "club name used":
                        Toast.makeText(this, "社团名已被使用", Toast.LENGTH_LONG).show();
                        Log.e("ADD_CLUB", "社团名已被使用");
                        break;
                    case "success":
                        adapter.addData(1, jsonObject1.getString("clubName"), jsonObject1.getString("president"));
                        Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            case DELETE_CLUB_POST:
                JSONObject jsonObject2 = new JSONObject(json);
                String data2 = jsonObject2.getString("data");
                switch (data2) {
                    case "invalid clubname":
                        Toast.makeText(this, "该社团不存在", Toast.LENGTH_LONG).show();
                        break;
                    case "success":
                        adapter.removeData(jsonObject2.getInt("position"));
                        Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            case CHANGE_PRESIDENT_POST:
                JSONObject jsonObject3 = new JSONObject(json);
                String data3 = jsonObject3.getString("data");
                switch (data3) {
                    case "invalid username":
                        Toast.makeText(this, "该用户不存在", Toast.LENGTH_LONG).show();
                        break;
                    case "invalid clubname":
                        Toast.makeText(this, "该社团不存在", Toast.LENGTH_LONG).show();
                        break;
                    case "success":
                        adapter.changeAdmin(jsonObject3.getInt("position"), jsonObject3.getString("president"));
                        Toast.makeText(this, "修改成功", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            case LOGOUT_POST:
                // JSONObject jsonObject4 = new JSONObject(json);
                String data4 = json.toString();
                Log.e("LOGOUT1", data4);
                switch (data4) {
                    case "invalid operation":
                        // Toast.makeText(this, "你不具有管理员权限", Toast.LENGTH_LONG).show();
                        // break;
                    case "success":
                        Intent intent2 = new Intent(this, LoginActivity.class);
                        startActivity(intent2);
                        this.finish();
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
    }

    public void initToolBar() {
        mNavigation.setTitle("社团管理");
        setSupportActionBar(mNavigation);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_homepage);
        mNavigation = findViewById(R.id.manager_homepage);
        initToolBar();

        mClubData = new ArrayList<>();
        initRecyclerLinear();
        Toast.makeText(this,
                "长按社团进行删除",
                Toast.LENGTH_LONG
        ).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manager_homepage_menu, menu);
        return true;
    }

    // menu选项
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                EditClubDialogFragment editClubDialog = new EditClubDialogFragment();
                editClubDialog.show(getFragmentManager(), "EditClubDialog");
                // adapter.addData(1);
                break;
            case R.id.logout:
                logoutPost(HttpServer.CURRENTURL+"systemAdmin/logout");
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void createClubComplete(String clubName, String adminName) {
        if (clubName.length() == 0) {
            Toast.makeText(this, "社团名不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        else if (clubName.length() > 20) {
            Toast.makeText(this, "社团名过长", Toast.LENGTH_LONG).show();
            return;
        }
        addClubPost(HttpServer.CURRENTURL+"systemAdmin/homepage/addClub", clubName, adminName);
        //adapter.addData(1, clubName, adminName);
    }

    @Override
    public void changeAdminComplete(String newAdminName, int position) {
        changePresidentPost(HttpServer.CURRENTURL+"systemAdmin/homepage/changeClubPresident", position, newAdminName);
        // adapter.changeAdmin(position, newAdminName);
    }

    public void initEmptyView() {
        Snackbar.make(rv_manage_club, "数据加载失败,请重新加载或者检查网络是否链接", Snackbar.LENGTH_SHORT).show();
    }

    public void initRecyclerLinear() {
        rv_manage_club = findViewById(R.id.rv_manage_club);
        manager = new LinearLayoutManager(this);
        rv_manage_club.setLayoutManager(manager);

        adapter = new ManageClubAdapter(this, mClubData);
        rv_manage_club.setAdapter(adapter);
        rv_manage_club.setItemAnimator(new DefaultItemAnimator());
        getDataFromGet(HttpServer.CURRENTURL+"systemAdmin/homepage");

        adapter.setOnItemClickListener(new ManageClubAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ManageClubAdapter.ViewName viewName, int position) {
                switch (viewName) {
                    case PRACTISE:
                        EditAdminDialogFragment editAdminDialog = new EditAdminDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        editAdminDialog.setArguments(bundle);
                        editAdminDialog.show(getFragmentManager(), "EditClubDialog");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                AlertDialog.Builder delete_confirm = new AlertDialog.Builder(ManagerHomePage.this);
                delete_confirm.setMessage("确认删除该社团吗？");
                delete_confirm.setTitle("提示");
                delete_confirm.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
                delete_confirm.setPositiveButton("确定", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteClubPost(HttpServer.CURRENTURL+"systemAdmin/homepage/deleteClub", position);
                    }
                });

                delete_confirm.show();
            }
        });

    }
}
