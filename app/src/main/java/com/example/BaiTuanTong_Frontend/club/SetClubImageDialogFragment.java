package com.example.BaiTuanTong_Frontend.club;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.example.BaiTuanTong_Frontend.HttpServer;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.utils.ScreenUtil;
import com.example.BaiTuanTong_Frontend.widget.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;


public class SetClubImageDialogFragment extends DialogFragment {

    private Button confirmButton;
    private CircleImageView clubImage;
    private String clubId;

    private final String IMAGE_TYPE = "image/*";
    private final int IMAGE_CODE = 0;
    private String imgPath = null;
    private Bitmap temp_bitmap;

    private final OkHttpClient client = HttpServer.client;
    private static final int POST_IMG = 3;
    private static final int POST_FAIL = 4;
    private int retry_time = 0;
    private static final String SERVERURL = HttpServer.CURRENTURL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_clubimage_dialog, container,false);
        confirmButton = (Button)view.findViewById(R.id.confirm_button_club_image);
        clubImage = (CircleImageView) view.findViewById(R.id.dialog_club_picture);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        checkStoragePermissions(getActivity());
        ClubHomeActivity clubHomeActivity = (ClubHomeActivity) getActivity();
        clubId = "" + clubHomeActivity.clubId;
        if(clubHomeActivity.clubImageBitmap != null){
            clubImage.setImageBitmap(clubHomeActivity.clubImageBitmap);
        }
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    // 检查读写权限
    private static final int REQUEST_EXTERNAL_STORAGE=1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE};
    public static void checkStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                //没有读的权限，去申请读的权限，或弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImage() {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Log.e("TAG->onresult", "ActivityResult resultCode error");
            return;
        }

        ContentResolver resolver = getActivity().getContentResolver();
        if (requestCode == IMAGE_CODE) {
            try {
                Uri originalUri = data.getData();        //获得图片的uri

                temp_bitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                // 处理图像，获取路径
                handleImageOnKitKat(data);
                Log.e("img path", imgPath);
                // 发送给后端
                getDataFromPostImg(SERVERURL+"club/image/upload");

            } catch (IOException e) {
                Log.e("TAG-->Error", e.toString());
            }
        }
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection 来获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);
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
        if(DocumentsContract.isDocumentUri(getActivity(),uri)){
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
            if (msg.what == POST_IMG) {
                retry_time = 0;
                String result = (String)msg.obj;
                if(result.equals("success")) {
                    Toast.makeText(getActivity().getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                    clubImage.setImageBitmap(temp_bitmap);
                    ClubHomeActivity clubHomeActivity = (ClubHomeActivity) getActivity();
                    clubHomeActivity.setClubImage(temp_bitmap);
                    //SetClubImageDialogFragment.this.dismiss();
                }
                else{
                    Toast.makeText(getActivity().getBaseContext(), "修改失败，请重试", Toast.LENGTH_SHORT).show();
                }
            } else if(msg.what == POST_FAIL) {
                if(retry_time < 3) {
                    retry_time++;
                    getDataFromPostImg(SERVERURL + "club/image/upload");
                    return true;
                }
                else{
                    retry_time = 0;
                    return false;
                }
            }
            return true;
        }
    });

    // 使用postImg获取数据
    private void getDataFromPostImg(String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = postImg(url, clubId, imgPath); //jason用于上传数据，目前不需要
                    Log.e("Result", result);
                    Message msg = Message.obtain();
                    msg.what = POST_IMG;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("Exception: ", IOException.getMessage());
                    Message msg = Message.obtain();
                    msg.what = POST_FAIL;
                    getHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * Okhttp的post请求
     * @param url 向服务器请求的url
     * @param path 图像路径
     * @return 服务器返回的字符串
     * @throws IOException 请求出错
     */
    private String postImg(String url, String clubId, String path) throws IOException {
        // 创建发送头像请求
        File file = new File(path);
        if (file == null)
            Log.e("file create wrong", "sad");
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("clubId", clubId)
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
}