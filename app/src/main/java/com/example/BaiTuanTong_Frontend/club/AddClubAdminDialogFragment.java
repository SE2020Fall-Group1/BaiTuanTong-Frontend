package com.example.BaiTuanTong_Frontend.club;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.BaiTuanTong_Frontend.R;

public class AddClubAdminDialogFragment extends DialogFragment {

    private Button add_admin_button;
    private EditText newAdminName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_add_club_admin,null);

        add_admin_button = (Button)view.findViewById(R.id.add_button);
        add_admin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "点击了确认按钮", Toast.LENGTH_SHORT).show();
                //这里传递文本框的数据。
                newAdminName = (EditText)view.findViewById(R.id.editText1);
                Intent intent = new Intent();
                intent.putExtra("adminName", newAdminName.getText().toString());
                ((EditClubAdminActivity)getActivity()).onActivityResult(
                        EditClubAdminActivity.REQUEST_CODE_SUBMIT, Activity.RESULT_OK, intent);
                getDialog().dismiss();
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}