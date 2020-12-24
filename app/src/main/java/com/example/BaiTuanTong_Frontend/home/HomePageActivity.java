package com.example.BaiTuanTong_Frontend.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.BaiTuanTong_Frontend.home.ui.home.HomeFragment;
import com.example.BaiTuanTong_Frontend.search_result.SearchResultActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.BaiTuanTong_Frontend.R;

import com.example.BaiTuanTong_Frontend.home.ui.home.HomeFragment.MyListener;

public class HomePageActivity extends AppCompatActivity implements MyListener{

    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_personal)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public static final String PAGE_EXTRA_MESSAGE = "com.example.BaiTuanTong_Frontend.pageMESSAGE";

    public void sendContent(String info){
        Intent intent = new Intent(this, SearchResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("searchText", info);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_home);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
        return true;
    }
}