package com.example.BaiTuanTong_Frontend.search_result.ui.post_search_result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BaiTuanTong_Frontend.R;

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

    // 设置一个枚举类型给使用接口的Activity/Fragment传参，使其知道点击类型
    public enum ViewName{
        CLUB_IMG,
        CLUB_NAME,
        POST_CONTENT
    }

    public interface OnItemClickListener {
        void onInternalViewClick(View view, PostSearchResultAdapter.ViewName viewName, int position);
    }

    // Item内部空间点击事件处理
    private PostSearchResultAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(PostSearchResultAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    class PostSearchResultViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout post_list_content;
        private ImageView club_img;
        private TextView post_title;
        private TextView post_clubName;
        private TextView post_text;
        private TextView post_likeCnt;
        private TextView post_commentCnt;

        public PostSearchResultViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            post_list_content = itemView.findViewById(R.id.post_list_content);
            club_img = itemView.findViewById(R.id.club_img);
            post_title = itemView.findViewById(R.id.post_title);
            post_clubName = itemView.findViewById(R.id.post_clubName);
            post_text = itemView.findViewById(R.id.post_text);
            post_likeCnt = itemView.findViewById(R.id.post_likeCnt);
            post_commentCnt = itemView.findViewById(R.id.post_commentCnt);

            // 设置内部点击事件
            club_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onInternalViewClick(view, ViewName.CLUB_IMG, position);
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
                            onItemClickListener.onInternalViewClick(view, ViewName.CLUB_NAME, position);
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
                            onItemClickListener.onInternalViewClick(view, ViewName.POST_CONTENT, position);
                        }
                    }
                }
            });
        }
    }

    @Override
    public PostSearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_post_list_recycler, parent, false);
        PostSearchResultAdapter.PostSearchResultViewHolder holder = new PostSearchResultAdapter.PostSearchResultViewHolder(itemView, mOnItemClickListener);
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
    }



    @Override
    public int getItemCount() {
        return title.size();
    }
}