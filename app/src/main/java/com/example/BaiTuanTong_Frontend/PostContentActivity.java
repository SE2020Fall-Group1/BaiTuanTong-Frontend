package com.example.BaiTuanTong_Frontend;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.BaiTuanTong_Frontend.adapter.CommentAdapter;
import com.example.BaiTuanTong_Frontend.data.Comment;
import com.example.BaiTuanTong_Frontend.data.CommentDialogFragment;
import com.example.BaiTuanTong_Frontend.data.ListViewUnderScroll;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class PostContentActivity extends AppCompatActivity {

    private ListView commentListView;
    private CommentAdapter commentAdapter;
    private CommentDialogFragment commentDialogFragment;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //创建一个评论列表，后续用后端传来数据集构建
        List<Comment> commentList = new ArrayList<Comment>();
        for(int i=0;i<100;i++) {
            commentList.add(new Comment("zhp:", "hahaha"));
            commentList.add(new Comment("wls:", "miaomiaomiao"));
            commentList.add(new Comment("zsh:", "lueluelue"));
        }

        //设置评论list
        commentListView = (ListViewUnderScroll)findViewById(R.id.comment_list);
        //commentListView.addHeaderView(new ViewStub(this));
        commentAdapter = new CommentAdapter(this, commentList);
        commentListView.setAdapter(commentAdapter);

        //创建dialogFragment
        commentDialogFragment = new CommentDialogFragment();
        Button commentButton = (Button)findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new MyOnClickListener());

    }
    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.comment_button:
                    commentDialogFragment.show(getSupportFragmentManager(),"dialog");
                    break;

            }
        }
    }
}