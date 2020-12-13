package com.example.BaiTuanTong_Frontend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Toast;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.MyViewHolder> {
    private List<String> mList;
    private Context mContext;
    public enum ViewName {  // 区分多个控件的点击事件
        ITEM,
        PRACTISE
    }

    public ManagerAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addData(int position, String clubName, String adminName) {
        mList.add(position, clubName+adminName);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }


    // 下面是为点击事件添加的代码
    //定义接口 OnItemClickListener
    public interface OnItemClickListener {
        void onItemClick(View view, ViewName viewName, int position);
        void onItemLongClick(View view, int position);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.club_list_recycler, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv.setText(mList.get(position));

        //自己做item点击
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, ViewName.ITEM, pos);
                }
            });

            holder.bt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, ViewName.PRACTISE, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;
        private Button bt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.clubItem);
            bt = itemView.findViewById(R.id.changeAdministrator);
        }
    }
}