package com.example.BaiTuanTong_Frontend.home.ui.Personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.BaiTuanTong_Frontend.FollowedClubsDisplayActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.home.HomePageActivity;
import com.example.BaiTuanTong_Frontend.club.ClubHomeActivity;

public class PersonalFragment extends Fragment {

    private PersonalViewModel personalViewModel;
    private Button followClubButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        personalViewModel =
                new ViewModelProvider(this).get(PersonalViewModel.class);
        View root = inflater.inflate(R.layout.fragment_personal, container, false);

        followClubButton = (Button)root.findViewById(R.id.follow_club);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) { //如果点击关注社团按钮，就跳转到关注社团页面
        super.onActivityCreated(savedInstanceState);
        followClubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowedClubsDisplayActivity.class);
                //目前跳转到社团主页，为了调试
                startActivityForResult(intent, 3);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3 && resultCode == 3){
            HomePageActivity homePageActivity = (HomePageActivity)getActivity();
            FragmentManager fragmentManager = homePageActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.replace(R.id.personal_manage, new PersonalFragment());
            fragmentTransaction.commit();
        }
    }

    /*@Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getActivity(),"Personal is onResume",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause(){
        super.onPause();
        Toast.makeText(getActivity(),"Personal is onPause",Toast.LENGTH_SHORT).show();
    }*/

}