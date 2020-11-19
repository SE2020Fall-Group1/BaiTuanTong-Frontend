package com.example.BaiTuanTong_Frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String UserName = intent.getStringExtra(MainActivity.USER_EXTRA_MESSAGE);
        String Password = intent.getStringExtra(MainActivity.PASS_EXTRA_MESSAGE);


        TextView textView = findViewById(R.id.textView);
        if(UserName.equals("zhp")&&Password.equals("123")){
            textView.setText("登录成功！");
        }
        else{
            textView.setText("用户名或密码错误！请重试");
        }
    }

}