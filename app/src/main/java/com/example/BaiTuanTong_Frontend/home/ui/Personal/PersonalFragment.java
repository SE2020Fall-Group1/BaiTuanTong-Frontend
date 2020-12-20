package com.example.BaiTuanTong_Frontend.home.ui.Personal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.BaiTuanTong_Frontend.FollowedClubsDisplayActivity;
import com.example.BaiTuanTong_Frontend.ManageClubsActivity;
import com.example.BaiTuanTong_Frontend.PostListDisplayActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.home.HomePageActivity;
import com.example.BaiTuanTong_Frontend.ui.login.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

public class PersonalFragment extends Fragment {

    private PersonalViewModel personalViewModel;
    private Button manageClubButton;
    private Button followClubButton;
    private Button collectedPostButton;
    private Button configureButton;
    private Button signOutButton;
    private TextView tv_username;
    private String username;
    private SharedPreferences shared;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        shared = getActivity().getSharedPreferences("share", MODE_PRIVATE);
        username = shared.getString("userName","");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        personalViewModel =
                new ViewModelProvider(this).get(PersonalViewModel.class);
        View root = inflater.inflate(R.layout.fragment_personal, container, false);

        manageClubButton = (Button)root.findViewById(R.id.manage_club);
        followClubButton = (Button)root.findViewById(R.id.follow_club);
        signOutButton = (Button)root.findViewById((R.id.sign_out));
        collectedPostButton = (Button)root.findViewById(R.id.collect_post);
        tv_username = (TextView)root.findViewById(R.id.personal_id);
        tv_username.setText(username);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) { //如果点击关注社团按钮，就跳转到关注社团页面
        super.onActivityCreated(savedInstanceState);

        manageClubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManageClubsActivity.class);
                startActivityForResult(intent, 3);
            }
        });
        followClubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowedClubsDisplayActivity.class);
                startActivityForResult(intent, 3);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = shared.edit();
                editor.remove("logged");
                editor.commit();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        collectedPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostListDisplayActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3 && resultCode == 3){
            HomePageActivity homePageActivity = (HomePageActivity)getActivity();
            assert homePageActivity != null;
            FragmentManager fragmentManager = homePageActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.replace(R.id.personal_manage, new PersonalFragment());
            fragmentTransaction.commit();
        }
    }

}