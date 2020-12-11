package com.example.wlspages;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context mContext;
    private List<String> mList;

    public MyAdapter(Context context, List<String> list){
        mContext = context;
        mList = list;
    }

    public int getItemCount(){
        return mList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup vg, int viewType){
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_post,vg,false);
        return new MyViewHolder(v, mOnItemClickListener);
    }
    // Item内部空间点击事件处理
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
    public enum ViewName{
        USR_IMG,
        POST_TEXT,
        ITEM
    }
    public interface OnItemClickListener {
        void onInternalViewClick(View view, ViewName viewName, int position);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position){
        // 给post设置内容，尚未从后端获取，在前端自定义实现
        holder.usr_id.setText("测试用户"+position);
        //holder.post_text.setText(mList.get(position));
        holder.post_text.setText("这是一条无聊的动态"+"\n"+"第二行"+"\n"+"第三行");
        holder.usr_img.setTag(position);
        holder.post_text.setTag(position);
        holder.post_content.setTag(position);
        /*
        holder.post_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener!=null){
                    mOnItemClickListener.onClick(position);
                }
            }
        });*/
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout post_content;
        public ImageView usr_img;
        public TextView usr_id;
        public TextView post_text;

        public MyViewHolder(View v, OnItemClickListener onItemClickListener){
            super(v);
            post_content = v.findViewById(R.id.post_content);
            usr_id = v.findViewById(R.id.usr_id);
            usr_img = v.findViewById(R.id.usr_img);
            post_text = v.findViewById(R.id.post_text);

            usr_img.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    if (onItemClickListener!=null){
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            onItemClickListener.onInternalViewClick(view, ViewName.USR_IMG,position);
                        }
                    }
                }
            });
            post_text.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    if (onItemClickListener!=null){
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            onItemClickListener.onInternalViewClick(view, ViewName.POST_TEXT,position);
                        }
                    }
                }
            });
        }
    }
}

