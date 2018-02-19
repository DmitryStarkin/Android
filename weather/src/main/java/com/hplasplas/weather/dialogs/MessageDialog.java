
/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * This file is part of weather
 *
 *     weather is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *    weather is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with weather  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hplasplas.weather.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;

import com.hplasplas.weather.R;

public class MessageDialog extends AppCompatDialogFragment {
    
    public static final String DIALOG_MESSAGE_TAG = "DialogMessage";
    public static final String ICON_DIALOG_TAG = "DialogIcon";
    public static final String BODY_DIALOG_TAG = "DialogBody";
    
    public static MessageDialog newInstance(String message, int icon, int bodyId) {
        
        MessageDialog dialog = new MessageDialog();
        Bundle args = new Bundle();
        args.putInt(ICON_DIALOG_TAG, icon);
        args.putInt(BODY_DIALOG_TAG, bodyId);
        args.putString(DIALOG_MESSAGE_TAG, message);
        dialog.setArguments(args);
        return dialog;
    }
    
    public static MessageDialog newInstance(String message, int icon) {
        
        MessageDialog dialog = new MessageDialog();
        Bundle args = new Bundle();
        args.putInt(ICON_DIALOG_TAG, icon);
        args.putInt(BODY_DIALOG_TAG, 0);
        args.putString(DIALOG_MESSAGE_TAG, message);
        dialog.setArguments(args);
        return dialog;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MessageDialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        if (getArguments().getInt(BODY_DIALOG_TAG) != 0) {
            builder.setView(inflater.inflate(getArguments().getInt(BODY_DIALOG_TAG), null));
        }
        
        builder.setTitle(getArguments().getString(DIALOG_MESSAGE_TAG))
                .setIcon(getArguments().getInt(ICON_DIALOG_TAG))
                .setPositiveButton(R.string.button_ok, (dialog, id) -> MessageDialog.this.getDialog().cancel());
        return builder.create();
    }
}
