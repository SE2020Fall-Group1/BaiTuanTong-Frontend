package com.example.BaiTuanTong_Frontend.club;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import com.example.BaiTuanTong_Frontend.MyAdapter;
import com.example.BaiTuanTong_Frontend.R;

import java.util.ArrayList;
import java.util.List;


public class EditClubAdminActivity extends AppCompatActivity {

    private RecyclerView adminListView;
    private List<String> adminList;;
    private MyAdapter mMyAdapter;
    private boolean deleting;
    private Switch mySwitch;

    private List<String> getList()
    {
        List<String> ret = new ArrayList<>();
        for (int i = 1; i <= 10; ++i)
        {
            ret.add("username：李子恒" + "\nid:"+ i + "" + "");
        }
        return ret;
    }

//    public void

    @Override
    public boolean onCreateOptionsMenu(Menu menu)//添加右上角三个点儿
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_club_admin_menu, menu);
        //这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add_club_admin:
                Toast.makeText(getApplicationContext(),
                        "点击了添加成员按钮",
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_club_admin);

        deleting = false;

        adminListView = findViewById(R.id.recyclerView2);
        // 设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(adminListView.VERTICAL);
        adminListView.setLayoutManager(linearLayoutManager);
        // 设置 item 增加和删除时的动画
        adminListView.setItemAnimator(new DefaultItemAnimator());

        adminList = getList();

        mMyAdapter = new MyAdapter(this, adminList);
        adminListView.setAdapter(mMyAdapter);

        mMyAdapter.setOnItemLongClickListener(new MyAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position) {
                //长按删除一个成员

                if (deleting == false)
                    return ;
                adminList.remove(position);
                mMyAdapter.notifyDataSetChanged();
            }
        });

        //设定删除开关。
        mySwitch = (Switch)findViewById(R.id.switch1);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if(isChecked)
                {
                    Toast.makeText(getApplicationContext(),
                            "删除模式开启，长按成员列表项完成删除",
                            Toast.LENGTH_SHORT).show();
                //    Toast.makeText(this,"删除模式开启，长按成员项完成删除",Toast.LENGTH_SHORT).show();
                    deleting = true;
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "删除模式关闭",
                            Toast.LENGTH_SHORT).show();
                    deleting = false;
                }
            }
        });




/*        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.edit_club_admin_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Toast.makeText(this, "my flaut", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        popupMenu.show();
*/
    }

}