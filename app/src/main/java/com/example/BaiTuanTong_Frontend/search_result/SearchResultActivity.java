package com.example.BaiTuanTong_Frontend.search_result;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.SearchActivity;
import com.example.BaiTuanTong_Frontend.search_result.ui.club_search_result.ClubSearchResultFragment;
import com.example.BaiTuanTong_Frontend.search_result.ui.post_search_result.PostSearchResultFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private ViewPager2 mViewPager2;
    private TabLayout mTabLayout;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        searchText = bundle.getString("searchText");

        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(ClubSearchResultFragment.newInstance(searchText, 0));
        mFragments.add(PostSearchResultFragment.newInstance(searchText, 1));
        SearchResultTabAdapter mSearchResultTabAdapter = new SearchResultTabAdapter(this, mFragments);

        mViewPager2 = findViewById(R.id.vp_tb);
        mViewPager2.setAdapter(mSearchResultTabAdapter);

        mTabLayout = findViewById(R.id.tb_vp);

        List<String> titles = new ArrayList<>();
        titles.add("社团");
        titles.add("动态");

        /*mTabLayout.addTab(mTabLayout.newTab().setText("社团"));
        mTabLayout.addTab(mTabLayout.newTab().setText("动态"));*/

        //TabLayout和Viewpager2进行关联
        new TabLayoutMediator(mTabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles.get(position));
            }
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        initSearchView(menu);

        return true;
    }

    private void initSearchView(Menu menu){
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        // 设置搜索框默认自动缩小为图标
        searchView.setIconifiedByDefault(false);
        // 让键盘的回车键设置成搜索
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        // 设置是否显示搜索按钮
        searchView.setSubmitButtonEnabled(true);
        //设置搜索内容
        searchView.setQuery(searchText, false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 搜索关键词完成输入
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(SearchResultActivity.this, SearchResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("searchText", query);
                intent.putExtras(bundle);

                startActivity(intent);
                return false;
            }
            // 搜索关键词发生变化
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}