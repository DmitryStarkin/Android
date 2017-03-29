package com.hplasplas.task6.managers;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hplasplas.task6.R;
import com.hplasplas.task6.activitys.CamCapture;
import com.hplasplas.task6.util.HidePanelTask;

import java.util.Arrays;
import java.util.Timer;

import static com.hplasplas.task6.setting.Constants.BOTTOM_PANEL_IDLE;
import static com.hplasplas.task6.setting.Constants.FAB_ANIMATION_DURATION;

/**
 * Created by StarkinDG on 29.03.2017.
 */

public class CollapsedElementsManager implements View.OnTouchListener, HidePanelTask.HideBottomPanelListener {
    
    private Timer mTimer;
    private CamCapture mActivity;
    private TextView mFilesInFolderText;
    private FloatingActionButton mButton;
    private CardView mFilesInFolderTextCard;
    private BottomSheetBehavior<LinearLayout> mBottomSheetBehavior;
    private int[] usedViewId = {R.id.fab_photo, R.id.photo_list_container, R.id.photo_list, R.id.pictureFileDescription,
            R.id.picturePreview, R.id.pictureLoadBar};
    
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
        LinearLayout bottomPanel = (LinearLayout) mActivity.findViewById(R.id.photo_list_container);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomPanel);
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
            hideBottomPanel();
            setInterfaceElementsScale(0);
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
    
    private void hideBottomPanel() {
        
        stopTimer();
        if (mBottomSheetBehavior != null) {
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
    
    public void setFilesInFolderText(int filesInFolder) {
        
        if (mActivity != null && mFilesInFolderText != null) {
            mFilesInFolderText.setText(mActivity.getResources().getString(R.string.files_in_folder, filesInFolder));
        }
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int id = v.getId();
            if (Arrays.binarySearch(usedViewId, id) < 0) {
                changeBottomPanelVisibility();
                return true;
            } else {
                restartTimer();
                return false;
            }
        }
        return false;
    }
    
    private void restartTimer() {
        
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.cancel();
        mTimer.purge();
        mTimer.schedule(new HidePanelTask(this), BOTTOM_PANEL_IDLE);
    }
    
    private void stopTimer() {
        
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }
    
    @Override
    public void hidePanel() {
        
        hideBottomPanel();
    }
}