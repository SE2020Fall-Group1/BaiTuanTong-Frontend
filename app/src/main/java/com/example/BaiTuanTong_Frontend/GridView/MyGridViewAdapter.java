package com.example.BaiTuanTong_Frontend.GridView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.BaiTuanTong_Frontend.R;

import java.util.List;

public class MyGridViewAdapter extends BaseAdapter {

    private Context myContext;
    private LayoutInflater myLayout;
    private List<String> myList;

    public MyGridViewAdapter(Context context, List<String> mList)
    {
        myContext = context;
        myList = mList;
        myLayout = LayoutInflater.from(context);
    }

    static class ViewHolder
    {
        public ImageView Grid_imageview;
    //    public TextView Grid_textview;
    }

    @Override
    public int getCount() {
        int cnt = (myList == null) ? 1 : myList.size() + 1;
        if (cnt > 9)
            cnt = 9;
        return cnt;
    }

    @Override
    public Object getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    /*    ViewHolder holder = null;
        if(convertView == null){
            //填写ListView的图标和标题等控件的来源，来自于layout_list_item这个布局文件
            //把控件所在的布局文件加载到当前类中
            convertView = myLayout.inflate(R.layout.layout_gird_item,null);
            //生成一个ViewHolder的对象
            holder = new ViewHolder();
            //获取控件对象
            holder.Grid_imageview=convertView.findViewById(R.id.grid_IV_Id);
         //   holder.Grid_textview=convertView.findViewById(R.id.grid_TV_Id);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        //修改空间属性
     //   holder.Grid_textview.setText("汽车");
*/
        convertView = myLayout.inflate(R.layout.layout_gird_item, parent, false);
        ImageView iv = (ImageView)convertView.findViewById(R.id.grid_IV_Id);

        if (position < myList.size()){
            String picUrl = myList.get(position);
            Glide.with(myContext).load(picUrl).into(iv);
        }
        else{
            iv.setImageResource(R.mipmap.test_icon);
        }

        return convertView;
    }
}
