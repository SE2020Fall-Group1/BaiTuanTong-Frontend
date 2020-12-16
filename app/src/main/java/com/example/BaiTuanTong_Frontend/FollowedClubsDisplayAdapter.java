package com.example.BaiTuanTong_Frontend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FollowedClubsDisplayAdapter extends RecyclerView.Adapter<FollowedClubsDisplayAdapter.FollowedClubsDisplayViewHolder> {
    private List<String> mList;
    private Context mContext;

    public FollowedClubsDisplayAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    // 下面是为点击事件添加的代码
    public interface OnItemClickListener {
        void onClick(int position);
    }

    MyAdapter.OnItemClickListener onItemClickListener;


    public void setOnItemClickListener(MyAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    // 上面是为点击事件添加的代码

    class FollowedClubsDisplayViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout club_list_content;
        private ImageView club_img;
        private TextView club_name;
        private TextView club_intro;

        public FollowedClubsDisplayViewHolder(@NonNull View itemView) {
            super(itemView);
            club_list_content = itemView.findViewById(R.id.club_list_content);
            club_img = itemView.findViewById(R.id.club_img);
            club_name = itemView.findViewById(R.id.club_name);
            club_intro = itemView.findViewById(R.id.club_intro);
        }
    }

    @Override
    public FollowedClubsDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.followed_clubs_recycler_item, parent, false);
        FollowedClubsDisplayAdapter.FollowedClubsDisplayViewHolder holder = new FollowedClubsDisplayAdapter.FollowedClubsDisplayViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FollowedClubsDisplayAdapter.FollowedClubsDisplayViewHolder holder, int position) {
        holder.club_name.setText(mList.get(position));
        holder.club_intro.setText("这是社团" + (position+1) + "的简介，为了测试自动换行，我多加一些内容进去，看看会不会有问题。");
        holder.club_intro.setTag(position);
        holder.club_img.setTag(position);
        holder.club_list_content.setTag(position);
        //自己做item点击
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onClick(position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
}