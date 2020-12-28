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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostListViewHolder> {
    private List<String> title;         // 动态标题
    private List<String> clubName;      // 社团名称
    private List<String> text;          // 动态内容
    private List<String> likeCnt;       // 点赞数
    private List<String> commentCnt;    // 评论数
    private List<Bitmap> clubImg;       // 社团图片
    private Context mContext;

    public PostListAdapter(Context mContext, List<String> title, List<String> clubName, List<String> text,
                           List<String> likeCnt, List<String> commentCnt) {
        this.mContext = mContext;
        this.title = title;
        this.clubName = clubName;
        this.text = text;
        this.likeCnt = likeCnt;
        this.clubImg = null;
        this.commentCnt = commentCnt;
    }

    public PostListAdapter(Context mContext, List<String> title, List<String> clubName, List<String> text,
                           List<String> likeCnt, List<String> commentCnt, List<Bitmap> clubImg) {
        this.mContext = mContext;
        this.title = title;
        this.clubName = clubName;
        this.text = text;
        this.likeCnt = likeCnt;
        this.clubImg = clubImg;
        this.commentCnt = commentCnt;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Item内部空间点击事件处理
    private PostListAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(PostListAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemLongClickListener {
        void onLongClick(int position);
    }

    PostListAdapter.OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(PostListAdapter.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public class PostListViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout post_list_content;
        public ImageView club_img;
        private TextView post_title;
        private TextView post_clubName;
        private TextView post_text;
        public TextView post_likeCnt;
        public TextView post_commentCnt;
        public CardView post_card;

        public PostListViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            post_list_content = itemView.findViewById(R.id.post_list_content);
            club_img = itemView.findViewById(R.id.club_img);
            post_title = itemView.findViewById(R.id.post_title);
            post_clubName = itemView.findViewById(R.id.post_clubName);
            post_text = itemView.findViewById(R.id.post_text);
            post_likeCnt = itemView.findViewById(R.id.post_likeCnt);
            post_commentCnt = itemView.findViewById(R.id.post_commentCnt);
            post_card = itemView.findViewById(R.id.card_view);

            // 设置内部点击事件
            club_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(view, position);
                        }
                    }
                }
            });
            post_clubName.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(view, position);
                        }
                    }
                }
            });
            post_list_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(view, position);
                        }
                    }
                }
            });

            post_list_content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemLongClickListener.onLongClick(position);
                        }
                    }
                    //返回false会在长按结束后继续点击
                    return true;
                }
            });
        }
    }

    @Override
    public PostListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_post_list_recycler, parent, false);
        PostListAdapter.PostListViewHolder holder = new PostListAdapter.PostListViewHolder(itemView, mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostListAdapter.PostListViewHolder holder, int position) {
        holder.post_title.setText(title.get(position));
        holder.post_clubName.setText(clubName.get(position));
        holder.post_text.setText(text.get(position));
        holder.club_img.setTag(position);
        holder.post_likeCnt.setText(likeCnt.get(position));
        holder.post_commentCnt.setText(commentCnt.get(position));
        holder.post_list_content.setTag(position);
        if (clubImg != null) {
            holder.club_img.setImageBitmap(clubImg.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return title.size();
    }
}