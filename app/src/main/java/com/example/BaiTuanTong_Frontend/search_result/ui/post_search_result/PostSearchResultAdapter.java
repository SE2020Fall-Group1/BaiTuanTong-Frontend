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
    private List<String> title;
    private List<String> clubName;
    private List<String> text;
    private List<String> likeCnt;
    private List<String> commentCnt;
    private Context mContext;

    public PostSearchResultAdapter(Context mContext, List<String> title, List<String> clubName, List<String> text, List<String> likeCnt, List<String> commentCnt) {
        this.mContext = mContext;
        this.title = title;
        this.clubName = clubName;
        this.text = text;
        this.likeCnt = likeCnt;
        this.commentCnt = commentCnt;
    }

    class PostSearchResultViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout post_list_content;
        private ImageView club_img;
        private TextView post_title;
        private TextView post_clubName;
        private TextView post_text;
        private TextView post_likeCnt;
        private TextView post_commentCnt;

        public PostSearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            post_list_content = itemView.findViewById(R.id.post_list_content);
            club_img = itemView.findViewById(R.id.club_img);
            post_title = itemView.findViewById(R.id.post_title);
            post_clubName = itemView.findViewById(R.id.post_clubName);
            post_text = itemView.findViewById(R.id.post_text);
            post_likeCnt = itemView.findViewById(R.id.post_likeCnt);
            post_commentCnt = itemView.findViewById(R.id.post_commentCnt);
        }
    }

    @Override
    public PostSearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_post_list_recycler, parent, false);
        PostSearchResultAdapter.PostSearchResultViewHolder holder = new PostSearchResultAdapter.PostSearchResultViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostSearchResultAdapter.PostSearchResultViewHolder holder, int position) {
        holder.post_title.setText(title.get(position));
        holder.post_clubName.setText(clubName.get(position));
        holder.post_text.setText(text.get(position));
        holder.club_img.setTag(position);
        holder.post_likeCnt.setText("Likes: " + likeCnt.get(position));
        holder.post_commentCnt.setText("Comments: " + commentCnt.get(position));
        holder.post_list_content.setTag(position);
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
        return title.size();
    }
}