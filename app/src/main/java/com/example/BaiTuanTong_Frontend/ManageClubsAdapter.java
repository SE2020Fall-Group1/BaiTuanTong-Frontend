package com.example.BaiTuanTong_Frontend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ManageClubsAdapter extends RecyclerView.Adapter<ManageClubsAdapter.ManageClubsViewHolder> {
    private List<String> clubName;
    private List<String> introduction;
    private Context mContext;

    public ManageClubsAdapter(Context mContext, List<String> clubName, List<String> introduction) {
        this.mContext = mContext;
        this.clubName = clubName;
        this.introduction = introduction;
    }

    // 下面是为点击事件添加的代码
    public interface OnItemClickListener {
        void onClick(int position);
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ManageClubsViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout club_list_content;
        private ImageView club_img;
        private TextView club_name;
        private TextView club_intro;

        public ManageClubsViewHolder(@NonNull View itemView) {
            super(itemView);
            club_list_content = itemView.findViewById(R.id.club_list_content);
            club_img = itemView.findViewById(R.id.club_img);
            club_name = itemView.findViewById(R.id.club_name);
            club_intro = itemView.findViewById(R.id.club_intro);
        }
    }

    @Override
    public ManageClubsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_club_list_recycler, parent, false);
        ManageClubsAdapter.ManageClubsViewHolder holder = new ManageClubsAdapter.ManageClubsViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ManageClubsAdapter.ManageClubsViewHolder holder, int position) {
        holder.club_name.setText(clubName.get(position));
        holder.club_intro.setText(introduction.get(position));
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
        return clubName.size();
    }
}