package com.example.BaiTuanTong_Frontend.search_result.ui.club_search_result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.BaiTuanTong_Frontend.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClubSearchResultAdapter extends RecyclerView.Adapter<ClubSearchResultAdapter.ClubSearchResultViewHolder> {
    private List<String> clubName;
    private List<String> introduction;
    private Context mContext;

    public ClubSearchResultAdapter(Context mContext, List<String> clubName, List<String> introduction) {
        this.mContext = mContext;
        this.clubName = clubName;
        this.introduction = introduction;
    }

    class ClubSearchResultViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout club_list_content;
        private ImageView club_img;
        private TextView club_name;
        private TextView club_intro;

        public ClubSearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            club_list_content = itemView.findViewById(R.id.club_list_content);
            club_img = itemView.findViewById(R.id.club_img);
            club_name = itemView.findViewById(R.id.club_name);
            club_intro = itemView.findViewById(R.id.club_intro);
        }
    }

    @Override
    public ClubSearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.followed_clubs_recycler_item, parent, false);
        ClubSearchResultAdapter.ClubSearchResultViewHolder holder = new ClubSearchResultAdapter.ClubSearchResultViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClubSearchResultAdapter.ClubSearchResultViewHolder holder, int position) {
        holder.club_name.setText(clubName.get(position));
        holder.club_intro.setText(introduction.get(position));
        holder.club_intro.setTag(position);
        holder.club_img.setTag(position);
        holder.club_list_content.setTag(position);
/*        //自己做item点击
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onClick(position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null)
                    onItemLongClickListener.onLongClick(position);
                //返回false会在长按结束后继续点击
                return true;
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return clubName.size();
    }
}