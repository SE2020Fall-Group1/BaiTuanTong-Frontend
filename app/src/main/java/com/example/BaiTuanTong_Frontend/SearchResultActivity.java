package com.example.BaiTuanTong_Frontend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.BaiTuanTong_Frontend.ui.club_search_result.ClubSearchResultFragment;
import com.example.BaiTuanTong_Frontend.ui.post_search_result.PostSearchResultFragment;
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

        searchText = "zsh";

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
        new  TabLayoutMediator(mTabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles.get(position));
            }
        }).attach();
        /*mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mTabLayout.setScrollPosition(position, 0, false);
            }
        });*/
    }
}

/*
package com.example.BaiTuanTong_Frontend;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_club_search_result, R.id.navigation_post_search_result)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}*/
