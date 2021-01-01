package com.example.BaiTuanTong_Frontend.club;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BaiTuanTong_Frontend.HttpServer;
import com.example.BaiTuanTong_Frontend.MyAdapter;
import com.example.BaiTuanTong_Frontend.R;

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


public class EditClubAdminActivity extends AppCompatActivity {


    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final MediaType STRING
            = MediaType.get("text/plain; charset=utf-8");
    private RecyclerView adminListView;
    private List<String> adminList;
    private List<Integer> adminIdList;
    private MyAdapter mMyAdapter;
    private boolean deleting;
    private Switch mySwitch;
    private AddClubAdminDialogFragment dialogFragment;
    private final OkHttpClient client = HttpServer.client;
    private static final int GET = 1;
    private static final int POST = 2;
    private static final String SERVERURL = HttpServer.CURRENTURL;//服务器用 port5000
    private static final String LOCALURL = "http://10.0.2.2:5000/";//本地测试用
    public static final int REQUEST_CODE_SUBMIT = 1;

//clubID
    private int clubID;

//    JSON内部信息
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
                            adminList.add(" " + tmp.getString("username"));
                            adminIdList.add(tmp.getInt("userId"));
                            mMyAdapter.notifyDataSetChanged();
                        //    mMyAdapter.notifyItemRangeChanged(adminList.size()-1, adminList.size());
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
                    Log.e("POST_RES", (String) msg.obj);
                    //club_profile.setText((String)msg.obj);
                    Toast.makeText(getApplicationContext(),
                            (String)msg.obj,
                            Toast.LENGTH_SHORT).show();
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
     /*     JSONObject jsonObject = new JSONObject(json);
      code = jsonObject.getInt("code");
        data = jsonObject.getString("data");
        Log.e("code", ""+ code);
        Log.e("data", data);*/

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
                    Log.e("ADD_TAG", result);
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

    //offline test here
    private List<String> getList()
    {
        List<String> ret = new ArrayList<>();
    /*    for (int i = 1; i <= 2; ++i)
        {
            ret.add("username：李子恒" + "\nid:"+ i + "" + "");
        }*/
        return ret;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)//添加右上角三个点儿
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_club_admin_menu, menu);
        //这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能

        return true;
    }

    public void refresh()
    {
        adminList.clear();
        adminIdList.clear();
        getDataFromGet(SERVERURL + "club/admin?clubId=" + Integer.toString(clubID));
        mMyAdapter.notifyDataSetChanged();
    }

    //从此处添加社团管理员。
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SUBMIT && resultCode == RESULT_OK)
        {
            String newAdmin = data.getStringExtra("adminName");
//            adminList.add("username：" + newAdmin);//离线方法，测试用

            //以下为okhttp方法。
        /*    JSONObject obj = new JSONObject();
            try {
                obj.put("clubId", clubID);
             //   obj.put("userId", 4);
                Log.e("newAdminName", newAdmin);
                obj.put("username", newAdmin);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("posting json", obj.toString());
            getDataFromPost(SERVERURL + "club/admin/add", obj.toString());
            */
        //    refresh();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete_post_button:
            /*    Toast.makeText(getApplicationContext(),
                        "点击了添加成员按钮",
                        Toast.LENGTH_SHORT).show();
                        */

                dialogFragment.show(getSupportFragmentManager(), "add_Admin");
                break;


            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_club_admin);

        this.setTitle("管理社团管理员");

        //这个部分测试完成了
        adminList = getList();
        adminIdList = new ArrayList<>();
        if (adminIdList == null){
            Log.e("nullfault", "null!");
        }
        clubID = getIntent().getIntExtra("clubId", -1);

        //test: -1
     //   clubID = -1;
        if (clubID == -1)
        {
            Toast.makeText(getApplicationContext(),
                    "社团获取出现问题，请返回上级页面！",
                    Toast.LENGTH_LONG).show();
        }
        Log.e("clubId",""+clubID);
        // clubID = 1;//for test server

        adminListView = findViewById(R.id.recyclerView2);
        // 设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(adminListView.VERTICAL);
        adminListView.setLayoutManager(linearLayoutManager);
        // 设置 item 增加和删除时的动画
        adminListView.setItemAnimator(new DefaultItemAnimator());

//        Log.e("TAG", adminList.get(0));
        mMyAdapter = new MyAdapter(this, adminList);

        adminListView.setAdapter(mMyAdapter);

        adminListView.invalidate();

        refresh();




//        getActionBar().setDisplayHomeAsUpEnabled(true);
        //设置返回键功能,这样点击左上角返回按钮时才能返回到同一个社团主页
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        deleting = false;


        mMyAdapter.setOnItemLongClickListener(new MyAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position) {
                //长按删除一个成员

                if (deleting == false)//不在删除模式下，不考虑长按的作用
                    return ;

                int del_admin = adminIdList.get(position);
            //离线模式做法，直接对列表做操作
            //   adminList.remove(position);
            //    mMyAdapter.notifyDataSetChanged();
                //以下为okhttp方法。
                JSONObject obj = new JSONObject();
                try {
                    obj.put("clubId", clubID);
                    obj.put("userId", del_admin);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*
                Toast.makeText(getApplicationContext(),
                        "删除完成！",
                        Toast.LENGTH_SHORT).show();

                 */
                getDataFromPost(SERVERURL + "club/admin/delete", obj.toString());
                refresh();

            }
        });

        //设定删除开关。
        mySwitch = (Switch)findViewById(R.id.switch1);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if(isChecked)
                {
                    Toast.makeText(getApplicationContext(),
                            "删除模式开启，长按成员列表项完成删除",
                            Toast.LENGTH_SHORT).show();
                //    Toast.makeText(this,"删除模式开启，长按成员项完成删除",Toast.LENGTH_SHORT).show();
                    deleting = true;
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "删除模式关闭",
                            Toast.LENGTH_SHORT).show();
                    deleting = false;
                }
            }
        });

        dialogFragment = new AddClubAdminDialogFragment();


    }
/*
    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.add_club_admin:
                    Toast.makeText(getApplicationContext(), "点击了确认按钮", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }
*/
    /*
        Button commentButton = (Button)findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new MyOnClickListener());

    }
    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.comment_button:
                    commentDialogFragment.show(getSupportFragmentManager(),"dialog");
                    break;

            }
        }
    }*/

 /*       addAdminButton = (Button)findViewById(R.id.add_button);
        addAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "提交！", Toast.LENGTH_SHORT).show();
            }
        });
*/

/*        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.edit_club_admin_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Toast.makeText(this, "my flaut", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        popupMenu.show();
*/

}