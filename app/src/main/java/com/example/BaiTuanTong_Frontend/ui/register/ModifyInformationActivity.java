package com.example.BaiTuanTong_Frontend.ui.register;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.BaiTuanTong_Frontend.PostContentActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.adapter.CommentAdapter;
import com.example.BaiTuanTong_Frontend.data.Comment;
import com.example.BaiTuanTong_Frontend.data.ListViewUnderScroll;
import com.example.BaiTuanTong_Frontend.utils.MD5Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyInformationActivity extends AppCompatActivity {

    private RegistViewModel modifyInfViewModel;
    private String userId;
    private String originPassword;
    private String newPassword;
    private EditText originPasswordEditText;
    private EditText newPasswordEditText;
    private EditText passwordConfirmText;
    private Button modifyInfButton;

    private String baseUrl = "http://47.92.233.174:5000/";
    private String modifyUrl = baseUrl + "user/password";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            //super.handleMessage(msg);
            Log.e("what","handler");
            String result = (String)msg.obj;
            Toast.makeText(ModifyInformationActivity.this, result,Toast.LENGTH_SHORT).show();
            if(result.equals("success"))
                ModifyInformationActivity.this.finish();
            return true;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_information);

        modifyInfViewModel = new ViewModelProvider(this, new RegistViewModelFactory())
                .get(RegistViewModel.class);

        originPasswordEditText = findViewById(R.id.modifyinfOriginalPassword);
        newPasswordEditText = findViewById(R.id.modifyInfPassword);
        passwordConfirmText = findViewById(R.id.modifyInfConfirmPassword);
        modifyInfButton = findViewById(R.id.modifyInfButton);


        modifyInfViewModel.getRegistFormState().observe(this, new Observer<RegistFormState>() {
            @Override
            public void onChanged(@Nullable RegistFormState modifyInfFormState) {
                if (modifyInfFormState == null) {
                    return;
                }
                modifyInfButton.setEnabled(modifyInfFormState.isDataValid());
                if (modifyInfFormState.getPasswordError() != null) {
                    newPasswordEditText.setError(getString(modifyInfFormState.getPasswordError()));
                }
                if (modifyInfFormState.getPasswordMisMatch() != null) {
                    passwordConfirmText.setError(getString(modifyInfFormState.getPasswordMisMatch()));
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                modifyInfViewModel.registDataChanged("A",
                        "1234567891@pku.edu.cn", newPasswordEditText.getText().toString(), passwordConfirmText.getText().toString());
            }
        };
        newPasswordEditText.addTextChangedListener(afterTextChangedListener);
        passwordConfirmText.addTextChangedListener(afterTextChangedListener);

        modifyInfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences shared = getSharedPreferences("share",  MODE_PRIVATE);
                userId = shared.getString("userId", "");
                originPassword = MD5Util.encryp(originPasswordEditText.getText().toString());
                newPassword = MD5Util.encryp(newPasswordEditText.getText().toString());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userId", Integer.parseInt(userId));
                    jsonObject.put("password", originPassword);
                    jsonObject.put("new_password", newPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getDataFromPost(modifyUrl, jsonObject.toString(), 0);
                return;
            }
        });
    }
    private void getDataFromPost(String url, String json, int what) {
        //Log.e("TAG", "Start getDataFromGet()");
        new Thread(){
            @Override
            public void run() {
                super.run();
                //Log.e("TAG", "new thread run.");
                try {
                    String result = post(url, json);
                    Log.e("result", result);
                    Message msg = Message.obtain();
                    msg.obj = result;
                    msg.what = what;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                }
            }
        }.start();
    }
    /**
     * Okhttp的post请求
     * @param url
     * @param json
     * @return 服务器返回的字符串
     * @throws IOException
     */
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