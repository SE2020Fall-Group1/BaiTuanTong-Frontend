package com.example.BaiTuanTong_Frontend.AdministratorAll;

import com.example.BaiTuanTong_Frontend.AdministratorAll.EditClubDialogFragment.CreateClubListener;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.AdministratorAll.EditAdminDialogFragment.ChangeAdminListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.List;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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
    private final OkHttpClient client = new OkHttpClient();
    private static final int GET = 1;
    private static final int ADD_CLUB_POST = 2;
    private static final int DELETE_CLUB_POST = 3;
    private static final int CHANGE_PRESIDENT_POST = 4;
    private static final String SERVERURL = "http://47.92.233.174:5000/";
    private static final String LOCALURL = "http://10.0.2.2:5000/";
    private static final String TESTURL = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";

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
                    String clubName = adapter.mClubData.get(position).getClubName();
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
                    String clubName = adapter.mClubData.get(position).getClubName();
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
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    adapter.addData(i, jsonObject2.getString("clubName"), jsonObject2.getString("president_name"));
                }
                break;
            case ADD_CLUB_POST:
                JSONObject jsonObject1 = new JSONObject(json);
                String data1 = jsonObject1.getString("data");
                // Toast.makeText(this, "add club " + jsonObject1.toString(), Toast.LENGTH_SHORT).show();
                switch (data1) {
                    case "president do not exist":
                        Log.e("AddClub", data1);
                        Toast.makeText(this, data1, Toast.LENGTH_LONG).show();
                        break;
                    case "club name exist":
                        Log.e("AddClub", data1);
                        Toast.makeText(this, data1, Toast.LENGTH_LONG).show();
                        break;
                    case "success":
                        adapter.addData(1, jsonObject1.getString("clubName"), jsonObject1.getString("president"));
                        break;
                    default:
                        break;
                }
            case DELETE_CLUB_POST:
                JSONObject jsonObject4 = new JSONObject(json);
                String data4 = jsonObject4.getString("data");
                switch (data4) {
                    case "club do not exist":
                        Log.e("DeleteClub", data4);
                        break;
                    case "success":
                        adapter.removeData(jsonObject4.getInt("position"));
                        break;
                    default:
                        break;
                }
            case CHANGE_PRESIDENT_POST:
                JSONObject jsonObject3 = new JSONObject(json);
                String data3 = jsonObject3.getString("data");
                switch (data3) {
                    case "new president do not exist":
                        Log.e("ChangePresident", data3);
                        break;
                    case "club do not exist":
                        Log.e("ChangePresident", data3);
                        break;
                    case "success":
                        adapter.changeAdmin(jsonObject3.getInt("position"), jsonObject3.getString("president"));
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
    }
    /*private void parseJsonPacket(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        clubInfo = jsonObject.getString("introduction");
        clubPresident = jsonObject.getString("president");
        JSONArray jsonArray = jsonObject.getJSONArray("club_post_list");
        for(int i = 0; i < jsonArray.length(); i++){
            postList.add(jsonArray.getString(i));
        }
    }*/



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
    }

    // List初始化，测试用
    /*
    private void getClubData(){
        for (int i = 0; i < 20; i++)
            mClubData.add(new ClubData("Club " + i + "", "Admin " + i + ""));
    }
     */

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
        }
        return true;
    }

    @Override
    public void createClubComplete(String clubName, String adminName) {
        Toast.makeText(this, clubName+adminName, Toast.LENGTH_LONG).show();
        addClubPost(LOCALURL+"systemAdmin/homepage/addClub", clubName, adminName);
        //adapter.addData(1, clubName, adminName);
    }

    @Override
    public void changeAdminComplete(String newAdminName, int position) {
        changePresidentPost(LOCALURL+"systemAdmin/homepage/changeClubPresident", position, newAdminName);
        // adapter.changeAdmin(position, newAdminName);
    }

    public void initRecyclerLinear() {
        rv_manage_club = findViewById(R.id.rv_manage_club);
        manager = new LinearLayoutManager(this);
        rv_manage_club.setLayoutManager(manager);

        // getClubData();
        adapter = new ManageClubAdapter(this, mClubData);
        rv_manage_club.setAdapter(adapter);
        rv_manage_club.setItemAnimator(new DefaultItemAnimator());
        getDataFromGet(LOCALURL+"systemAdmin/homepage");

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
                        // Toast.makeText(ManagerHomePage.this, position + " button click", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(ManagerHomePage.this, position + " click", Toast.LENGTH_SHORT).show();
                        Log.e("Click", position + " click");
                        break;
                }

                //Intent intent = new Intent(ManagerHomePage.this, ManageAdministrator.class);
                //intent.putExtra("clubName", mList.get(position));
                //startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

                // Toast.makeText(ManagerHomePage.this, position + " Long click", Toast.LENGTH_SHORT).show();
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
                        deleteClubPost(LOCALURL+"systemAdmin/homepage/deleteClub", position);
                        //adapter.removeData(position);
                    }
                });

                delete_confirm.show();
            }
        });

    }
}
