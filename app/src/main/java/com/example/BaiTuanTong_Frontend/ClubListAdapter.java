package com.example.BaiTuanTong_Frontend;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class ClubListAdapter extends RecyclerView.Adapter<ClubListAdapter.ClubListViewHolder> {
    private List<String> clubName;
    private List<String> introduction;
    private List<Bitmap> clubImg;
    private Context mContext;

    public ClubListAdapter(Context mContext, List<String> clubName, List<String> introduction) {
        this.mContext = mContext;
        this.clubName = clubName;
        this.introduction = introduction;
    }

    public ClubListAdapter(Context mContext, List<String> clubName, List<String> introduction, List<Bitmap> clubImg) {
        this.mContext = mContext;
        this.clubName = clubName;
        this.introduction = introduction;
        this.clubImg = clubImg;
    }

    // 下面是为点击事件添加的代码
    public interface OnItemClickListener {
        void onClick(int position);
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ClubListViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout club_list_content;
        public ImageView club_img;
        private TextView club_name;
        private TextView club_intro;

        public ClubListViewHolder(@NonNull View itemView) {
            super(itemView);
            club_list_content = itemView.findViewById(R.id.club_list_content);
            club_img = itemView.findViewById(R.id.club_img);
            club_name = itemView.findViewById(R.id.club_name);
            club_intro = itemView.findViewById(R.id.club_intro);
        }
    }

    @Override
    public ClubListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_club_list_recycler, parent, false);
        ClubListAdapter.ClubListViewHolder holder = new ClubListAdapter.ClubListViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClubListAdapter.ClubListViewHolder holder, int position) {
        holder.club_name.setText(clubName.get(position));
        holder.club_intro.setText(introduction.get(position));
        holder.club_intro.setTag(position);
        if (clubImg != null)
            holder.club_img.setImageBitmap(clubImg.get(position));
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
        return clubName.size();
    }
}