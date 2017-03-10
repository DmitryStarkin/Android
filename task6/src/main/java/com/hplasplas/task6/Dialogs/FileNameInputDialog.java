package com.hplasplas.task6.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.hplasplas.task6.R;

import static com.hplasplas.task6.Setting.Constants.MUST_IMPLEMENT_INTERFACE_MESSAGE;

/**
 * Created by StarkinDG on 10.03.2017.
 */

public class FileNameInputDialog extends AppCompatDialogFragment {
    
    FileNameInputDialogListener mListener;
    EditText myEditText;
    
    public interface FileNameInputDialogListener {
        public void onDialogPositiveClick(AppCompatDialogFragment dialog, String newFileName);
        public void onDialogNegativeClick(AppCompatDialogFragment dialog);
    }
    
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.rename_dialog_layaut, null);
        myEditText = (EditText) view.findViewById(R.id.editText);
        builder.setView(view)
                .setTitle(R.string.rename_dialog_title)
        
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO rename file
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        
                        FileNameInputDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
    
    @Override
    public void onAttach(Context context) {
        
        super.onAttach(context);
        try {
            mListener = (FileNameInputDialogListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + MUST_IMPLEMENT_INTERFACE_MESSAGE);
        }
    }
}