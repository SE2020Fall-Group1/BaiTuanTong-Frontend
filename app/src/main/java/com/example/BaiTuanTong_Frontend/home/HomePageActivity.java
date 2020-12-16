package com.example.BaiTuanTong_Frontend.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.BaiTuanTong_Frontend.search_result.SearchResultActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.BaiTuanTong_Frontend.R;

import com.example.BaiTuanTong_Frontend.home.ui.home.HomeFragment.MyListener;

public class HomePageActivity extends AppCompatActivity implements MyListener{

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
    /*@Override
    public void onStart(){
        super.onStart();
        Toast.makeText(this,"HomePage is onStart",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume(){
        super.onResume();
        Toast.makeText(this,"HomePage is onResume",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause(){
        super.onPause();
        Toast.makeText(this,"HomePage is onPause",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop(){
        super.onStop();
        Toast.makeText(this,"HomePage is onStop",Toast.LENGTH_SHORT).show();
    }*/
}