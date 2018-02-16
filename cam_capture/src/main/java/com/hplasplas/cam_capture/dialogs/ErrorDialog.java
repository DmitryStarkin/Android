/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * This file is part of cam_capture
 *
 *     cam_capture is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *    cam_capture is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cam_capture  If not, see <http://www.gnu.org/licenses/>.
 */
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
