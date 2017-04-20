package com.hplasplas.task7.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.hplasplas.task7.R;

/**
 * Created by StarkinDG on 11.03.2017.
 */

public class MessageDialog extends AppCompatDialogFragment {
    
    public static final String DIALOG_MESSAGE_TAG = "DialogMessage";
    public static final String ICON_DIALOG_TAG = "DialogIcon";
    
    public static MessageDialog newInstance(String message, int icon) {
        
        MessageDialog dialog = new MessageDialog();
        Bundle args = new Bundle();
        args.putInt(ICON_DIALOG_TAG, icon);
        args.putString(DIALOG_MESSAGE_TAG, message);
        dialog.setArguments(args);
        return dialog;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MessageDialogStyle);
        
        builder.setTitle(getArguments().getString(DIALOG_MESSAGE_TAG))
                .setIcon(getArguments().getInt(ICON_DIALOG_TAG))
                .setPositiveButton(R.string.button_ok, (dialog, id) -> MessageDialog.this.getDialog().cancel());
        return builder.create();
    }
}
