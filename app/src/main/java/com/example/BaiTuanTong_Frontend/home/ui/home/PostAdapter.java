package com.example.BaiTuanTong_Frontend.home.ui.home;

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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private List<String> title;
    private List<String> clubName;
    private List<String> text;
    private List<Integer> likeCnt;
    private List<Integer> commentCnt;
    private Context mContext;

    public PostAdapter(Context context, List<String> title, List<String> clubName, List<String> text, List<Integer> likeCnt, List<Integer> commentCnt){
        this.mContext = context;
        this.title = title;
        this.clubName = clubName;
        this.text = text;
        this.likeCnt = likeCnt;
        this.commentCnt = commentCnt;
    }

    public int getItemCount(){
        return title.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup vg, int viewType){
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_post_list_recycler,vg,false);
        return new MyViewHolder(v, mOnItemClickListener);
    }
    // Item内部空间点击事件处理
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
    // 设置一个枚举类型给使用接口的Activity/Fragment传参，使其知道点击类型
    public enum ViewName{
        CLUB_IMG,
        CLUB_NAME,
        POST_CONTENT
    }
    public interface OnItemClickListener {
        void onInternalViewClick(View view, ViewName viewName, int position);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position){
        // 给post设置内容，尚未从后端获取，在前端自定义实现
        holder.post_title.setText(title.get(position));
        holder.post_clubName.setText(clubName.get(position));
        holder.post_text.setText(text.get(position));
        holder.club_img.setTag(position);
        holder.post_likeCnt.setText("" + likeCnt.get(position));
        holder.post_commentCnt.setText("" + commentCnt.get(position));
        holder.post_list_content.setTag(position);
            }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout post_list_content;
        private ImageView club_img;
        private TextView post_title;
        private TextView post_clubName;
        private TextView post_text;
        private TextView post_likeCnt;
        private TextView post_commentCnt;

        public MyViewHolder(View v, OnItemClickListener onItemClickListener){
            super(v);

            post_list_content = v.findViewById(R.id.post_list_content);
            club_img = v.findViewById(R.id.club_img);
            post_title = v.findViewById(R.id.post_title);
            post_clubName = v.findViewById(R.id.post_clubName);
            post_text = v.findViewById(R.id.post_text);
            post_likeCnt = v.findViewById(R.id.post_likeCnt);
            post_commentCnt = v.findViewById(R.id.post_commentCnt);

            // 设置内部点击事件
            club_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onInternalViewClick(view, PostAdapter.ViewName.CLUB_IMG, position);
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
                            onItemClickListener.onInternalViewClick(view, PostAdapter.ViewName.CLUB_NAME, position);
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
                            onItemClickListener.onInternalViewClick(view, PostAdapter.ViewName.POST_CONTENT, position);
                        }
                    }
                }
            });
        }
    }
}
