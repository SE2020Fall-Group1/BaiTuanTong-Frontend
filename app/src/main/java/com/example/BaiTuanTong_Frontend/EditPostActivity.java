package com.example.BaiTuanTong_Frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class EditPostActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu)//添加右上角三个点儿
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_post, menu);
        //这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Intent intent = getIntent();
        String message = intent.getStringExtra(PostListDisplayActivity.PAGE_EXTRA_MESSAGE);
        TextView textView = findViewById(R.id.textView3);
        textView.setText(message);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete_post:
                Toast.makeText(this,"delete is clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_post:
                Toast.makeText(this,"edit is clicked",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}