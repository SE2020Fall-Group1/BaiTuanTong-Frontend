package com.example.BaiTuanTong_Frontend.SystemAdministrator;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

import com.example.BaiTuanTong_Frontend.R;


public class EditAdminDialogFragment extends DialogFragment {
    private EditText mAdminName;
    private int position;

    public interface ChangeAdminListener {
        void changeAdminComplete(String newAdminName, int position);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        position = this.getArguments().getInt("position");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_edit_admin, null);
        mAdminName = (EditText) view.findViewById(R.id.id_edit_admin_name);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view) // Add action buttons
                .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Toast.makeText(getActivity(), mClubName.getText().toString() + mAdminName.getText().toString(), Toast.LENGTH_SHORT).show();
                        ChangeAdminListener listener = (ChangeAdminListener)getActivity();
                        listener.changeAdminComplete(mAdminName.getText().toString(), position);
                    }
                })
                .setNegativeButton("取消", null);
        return builder.create();
    }
}