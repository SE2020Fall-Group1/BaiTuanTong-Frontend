package com.example.BaiTuanTong_Frontend.data;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.BaiTuanTong_Frontend.R;

import static android.view.Gravity.*;


public class CommentDialogFragment extends DialogFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_dialog, container,false);
        // 隐藏标题栏, 不加弹窗上方会一个透明的标题栏占着空间
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 必须设置这两个,才能设置宽度
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

        // 遮罩层透明度
        getDialog().getWindow().setDimAmount(0);

        // 设置宽度
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = BOTTOM | CENTER_HORIZONTAL;
        params.windowAnimations = R.style.bottomSheet_animation;
        getDialog().getWindow().setAttributes(params);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
