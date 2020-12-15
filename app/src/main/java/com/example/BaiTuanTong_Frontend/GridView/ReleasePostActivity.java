package com.example.BaiTuanTong_Frontend.GridView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.BaiTuanTong_Frontend.R;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ReleasePostActivity extends AppCompatActivity {

    private GridView gridView;
    private Context mContext;
    private ArrayList<String> mPicList = new ArrayList<>();
    private MyGridViewAdapter myGridViewAdapter;


    private void viewPluImg(int pos){
        Toast.makeText(getApplicationContext(), "click a pic", Toast.LENGTH_SHORT);
    }
    private void selectPic(int maxTotal) {
        Toast.makeText(getApplicationContext(), "want to update a pic", Toast.LENGTH_SHORT);
     //   PictureSelector.create(this, maxTotal);
    }

    private void initGridView()
    {
        myGridViewAdapter = new MyGridViewAdapter(mContext, mPicList);
        gridView.setAdapter(myGridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == parent.getChildCount() - 1) {
                    //如果“增加按钮形状的”图片的位置是最后一张，且添加了的图片的数量不超过9张，才能点击
                    if (mPicList.size() == 9) {
                        //最多添加5张图片
                        viewPluImg(position);
                    } else {
                        //添加凭证图片
                        selectPic(9 - mPicList.size());
                    }
                } else {
                    viewPluImg(position);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_post);

        mContext = this;
        gridView = (GridView)findViewById(R.id.Gridview1);
     //   gridView.setAdapter(myGridViewAdapter);
        initGridView();
     //   gridView.setAdapter(new MyGridViewAdapter(ReleasePostActivity.this));
    }

    // 处理选择的照片的地址
 /*   private void refreshAdapter(List<LocalMedia> picList) {
        for (LocalMedia localMedia : picList) {
            //被压缩后的图片路径
            if (localMedia.isCompressed()) {
                String compressPath = localMedia.getCompressPath(); //压缩后的图片路径
                mPicList.add(compressPath); //把图片添加到将要上传的图片数组中
                myGridViewAdapter.notifyDataSetChanged();
            }
        }
    }
*/
}