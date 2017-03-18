package com.hplasplas.task6.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.hplasplas.task6.R;

import static com.hplasplas.task6.setting.Constants.ERROR_DIALOG_TITLE_TAG;

/**
 * Created by StarkinDG on 11.03.2017.
 */

public class RenameErrorDialog extends AppCompatDialogFragment {
    
    public static RenameErrorDialog newInstance(String message) {
        
        RenameErrorDialog dialog = new RenameErrorDialog();
        Bundle args = new Bundle();
        args.putString(ERROR_DIALOG_TITLE_TAG, message);
        dialog.setArguments(args);
        return dialog;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        
        builder.setTitle(getArguments().getString(ERROR_DIALOG_TITLE_TAG))
                .setIcon(R.mipmap.ic_report_problem_yellow_700_24dp)
                .setPositiveButton(R.string.button_ok, (dialog, id) -> RenameErrorDialog.this.getDialog().cancel());
        return builder.create();
    }
}
