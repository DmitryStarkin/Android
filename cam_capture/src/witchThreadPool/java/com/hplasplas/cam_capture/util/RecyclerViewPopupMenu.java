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
package com.hplasplas.cam_capture.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by StarkinDG on 27.03.2017.
 */

public class RecyclerViewPopupMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener{
    
    private int position;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    
    public RecyclerViewPopupMenu(@NonNull Context context, @NonNull View anchor, int position) {
        
        //TODO replace it when the styles will be correct -> super(context, anchor, Gravity.AXIS_PULL_BEFORE, 0, R.style.PopupMenuStyle);
        super(context, anchor);
        this.position = position;
    }
    
    public void setOnMenuItemClickListener(@Nullable RecyclerViewPopupMenu.OnMenuItemClickListener listener) {
    
        mOnMenuItemClickListener = listener;
        super.setOnMenuItemClickListener(this);
    }
    
    @Override
    public boolean onMenuItemClick(MenuItem item) {
    
        return mOnMenuItemClickListener != null && mOnMenuItemClickListener.onMenuItemClick(item, position);
    }
    
    public interface OnMenuItemClickListener {
        
        boolean onMenuItemClick(MenuItem item, int position);
    }
}
