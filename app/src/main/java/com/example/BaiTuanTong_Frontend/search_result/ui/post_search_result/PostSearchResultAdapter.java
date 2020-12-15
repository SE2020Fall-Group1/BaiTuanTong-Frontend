package com.example.BaiTuanTong_Frontend.search_result.ui.post_search_result;

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

public class PostSearchResultAdapter extends RecyclerView.Adapter<PostSearchResultAdapter.PostSearchResultViewHolder> {
    private List<String> mList;
    private Context mContext;

    public PostSearchResultAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    class PostSearchResultViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout club_list_content;
        private ImageView club_img;
        private TextView club_name;
        private TextView club_intro;

        public PostSearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            club_list_content = itemView.findViewById(R.id.club_list_content);
            club_img = itemView.findViewById(R.id.club_img);
            club_name = itemView.findViewById(R.id.club_name);
            club_intro = itemView.findViewById(R.id.club_intro);
        }
    }

    @Override
    public PostSearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.followed_clubs_recycler_item, parent, false);
        PostSearchResultAdapter.PostSearchResultViewHolder holder = new PostSearchResultAdapter.PostSearchResultViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostSearchResultAdapter.PostSearchResultViewHolder holder, int position) {
        holder.club_name.setText(mList.get(position));
        holder.club_intro.setText("    这是动态" + position + "的部分内容" + "");
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
        return mList.size();
    }
}
