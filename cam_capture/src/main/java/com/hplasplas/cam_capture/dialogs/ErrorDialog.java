package com.hplasplas.cam_capture.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.hplasplas.cam_capture.R;

import static com.hplasplas.cam_capture.setting.Constants.ERROR_DIALOG_TITLE_TAG;

/**
 * Created by StarkinDG on 11.03.2017.
 */

public class ErrorDialog extends AppCompatDialogFragment {
    
    public static ErrorDialog newInstance(String message) {
        
        ErrorDialog dialog = new ErrorDialog();
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
                .setPositiveButton(R.string.button_ok, (dialog, id) -> ErrorDialog.this.getDialog().cancel());
        return builder.create();
    }
}
