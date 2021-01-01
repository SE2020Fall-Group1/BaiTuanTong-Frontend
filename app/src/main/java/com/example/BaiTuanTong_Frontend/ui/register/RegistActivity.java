package com.example.BaiTuanTong_Frontend.ui.register;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.home.HomePageActivity;
import com.example.BaiTuanTong_Frontend.ui.login.LoginActivity;
import com.example.BaiTuanTong_Frontend.ui.register.RegistViewModel;
import com.example.BaiTuanTong_Frontend.ui.register.RegistViewModelFactory;
import com.example.BaiTuanTong_Frontend.ui.register.RegistViewModel;
import com.example.BaiTuanTong_Frontend.utils.MD5Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private RegistViewModel registViewModel;
    private OkHttpClient client = new OkHttpClient();
    private  static final int GET = 1;
    private  static final int POST = 2;
    private static final String SERVERURL = "http://47.92.233.174:5000/";
    private static final String LOCALURL = "http://10.0.2.2:5000/";
    private String username;
    private String password;
    private String email;
    private String captcha;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        registViewModel = new ViewModelProvider(this, new RegistViewModelFactory())
                .get(RegistViewModel.class);

        final EditText usernameEditText = findViewById(R.id.registUserName);
        final EditText passwordEditText = findViewById(R.id.registPassword);
        final EditText passwordConfirmText = findViewById(R.id.registConfirmPassword);
        final EditText emailEditText = findViewById(R.id.registEmail);
        final EditText captchaEditText = findViewById(R.id.captcha);
        final Button registButton = findViewById(R.id.registButton);
        final Button captchaButton = findViewById(R.id.getCatchaButton);

        registViewModel.getRegistFormState().observe(this, new Observer<RegistFormState>() {
            @Override
            public void onChanged(@Nullable RegistFormState registFormState) {
                if (registFormState == null) {
                    return;
                }
                registButton.setEnabled(registFormState.isDataValid());
                if (registFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(registFormState.getUsernameError()));
                }
                if (registFormState.getEmailError() != null) {
                    emailEditText.setError(getString(registFormState.getEmailError()));
                }
                if (registFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(registFormState.getPasswordError()));
                }
                if (registFormState.getPasswordMisMatch() != null) {
                    passwordConfirmText.setError(getString(registFormState.getPasswordMisMatch()));
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
                registViewModel.registDataChanged(usernameEditText.getText().toString(),
                        emailEditText.getText().toString(), passwordEditText.getText().toString(), passwordConfirmText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordConfirmText.addTextChangedListener(afterTextChangedListener);

        captchaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                getDataFromGet(SERVERURL+"user/captcha?email="+email);
            }
        });
        registButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject mJson =new JSONObject();
                username = usernameEditText.getText().toString();
                password = MD5Util.encryp(passwordEditText.getText().toString());
                email = emailEditText.getText().toString();
                captcha = captchaEditText.getText().toString();
                try {
                    mJson.put("username",username);
                    mJson.put("password",password);
                    mJson.put("email",email);
                    mJson.put("captcha",captcha);
                    getDataFromPost(SERVERURL+"/user/register", mJson.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }
        });
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
                    Toast.makeText(getBaseContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case POST:
                    Toast.makeText(getBaseContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
                    if (((String) msg.obj).contains("user established")) {
                        //String welcomeMessage = "欢迎！" + username;
                        Toast.makeText(RegistActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                        //startHomePage();
                        startLoginPage();
                    }
                    break;
            }
            return true;
        }
    });
    // 转到LoginPageActivity
    private void startLoginPage(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
    // 转到HomePageActivity
    private void startHomePage(){
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
        this.finish();
    }
    /**
     * 使用get获取数据
     */
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
     * 使用post获取数据
     */
    private void getDataFromPost(String url, String json) {
        //Log.e("TAG", "Start getDataFromGet()");
        new Thread(){
            @Override
            public void run() {
                super.run();
                //Log.e("TAG", "new thread run.");
                try {
                    String result = post(url, json);
                    Log.e("TAG", result);
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
    /**
     * Okhttp的get请求
     * @param url
     * @return 服务器返回的字符串
     * @throws IOException
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
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}