package com.example.BaiTuanTong_Frontend.ui.login;
import com.example.BaiTuanTong_Frontend.HttpServer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.SystemAdministrator.ManagerHomePage;
import com.example.BaiTuanTong_Frontend.home.HomePageActivity;
import com.example.BaiTuanTong_Frontend.ui.register.RegistActivity;
import com.example.BaiTuanTong_Frontend.utils.MD5Util;
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

public class LoginActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private LoginViewModel loginViewModel;
    private int loginResult;
    private String username;
    private final int POST = 0;
    private final int POSTFAIL = 1;
    private int retry_time = 0;

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

    //处理异步线程发来的消息
    private Handler getHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            //Log.e("handler",(String)msg.obj);
            //super.handleMessage(msg);
            if(msg.what == POST) {
                retry_time = 0;
                try {
                    String msgBody = (String) msg.obj;
                    if (msgBody.equals("wrong password") || msgBody.equals("wrong username")) {
                        Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                    } else if (msgBody.equals("system administrator login")) {
                        startManagerHomePage();
                        String welcomeMessage = "欢迎！管理员";
                        Toast.makeText(LoginActivity.this, welcomeMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jsonObject = new JSONObject((String) msgBody);
                        String userId = jsonObject.getString("userId");
                        Log.e("Toast", userId);
                        //共享参数对象，用来全局共享userId
                        SharedPreferences shared = getSharedPreferences("share", MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        // 把userId(String)放到全局变量userId中
                        // 取出只需调用shared.getString
                        editor.putString("userId", userId);
                        editor.putString("userName", username);
                        editor.putBoolean("logged", true);
                        editor.commit();// 提交编辑

                        String welcomeMessage = "欢迎！" + username;
                        Toast.makeText(LoginActivity.this, welcomeMessage, Toast.LENGTH_SHORT).show();
                        startHomePage();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
            else if(msg.what == POSTFAIL){
                if(retry_time < 3) { //尝试三次，如果不行就放弃
                    retry_time++;
                    String json = (String)msg.obj;
                    getDataFromPost(HttpServer.CURRENTURL+"user/login", json);
                }
                else {
                    retry_time = 0;
                }
            }

            return true;
        }
    });
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences shared = getSharedPreferences("share", MODE_PRIVATE);
        if(shared.getBoolean("logged", false)){  //如果已经登录过，直接进入主页

            //startHomePage();
        }


        setContentView(R.layout.activity_login);
        Intent intentShift = new Intent(this, RegistActivity.class);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final Button shiftToRegButton = findViewById(R.id.shift_to_register);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        //输入时，提示格式信息
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        //告知loginViewModel，输入信息有改变
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
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        //好像是如果输入密码后按回车直接登录，暂时忽略
        /*
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });*/

        //点击登录按钮则发送请求
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(LoginActivity.this.getWindow().getDecorView().getWindowToken(), 0);
                //String msg = loginViewModel.login(usernameEditText.getText().toString(),
                //         passwordEditText.getText().toString());
                OkHttpClient okHttpClient = HttpServer.client;

                username = usernameEditText.getText().toString();
                String password = MD5Util.encryp(passwordEditText.getText().toString());
                Log.e("encryp",password);
                HashMap<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("password", password);
                Gson gson = new Gson();
                String data = gson.toJson(map);
                getDataFromPost(HttpServer.CURRENTURL+"user/login", data);
            }
        });

        //点击注册按钮则切换页面
        shiftToRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentShift);
            }
        });
    }
    // 转到HomePageActivity
    private void startHomePage(){
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
        this.finish();
    }

    //转到ManageHomePage
    private void startManagerHomePage(){
        Intent intent = new Intent(this, ManagerHomePage.class);
        startActivity(intent);
        this.finish();
    }

    //Toast提示登录结果信息，暂时忽略
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    //okhttp方法集合

    //从post获取数据
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
                    Message msg = Message.obtain();
                    msg.what = POST;
                    msg.obj = result;
                    getHandler.sendMessage(msg);
                } catch (java.io.IOException IOException) {
                    Log.e("TAG", "post failed.");
                    Message msg = Message.obtain();
                    msg.what = POSTFAIL;
                    msg.obj = json;
                    getHandler.sendMessage(msg);
                }
            }
        }.start();
    }
}