package com.hplasplas.task7.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.hplasplas.task7.R;

import static com.hplasplas.task7.setting.Constants.MESSAGE_DIALOG_TAG;

/**
 * Created by StarkinDG on 11.03.2017.
 */

public class MessageDialog extends AppCompatDialogFragment {
    
    public static MessageDialog newInstance(String message) {
        
        MessageDialog dialog = new MessageDialog();
        Bundle args = new Bundle();
        args.putString(MESSAGE_DIALOG_TAG, message);
        dialog.setArguments(args);
        return dialog;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MessageDialogStyle);
        
        builder.setTitle(getArguments().getString(MESSAGE_DIALOG_TAG))
                .setIcon(R.drawable.ic_info_outline_white_24dp)
                .setPositiveButton(R.string.button_ok, (dialog, id) -> MessageDialog.this.getDialog().cancel());
        return builder.create();
    }
}
