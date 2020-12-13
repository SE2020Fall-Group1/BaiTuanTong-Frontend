package com.example.BaiTuanTong_Frontend;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;


public class EditClubDialogFragment extends DialogFragment {
    private EditText mClubName;
    private EditText mAdminName;

    public interface CreateClubListener {
        void createClubComplete(String clubName, String adminName);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_edit_club, null);
        mClubName = (EditText) view.findViewById(R.id.id_edit_club_name);
        mAdminName = (EditText) view.findViewById(R.id.id_edit_admin_name);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view) // Add action buttons
            .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // Toast.makeText(getActivity(), mClubName.getText().toString() + mAdminName.getText().toString(), Toast.LENGTH_SHORT).show();
                    CreateClubListener listener = (CreateClubListener)getActivity();
                    listener.createClubComplete(mClubName.getText().toString(), mAdminName.getText().toString());
                }
                })
                .setNegativeButton("取消", null);
        return builder.create();
    }
}