package com.example.BaiTuanTong_Frontend;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.BaiTuanTong_Frontend.ui.login.LoginActivity;


public class MainActivity extends AppCompatActivity {
    public static final String USER_EXTRA_MESSAGE = "com.example.BaiTuanTong_Frontend.UserMESSAGE";
    public static final String PASS_EXTRA_MESSAGE = "com.example.BaiTuanTong_Frontend.PassMESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
    }

}