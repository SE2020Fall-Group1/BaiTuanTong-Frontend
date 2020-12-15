package com.example.BaiTuanTong_Frontend.data;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.BaiTuanTong_Frontend.PostContentActivity;
import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.utils.ScreenUtil;

import static android.view.Gravity.*;


public class CommentDialogFragment extends DialogFragment {

    private Button sendButton;
    private EditText commentEditText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_dialog, container,false);
        sendButton = (Button)view.findViewById(R.id.send_button);
        commentEditText = (EditText)view.findViewById(R.id.input_comment_edit_text);
        sendButton.setOnClickListener(new sendOnClickListener());
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
    /**
     * 修改布局的大小
     */
    @Override
    public void onStart() {
        super.onStart();
        resizeDialogFragment();

    }

    private void resizeDialogFragment() {
        Dialog dialog = getDialog();
        if (null != dialog) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
            lp.height = (1 * ScreenUtil.getScreenHeight(getContext()) / 5);//获取屏幕的宽度，定义自己的宽度
            lp.width = (9 * ScreenUtil.getScreenWidth(getContext()) / 10);
            if (window != null) {
                window.setLayout(lp.width, lp.height);
            }
        }
    }

    public class sendOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String comment = commentEditText.getText().toString();
            if(comment.equals(""))
                Toast.makeText(getContext(), "输入评论不能为空", Toast.LENGTH_SHORT).show();
            else
                CommentDialogFragment.this.dismiss();
        }
    }

}
