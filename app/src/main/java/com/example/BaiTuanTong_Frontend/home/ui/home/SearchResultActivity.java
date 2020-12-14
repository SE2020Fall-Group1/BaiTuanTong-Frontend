package com.example.BaiTuanTong_Frontend.home.ui.home;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.BaiTuanTong_Frontend.PostListDisplayActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.home.HomePageActivity;

public class SearchResultActivity extends AppCompatActivity {

    private TextView tv_search_result;
    private Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("sr:","saasdsadsadsa");
        setContentView(R.layout.activity_search_result);
        tv_search_result = findViewById(R.id.tv_search_result);
        myIntent = getIntent();
        doSearchQuery(myIntent);
    }

    private void doSearchQuery(Intent intent){
        if(intent != null){
            String message = intent.getStringExtra(HomePageActivity.PAGE_EXTRA_MESSAGE);
            tv_search_result.setText(message);
        }
    }

}