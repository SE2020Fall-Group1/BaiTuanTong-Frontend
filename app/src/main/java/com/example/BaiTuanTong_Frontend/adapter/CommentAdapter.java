package com.example.BaiTuanTong_Frontend.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.data.Comment;

import java.util.List;


public class CommentAdapter extends BaseAdapter {
    List<Comment> commentList;
    Context context;

    public CommentAdapter(Context icontext, List<Comment> icommentlist) {
        this.context = icontext;
        this.commentList = icommentlist;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return commentList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, null);
            holder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
            holder.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
            holder.tv_usr = (TextView) convertView.findViewById(R.id.tv_usr);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        Comment comment = commentList.get(position);
        holder.ll_item.setBackgroundColor(Color.WHITE);
        holder.tv_usr.setText(comment.getUser());
        holder.tv_comment.setText(comment.getComment());
        return convertView;
    }

    public final class ViewHolder{
        private LinearLayout ll_item;
        public TextView tv_usr;
        public TextView tv_comment;
        //public ImageView iv_icon;
    }
}


