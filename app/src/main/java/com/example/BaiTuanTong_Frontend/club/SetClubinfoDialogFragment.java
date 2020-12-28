package com.example.BaiTuanTong_Frontend.club;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import androidx.fragment.app.DialogFragment;

import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.utils.ScreenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SetClubinfoDialogFragment extends DialogFragment {

    private Button confirmButton;
    private EditText clubInfoEditText;
    private String comment;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient okHttpClient = new OkHttpClient();

    private String baseUrl = "http://47.92.233.174:5000/";
    private String viewUrl = baseUrl+"club/homepage/changeIntroduction";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_clubinfo_dialog, container,false);
        confirmButton = (Button)view.findViewById(R.id.confirm_button);
        clubInfoEditText = (EditText)view.findViewById(R.id.club_info_edit_text);
        confirmButton.setOnClickListener(new sendOnClickListener());
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
        clubInfoEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        resizeDialogFragment();
        Window window = getDialog().getWindow();
        if (window != null) {
            //得到LayoutParams
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount=0f;
            //修改gravity
            params.gravity = Gravity.BOTTOM;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
        }
        ClubHomeActivity clubHomeActivity = (ClubHomeActivity) getActivity();
        clubInfoEditText.setText(clubHomeActivity.clubInfo);
    }
    @Override
    public void onResume() {
        super.onResume();
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager inManager = (InputMethodManager)clubInfoEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        },50);
    }

    private void resizeDialogFragment() {
        Dialog dialog = getDialog();
        if (null != dialog) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
            lp.height = (1 * ScreenUtil.getScreenHeight(getContext()) / 7);//获取屏幕的宽度，定义自己的宽度
            lp.width = (ScreenUtil.getScreenWidth(getContext()));
            if (window != null) {
                window.setLayout(lp.width, lp.height);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.getDecorView().setPadding(0,0,0,0);
            }
        }
    }

    protected WindowManager.LayoutParams getLayoutParams(WindowManager.LayoutParams params) {
        if (params == null) {
            return new WindowManager.LayoutParams();
        }
        //注意这里设置的宽高，需要设置成MATCH_PARENT,不然的话就不会起作用，就这一点坑。
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        return params;
    }

    public class sendOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            comment = clubInfoEditText.getText().toString();
            if(comment.equals("")) {
                Log.e("okclick","ok");
                Toast.makeText(getContext(), "社团简介不能为空", Toast.LENGTH_SHORT).show();
            }
            else{
                //clubInfoEditText.setText("");
                Log.e("found in click","hey");

                ClubHomeActivity clubHomeActivity = (ClubHomeActivity) getActivity();
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("clubId", clubHomeActivity.clubId);
                    jsonObject.put("newIntroduction", comment);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
                getDataFromPost(viewUrl, jsonObject.toString());
                //postContentActivity.getDataFromGet(postContentActivity.getUrl, 0);
                //SetClubinfoDialogFragment.this.dismiss();
            }
        }
    }
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Log.e("TAG", (String)msg.obj);
            switch (msg.what){
                case 0:
                    String result = (String)msg.obj;
                    if(result.equals("success")){
                        ClubHomeActivity clubHomeActivity = (ClubHomeActivity) getActivity();
                        clubHomeActivity.setClubProfile(comment);
                        SetClubinfoDialogFragment.this.dismiss();
                    }
                    return true;
            }
            Toast.makeText(getContext(), "社团简介修改失败，请重试", Toast.LENGTH_SHORT).show();
            return true;
        }
    });
    private void getDataFromPost(String url, String json) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = post(url, json); //jason用于上传数据，目前不需要
                    Log.e("result", result);
                    Message msg = Message.obtain();
                    msg.what = 0;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                    Message msg = Message.obtain();
                    msg.what = 1;
                    getHandler.sendMessage(msg);
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