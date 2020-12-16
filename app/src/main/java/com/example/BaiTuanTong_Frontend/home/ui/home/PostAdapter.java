package com.example.BaiTuanTong_Frontend.home.ui.home;

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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private List<String> title;
    private List<String> clubName;
    private List<String> text;
    private List<String> likeCnt;
    private List<String> commentCnt;
    private Context mContext;

    public PostAdapter(Context context, List<String> title, List<String> clubName, List<String> text, List<String> likeCnt, List<String> commentCnt){
        this.mContext = mContext;
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
        POST_TEXT,
        ITEM
    }
    public interface OnItemClickListener {
        void onInternalViewClick(View view, ViewName viewName, int position);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position){
        // 给post设置内容，尚未从后端获取，在前端自定义实现
        holder.post_clubName.setText("测试用户"+position);
        //holder.post_text.setText(mList.get(position));
        holder.post_text.setText("这是一条无聊的动态"+"\n"+"第二行"+"\n"+"第三行");
        holder.club_img.setTag(position);
        holder.post_text.setTag(position);
        holder.post_list_content.setTag(position);
        /*
        holder.post_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener!=null){
                    mOnItemClickListener.onClick(position);
                }
            }
        });*/
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        /*
        public LinearLayout post_content;
        public ImageView usr_img;
        public TextView usr_id;
        public TextView post_text;*/

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
            /*
            post_content = v.findViewById(R.id.post_content);
            usr_id = v.findViewById(R.id.usr_id);
            usr_img = v.findViewById(R.id.usr_img);
            post_text = v.findViewById(R.id.post_text);*/

            club_img.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    if (onItemClickListener!=null){
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            onItemClickListener.onInternalViewClick(view, ViewName.CLUB_IMG,position);
                        }
                    }
                }
            });
            post_text.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    if (onItemClickListener!=null){
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            onItemClickListener.onInternalViewClick(view, ViewName.POST_TEXT,position);
                        }
                    }
                }
            });
        }
    }
}
