package com.hplasplas.task6.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.hplasplas.task6.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hplasplas.task6.Setting.Constants.DEFAULT_FILE_NAME;
import static com.hplasplas.task6.Setting.Constants.LIST_FILE_NAME_TAG;
import static com.hplasplas.task6.Setting.Constants.LIST_INDEX_TAG;
import static com.hplasplas.task6.Setting.Constants.MUST_IMPLEMENT_INTERFACE_MESSAGE;

/**
 * Created by StarkinDG on 10.03.2017.
 */

public class FileNameInputDialog extends AppCompatDialogFragment {
    
    FileNameInputDialogListener mListener;
    private EditText myEditText;
    private int index;
    
    public static FileNameInputDialog newInstance(int index, String filename) {
        
        FileNameInputDialog dialog = new FileNameInputDialog();
        Bundle args = new Bundle();
        args.putInt(LIST_INDEX_TAG, index);
        args.putString(LIST_FILE_NAME_TAG, filename);
        dialog.setArguments(args);
        return dialog;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.rename_dialog_layaut, null);
        index = getArguments().getInt(LIST_INDEX_TAG);
        myEditText = (EditText) view.findViewById(R.id.editText);
        myEditText.setText(getArguments().getString(LIST_FILE_NAME_TAG, DEFAULT_FILE_NAME));
        builder.setView(view)
                .setTitle(R.string.rename_dialog_title)
                .setPositiveButton(R.string.button_ok, (dialog, id) -> returnRenameResult())
                .setNegativeButton(R.string.button_cancel, (dialog, id) -> FileNameInputDialog.this.getDialog().cancel());
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
    
    private boolean isValidFileName(String fileName) {
        
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_]+\\.jpg");
        Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();
    }
    
    private void returnRenameResult() {
        
        String newName = myEditText.getText().toString();
        if (isValidFileName(newName)) {
            mListener.onOkButtonClick(this, newName, index, true);
        } else {
            mListener.onOkButtonClick(this, newName, index, false);
        }
    }
    
    public interface FileNameInputDialogListener {
        
        public void onOkButtonClick(AppCompatDialogFragment dialog, String newFileName, int index, boolean successfully);
        
        public void onDialogNegativeClick(AppCompatDialogFragment dialog);
    }
}
