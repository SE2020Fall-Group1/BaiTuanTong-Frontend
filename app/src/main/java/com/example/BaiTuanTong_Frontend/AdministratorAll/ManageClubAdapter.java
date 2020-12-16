package com.example.BaiTuanTong_Frontend.AdministratorAll;

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

import com.example.BaiTuanTong_Frontend.R;

public class ManageClubAdapter extends RecyclerView.Adapter<ManageClubAdapter.MyViewHolder> {
    public List<ClubData> mClubData;
    private Context mContext;
    public enum ViewName {  // 区分多个控件的点击事件
        ITEM,
        PRACTISE
    }

    public ManageClubAdapter(Context context, List<ClubData> clubData) {
        this.mContext = context;
        this.mClubData = clubData;
    }



    @Override
    public int getItemCount() {
        return mClubData.size();
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addData(int position, String clubName, String adminName) {
        if (mClubData.size() < position) {
            mClubData.add(new ClubData(clubName, adminName));
        }
        else
            mClubData.add(position, new ClubData(clubName, adminName));
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        mClubData.remove(position);
        notifyItemRemoved(position);
    }

    public void changeAdmin(int position, String adminName) {
        mClubData.get(position).setAdminName(adminName);
        notifyItemChanged(position);
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
        holder.tv.setText(mClubData.get(position).getClubName() + "\n" + mClubData.get(position).getAdminName());

        //自己做item点击
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, ViewName.ITEM, pos);
                }
            });

            holder.changePresidentButton.setOnClickListener(new OnClickListener() {
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
        private Button changePresidentButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.clubItem);
            changePresidentButton = itemView.findViewById(R.id.changeAdministrator);
        }
    }
}