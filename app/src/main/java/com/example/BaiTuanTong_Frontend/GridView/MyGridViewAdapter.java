package com.example.BaiTuanTong_Frontend.GridView;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.BaiTuanTong_Frontend.R;

import java.io.IOException;
import java.util.List;

public class MyGridViewAdapter extends BaseAdapter {

    private Context myContext;
    private LayoutInflater myLayout;
    private List<Uri> mUriList;
    private List<String> mPathList;
    // 最多发布图片数
    
    private final int MAX_IMG_NUM = 1;

    public MyGridViewAdapter(Context context, List<Uri> uriList, List<String> pathList)
    {
        myContext = context;
        mUriList = uriList;
        mPathList = pathList;
        myLayout = LayoutInflater.from(context);
    }

    //如果超过九个 就不显示+号了，也就没法添加新的
    @Override
    public int getCount() {
        int cnt = (mUriList == null) ? 1 : mUriList.size() + 1;
        if (cnt > MAX_IMG_NUM)
            cnt = MAX_IMG_NUM;
        return cnt;
    }

    @Override
    public Object getItem(int position) {
        return mUriList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = myLayout.inflate(R.layout.layout_gird_item, parent, false);
        ImageView iv = (ImageView)convertView.findViewById(R.id.grid_IV_Id);

        if (position < mUriList.size()){
            Uri picUrI = mUriList.get(position);
            Bitmap bm = null;
            ContentResolver resolver = myContext.getContentResolver();
            try {
                // 通过Uri来获取bitmap图片展示在九宫格里
                bm = MediaStore.Images.Media.getBitmap(resolver, picUrI);
                // 设置一下
                iv.setImageBitmap(bm);
            } catch (IOException e) {
                Log.e("TAG-->Error", e.toString());
            }

        }
        else{
            // 背景是一个加号
            iv.setImageResource(R.drawable.icon_plus);
        }

        return convertView;
    }
}
