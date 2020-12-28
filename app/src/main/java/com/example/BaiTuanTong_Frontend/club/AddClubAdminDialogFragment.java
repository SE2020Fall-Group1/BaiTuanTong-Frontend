package com.example.BaiTuanTong_Frontend.club;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.BaiTuanTong_Frontend.GridView.EditPostGridActivity;
import com.example.BaiTuanTong_Frontend.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddClubAdminDialogFragment extends DialogFragment {

    private Button add_admin_button;
    private EditText newAdminName;


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

    private int post_code;
    private String post_data;
    private int clubID;
    private String newAdmin;

    //在线测试 处理get和post
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            Log.e("TAG", (String)msg.obj);
            switch (msg.what){
                /*unused
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
                    */
                case POST://posting result
                    Log.e("POST_RES", (String) msg.obj);
                    Toast.makeText(getActivity(),
                            (String) msg.obj,
                            Toast.LENGTH_SHORT).show();
                    //300:fail, 200:success
                    //club_profile.setText((String)msg.obj);
                    try {
                        parseJsonPacket((String)msg.obj);
                 /*        if (code == 200){
                            Toast.makeText(getApplicationContext(),
                                    "success",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if (code == 300){
                            Toast.makeText(getApplicationContext(),
                                    data,
                                    Toast.LENGTH_SHORT).show();
                        }*/
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
//        code = jsonObject.getInt("code");
        //       data = jsonObject.getString("data");
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
/*                Toast.makeText(getApplicationContext(),
                        "删除完成！",
                        Toast.LENGTH_SHORT).show();

 */
                Log.e("post_code", ""+post_code);
/*                if(post_code == 200) {
                    Intent intent = new Intent();
                   intent.putExtra("adminName", newAdminName.getText().toString());
                    ((EditClubAdminActivity)getActivity()).onActivityResult(
                            EditClubAdminActivity.REQUEST_CODE_SUBMIT, Activity.RESULT_OK, intent);
                    ((EditClubAdminActivity)getActivity()).refresh();
                    getDialog().dismiss();
                }*/

            }
        }.start();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_add_club_admin,null);


        clubID = getActivity().getIntent().getIntExtra("clubId", -1);


        add_admin_button = (Button)view.findViewById(R.id.add_button);
        add_admin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    Toast.makeText(view.getContext(), "点击了确认按钮", Toast.LENGTH_SHORT).show();
                //这里传递文本框的数据。
                newAdminName = (EditText)view.findViewById(R.id.editText1);
                newAdmin = newAdminName.getText().toString();
                if (newAdmin.length() == 0)
                {
                    Toast.makeText(view.getContext(),
                            "添加的管理员名不能为空！",
                            Toast.LENGTH_SHORT).show();
                    return ;
                }
                JSONObject obj = new JSONObject();
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
/*                Toast.makeText(view.getContext(),
                        "添加请求已发送！",
                        Toast.LENGTH_SHORT).show();
 */
                while (post_code == 0)
                {
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (post_code == 200)
                {
                    Intent intent = new Intent();
                    intent.putExtra("adminName", newAdminName.getText().toString());
                    ((EditClubAdminActivity) getActivity()).onActivityResult(
                            EditClubAdminActivity.REQUEST_CODE_SUBMIT, Activity.RESULT_OK, intent);
                    ((EditClubAdminActivity) getActivity()).refresh();
                    getDialog().dismiss();
                }
                post_code = 0;
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}