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
