package com.example.BaiTuanTong_Frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.BaiTuanTong_Frontend.AdministratorAll.ManagerHomePage;
import com.example.BaiTuanTong_Frontend.home.HomePageActivity;
// import com.example.BaiTuanTong_Frontend.AdministratorAll.ManagerHomePage;

public class MainActivity extends AppCompatActivity {
    public static final String USER_EXTRA_MESSAGE = "com.example.BaiTuanTong_Frontend.UserMESSAGE";
    public static final String PASS_EXTRA_MESSAGE = "com.example.BaiTuanTong_Frontend.PassMESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view){
        Intent intent1 = new Intent(this, FollowedClubsDisplayActivity.class);
        //Intent intent1 = new Intent(this, PostListDisplayActivity.class);
        Intent intent2 = new Intent(this, DisplayMessageActivity.class);
        EditText UserNameEditText = (EditText) findViewById(R.id.UserNameText);
        String UserName = UserNameEditText.getText().toString();
        EditText PasswordEditText = (EditText) findViewById(R.id.PasswordText);
        String Password = PasswordEditText.getText().toString();
        /*if(UserName.equals("zhp")&&Password.equals("123")){
            startActivity(intent1);
        }
        else
            startActivity(intent2);*/

        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }

    public void managerLogin(View view){

        // Intent intent = new Intent(this, HomePageActivity.class);
        Intent intent = new Intent(this, ManagerHomePage.class);
        startActivity(intent);
    }
}