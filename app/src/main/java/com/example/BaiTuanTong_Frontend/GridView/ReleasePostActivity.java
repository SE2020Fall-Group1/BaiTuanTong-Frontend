/*
发布动态
目前尚未实现和图片有关的工程。

 */
package com.example.BaiTuanTong_Frontend.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import com.example.BaiTuanTong_Frontend.R;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReleasePostActivity extends AppCompatActivity {

    private GridView gridView;
    private Context mContext;
    private ArrayList<Uri> mPicList = new ArrayList<>();
    // 图片路径数组，路径可以直接用于传输给后端
    private ArrayList<String> picPathList = new ArrayList<>();

    private MyGridViewAdapter myGridViewAdapter;
    private Button myButton;
    private EditText myTitle;
    private EditText myText;
    private String post_title;
    private String post_text;
    // 最多发布图片数
    private final int MAX_IMG_NUM = 1;
    private int clubId;

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
    // 为了添加图片：
    private final int IMAGE_CODE = 0;
    private final String IMAGE_TYPE = "image/*";

    //在线测试 处理get和post
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            Log.e("msgTAG", (String)msg.obj);
            switch (msg.what){
                case GET://not used in this page

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
                case POST://posting result
                    Log.e("POST_RES", (String) msg.obj);
                    Toast.makeText(getBaseContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();

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
 /*     private void parseJsonPacket(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
      code = jsonObject.getInt("code");
        data = jsonObject.getString("data");
    }
*/

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
     * @return 服务器返回的字符串
     * @throws IOException 请求出错
     */
    private String post(String url) throws IOException {
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
                    .addFormDataPart("clubId", Integer.toString(clubId))
                    .addFormDataPart("title", post_title)
                    .addFormDataPart("text", post_text)
                    .addFormDataPart("image", file.getName(), fileBody)
                    .build();
        }
        else {
            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("clubId", Integer.toString(clubId))
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

    private void getDataFromPost(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = post(url); //json用于上传数据，目前不需要
                    Log.e("TAG", result);
                    Message msg = Message.obtain();
                    msg.what = POST;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                }
                if (code == 200)
                    ReleasePostActivity.this.finish();
            }
        }.start();
    }

    private void viewPluImg(int pos){
        Toast.makeText(this, "click a pic", Toast.LENGTH_SHORT).show();
    }
    private void selectPic(int maxTotal) {
        Toast.makeText(this, "want to update a pic", Toast.LENGTH_SHORT).show();
     //   PictureSelector.create(this, maxTotal);
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
        this.setTitle("发布动态");
        clubId = getIntent().getIntExtra("clubId", -1);
        if (clubId == -1)//error detected
        {
            Toast.makeText(getApplicationContext(),
                    "社团获取出现问题，请返回上级页面！",
                    Toast.LENGTH_LONG).show();
        }

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
                getDataFromPost(SERVERURL + "post/release");


//                Log.e("code", ""+code);
//                Log.e("data", data);
       /*         if (code == 200)
                    ReleasePostActivity.this.finish();
               else
                {

                    if (data == null)
                        data = "null";
                    Toast.makeText(getApplicationContext(),
                            data,
                            Toast.LENGTH_SHORT).show();
                }
*/
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