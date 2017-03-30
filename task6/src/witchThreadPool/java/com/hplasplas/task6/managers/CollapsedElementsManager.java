package com.hplasplas.task6.managers;

import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hplasplas.task6.R;
import com.hplasplas.task6.activitys.CamCapture;
import com.hplasplas.task6.util.MainHandler;

import static com.hplasplas.task6.setting.Constants.BOTTOM_PANEL_IDLE;
import static com.hplasplas.task6.setting.Constants.DEBUG;
import static com.hplasplas.task6.setting.Constants.FAB_ANIMATION_DURATION;
import static com.hplasplas.task6.setting.Constants.MESSAGE_PANEL_MUST_HIDE;

/**
 * Created by StarkinDG on 29.03.2017.
 */

public class CollapsedElementsManager implements View.OnTouchListener {
    
    private final String TAG = getClass().getSimpleName();
    
    private CamCapture mActivity;
    private TextView mFilesInFolderText;
    private FloatingActionButton mButton;
    private CardView mFilesInFolderTextCard;
    private BottomSheetBehavior<LinearLayout> mBottomSheetBehavior;
    
    public CollapsedElementsManager(CamCapture activity) {
        
        mActivity = activity;
        if (mActivity != null) {
            findViews();
            adjustViews();
        }
    }
    
    private void findViews() {
        
        mFilesInFolderText = (TextView) mActivity.findViewById(R.id.files_in_folder);
        mFilesInFolderTextCard = (CardView) mActivity.findViewById(R.id.files_in_folder_card);
        mButton = (FloatingActionButton) mActivity.findViewById(R.id.fab_photo);
        mButton.setOnTouchListener(this);
        LinearLayout bottomPanel = (LinearLayout) mActivity.findViewById(R.id.photo_list_container);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomPanel);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) mActivity.findViewById(R.id.main_window);
        coordinatorLayout.setOnTouchListener(this);
    }
    
    private void adjustViews() {
        
        if (mButton != null && mBottomSheetBehavior != null) {
            mButton.setOnClickListener(v -> {
                enableButton(false);
                mActivity.makePhoto();
            });
            
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    
                    if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                        mButton.animate().scaleX(0).scaleY(0).setDuration(FAB_ANIMATION_DURATION).start();
                        mFilesInFolderTextCard.animate().scaleX(0).scaleY(0).setDuration(FAB_ANIMATION_DURATION).start();
                    } else if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                        mButton.animate().scaleX(1).scaleY(1).setDuration(FAB_ANIMATION_DURATION).start();
                        mFilesInFolderTextCard.animate().scaleX(1).scaleY(1).setDuration(FAB_ANIMATION_DURATION).start();
                    }
                }
                
                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    
                }
            });
        }
    }
    
    public void enableButton(boolean state) {
        
        if (mButton != null) {
            mButton.setEnabled(state);
        }
    }
    
    private void changeBottomPanelVisibility() {
        
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            showBottomPanel();
        } else {
            hideBottomPanel();
        }
    }
    
    public void hideBottomPanel() {
        
        stopTimer();
        if (mActivity != null && mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
    
    private void showBottomPanel() {
        
        restartTimer();
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
    
    private void setInterfaceElementsScale(float scale) {
        
        mButton.setScaleX(scale);
        mButton.setScaleY(scale);
        mFilesInFolderTextCard.setScaleX(scale);
        mFilesInFolderTextCard.setScaleY(scale);
    }
    
    public void setRightVisibilityInterfaceElements() {
        
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            setInterfaceElementsScale(0);
        } else {
            setInterfaceElementsScale(1);
        }
    }
    
    public void startTimerIfNeed() {
        
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            restartTimer();
        }
    }
    
    public void setFilesInFolderText(int filesInFolder) {
        
        if (mActivity != null && mFilesInFolderText != null) {
            mFilesInFolderText.setText(mActivity.getResources().getString(R.string.files_in_folder, filesInFolder));
        }
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        
        if (DEBUG) {
            Log.d(TAG, "onTouch: ");
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (v.getId() != R.id.fab_photo) {
                changeBottomPanelVisibility();
                return true;
            } else {
                restartTimer();
                return false;
            }
        }
        return false;
    }
    
    public void restartTimer() {
        
        MainHandler handler = MainHandler.getHandler();
        handler.removeMessages(MESSAGE_PANEL_MUST_HIDE);
        Message message = handler.obtainMessage(MESSAGE_PANEL_MUST_HIDE, this);
        handler.sendMessageDelayed(message, BOTTOM_PANEL_IDLE);
    }
    
    public void stopTimer() {
        
        MainHandler.getHandler().removeMessages(MESSAGE_PANEL_MUST_HIDE);
    }
}