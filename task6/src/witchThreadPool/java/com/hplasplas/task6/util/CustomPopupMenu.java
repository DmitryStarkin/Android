package com.hplasplas.task6.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by StarkinDG on 27.03.2017.
 */

public class CustomPopupMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener{
    
    private int position;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    
    public CustomPopupMenu(@NonNull Context context, @NonNull View anchor, int position) {
        
        super(context, anchor);
        this.position = position;
    }
    
    public void setOnMenuItemClickListener(@Nullable CustomPopupMenu.OnMenuItemClickListener listener) {
    
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
