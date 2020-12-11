package com.example.wlspages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
//import androidx.appcompat.widget.SearchView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initRecyclerLinear();
       // findViewById(R.id.usr_img).setOnClickListener(this);

    }

    //private AutoCompleteTextView sac_key;
    //private String[]hintArray = {"北京大学爱心社", "北京大学山鹰社"};

    private void initSearchView(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView)menuItem.getActionView();
        // 设置搜索框默认自动缩小为图标
        searchView.setIconifiedByDefault(getIntent().getBooleanExtra("collapse",true));
        // 设置是否显示搜索按钮
        searchView.setSubmitButtonEnabled(true);
        SearchManager sm = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        ComponentName cn = new ComponentName(this, SearchResultActivity.class);
        SearchableInfo info = sm.getSearchableInfo(cn);
        searchView.setSearchableInfo(info);/*
        sac_key = searchView.findViewById(R.id.search_src_text);
        sac_key.setTextColor(Color.WHITE);
        sac_key.setHintTextColor(Color.WHITE);*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 搜索关键词完成输入
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            // 搜索关键词发生变化
            @Override
            public boolean onQueryTextChange(String newText) {
                // 提示框，未实现
                doSearch(newText);
                return false;
            }
        });
    }
    // 前端调试用，实现一个简易的list
    private List<String> mList;
    // List初始化，随意实现的
    private List<String> getList(){
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add("首页测试动态专用\n"+"这是一条无聊的动态"+"");
        }
        return list;
    }
    // 初始化线性布局的循环视图
    private void initRecyclerLinear() {
        RecyclerView rv_post_list = findViewById(R.id.rv_post_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv_post_list.setLayoutManager(manager);
        mList = getList();
        MyAdapter adapter = new MyAdapter(this, mList);
        rv_post_list.setAdapter(adapter);
        // 下面是为点击事件添加的代码
        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onInternalViewClick(View view, MyAdapter.ViewName viewName, int position) {
                if (viewName == MyAdapter.ViewName.USR_IMG) {
                    Toast.makeText(getBaseContext(), "拍了拍头像", Toast.LENGTH_SHORT).show();
                }
                else if(viewName == MyAdapter.ViewName.POST_TEXT) {
                    Toast.makeText(getBaseContext(), "点击了文本", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getBaseContext(), mList.get(position), Toast.LENGTH_SHORT).show();
                //sendMessage(position);
            }
        });
        rv_post_list.setItemAnimator(new DefaultItemAnimator());
    }
    // 提示框，未实现
    private void doSearch(String text) {
       /* if(text.indexOf("北")==0){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.search_list_auto,hintArray);
            sac_key.setAdapter(adapter);
            sac_key.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    sac_key.setText(((TextView)view).getText());
                }
            });
        }*/
    }

    // 创建menu时调用，实现搜索框
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search,menu);
        initSearchView(menu);
        return true;
    }
}