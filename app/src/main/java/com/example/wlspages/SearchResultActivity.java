package com.example.wlspages;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SearchResultActivity extends AppCompatActivity {

    private TextView tv_search_result;
    private Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        tv_search_result = findViewById(R.id.tv_search_result);
        myIntent = getIntent();
        doSearchQuery(myIntent);
    }

    private void doSearchQuery(Intent intent){
        if(intent != null){
            if (Intent.ACTION_SEARCH.equals(intent.getAction())){
                String queryString = intent.getStringExtra(SearchManager.QUERY);
                tv_search_result.setText("搜索的文字为"+queryString);
            }
        }
    }

}