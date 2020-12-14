package com.example.BaiTuanTong_Frontend.AdministratorAll;

import com.example.BaiTuanTong_Frontend.AdministratorAll.EditClubDialogFragment.CreateClubListener;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.AdministratorAll.EditAdminDialogFragment.ChangeAdminListener;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import android.os.Bundle;

import java.util.List;
import java.util.ArrayList;


public class ManagerHomePage extends AppCompatActivity implements ChangeAdminListener, CreateClubListener {
    private Toolbar mNavigation;
    private RecyclerView rv_manage_club;
    private LinearLayoutManager manager;
    private ManageClubAdapter adapter;

    public void initToolBar() {
        mNavigation.setTitle("社团管理");
        setSupportActionBar(mNavigation);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_homepage);
        mNavigation = findViewById(R.id.manager_homepage);
        initToolBar();
        initRecyclerLinear();
    }

    // 调试用
    private List<ClubData> mClubData;

    // List初始化，随意实现的
    private List<ClubData> getClubData(){
        List<ClubData> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new ClubData("Club "+i+"", "Admin "+i+""));
        }
        return list;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manager_homepage_menu, menu);
        return true;
    }



    // menu选项
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                EditClubDialogFragment editClubDialog = new EditClubDialogFragment();
                editClubDialog.show(getFragmentManager(), "EditClubDialog");
                // adapter.addData(1);
                break;
            case R.id.delete:
                adapter.removeData(1);
                break;
        }
        return true;
    }

    @Override
    public void createClubComplete(String clubName, String adminName) {
        Toast.makeText(this, clubName+adminName, Toast.LENGTH_SHORT).show();
        adapter.addData(1, clubName, adminName);
    }

    @Override
    public void changeAdminComplete(String adminName, int position) {
        adapter.changeAdmin(position, adminName);
    }

    public void initRecyclerLinear() {
        rv_manage_club = findViewById(R.id.rv_manage_club);
        manager = new LinearLayoutManager(this);
        rv_manage_club.setLayoutManager(manager);

        mClubData = getClubData();
        adapter = new ManageClubAdapter(this, mClubData);
        rv_manage_club.setAdapter(adapter);
        rv_manage_club.setItemAnimator(new DefaultItemAnimator());

        adapter.setOnItemClickListener(new ManageClubAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ManageClubAdapter.ViewName viewName, int position) {
                switch (viewName) {
                    case PRACTISE:
                        EditAdminDialogFragment editAdminDialog = new EditAdminDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        editAdminDialog.setArguments(bundle);
                        editAdminDialog.show(getFragmentManager(), "EditClubDialog");
                        // Toast.makeText(ManagerHomePage.this, position + " button click", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(ManagerHomePage.this, position + " click", Toast.LENGTH_SHORT).show();
                        break;
                }

                //Intent intent = new Intent(ManagerHomePage.this, ManageAdministrator.class);
                //intent.putExtra("clubName", mList.get(position));
                //startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

                // Toast.makeText(ManagerHomePage.this, position + " Long click", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder delete_confirm = new AlertDialog.Builder(ManagerHomePage.this);
                delete_confirm.setMessage("确认删除该社团吗？");
                delete_confirm.setTitle("提示");
                delete_confirm.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
                delete_confirm.setPositiveButton("确定", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        adapter.removeData(position);
                    }
                });

                delete_confirm.show();
            }
        });

    }
}
