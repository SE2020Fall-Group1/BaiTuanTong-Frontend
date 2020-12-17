package com.example.BaiTuanTong_Frontend.data;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.BaiTuanTong_Frontend.PostContentActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.utils.ScreenUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.view.Gravity.*;


public class CommentDialogFragment extends DialogFragment {

    private Button sendButton;
    private EditText commentEditText;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient okHttpClient = new OkHttpClient();

    private String baseUrl = "http://47.92.233.174:5000/";
    private String viewUrl = baseUrl+"post/view/comment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_dialog, container,false);
        sendButton = (Button)view.findViewById(R.id.send_button);
        commentEditText = (EditText)view.findViewById(R.id.input_comment_edit_text);
        sendButton.setOnClickListener(new sendOnClickListener());
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
    /**
     * 修改布局的大小
     */
    @Override
    public void onStart() {
        super.onStart();
        resizeDialogFragment();

    }

    private void resizeDialogFragment() {
        Dialog dialog = getDialog();
        if (null != dialog) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
            lp.height = (1 * ScreenUtil.getScreenHeight(getContext()) / 5);//获取屏幕的宽度，定义自己的宽度
            lp.width = (9 * ScreenUtil.getScreenWidth(getContext()) / 10);
            if (window != null) {
                window.setLayout(lp.width, lp.height);
            }
        }
    }

    public class sendOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String comment = commentEditText.getText().toString();
            if(comment.equals("")) {
                Log.e("okclick","ok");
                Toast.makeText(getContext(), "输入评论不能为空", Toast.LENGTH_SHORT).show();
            }
            else{
                Log.e("found in click","hey");
                PostContentActivity postContentActivity = (PostContentActivity)getActivity();

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("userId", postContentActivity.userId);
                    jsonObject.put("postId", postContentActivity.postId);
                    jsonObject.put("commentText", comment);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("post","result");
                getDataFromPost(viewUrl, jsonObject.toString());
                postContentActivity.getDataFromGet(postContentActivity.getUrl, 0);
                CommentDialogFragment.this.dismiss();
            }
        }
    }
    private void getDataFromPost(String url, String json) {
        //Log.e("TAG", "Start getDataFromGet()");
        new Thread(){
            @Override
            public void run() {
                super.run();
                //Log.e("TAG", "new thread run.");
                try {
                    String result = post(url, json); //jason用于上传数据，目前不需要
                    Log.e("result", result);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
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
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
