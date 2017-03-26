package com.hplasplas.task6.managers;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hplasplas.task6.loaders.BitmapInThreadLoader;

import static com.hplasplas.task6.setting.Constants.FIRST_LOAD_PICTURE_HEIGHT;
import static com.hplasplas.task6.setting.Constants.FIRST_LOAD_PICTURE_WIDTH;

/**
 * Created by StarkinDG on 26.03.2017.
 */

public class MainImageManager implements BitmapInThreadLoader.BitmapLoaderListener{
    
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private Bitmap mMainBitmap;
    
    public MainImageManager(ImageView imageView, ProgressBar mainProgressBar){
        mImageView = imageView;
        mProgressBar = mainProgressBar;
    }
    
    public void notifyMainBitmapLoad() {
        
        mProgressBar.setVisibility(View.VISIBLE);
    }
    
    public boolean isMainPictureLoaded(){
        return  mProgressBar.isShown();
    }
    
    public int getMainBitmapRequestedWidth() {
        
        int requestedWidth;
        if ((requestedWidth = mImageView.getWidth()) == 0) {
            requestedWidth = FIRST_LOAD_PICTURE_WIDTH;
        }
        return requestedWidth;
    }
    
    public int getMainBitmapRequestedHeight() {
        
        int requestedHeight;
        if ((requestedHeight = mImageView.getHeight()) == 0) {
            requestedHeight = FIRST_LOAD_PICTURE_HEIGHT;
        }
        return requestedHeight;
    }
    
    @Override
    public void onBitmapLoadFinished(int index, String fileName, Bitmap bitmap) {
        if (mMainBitmap != null) {
            mMainBitmap.recycle();
        }
        mMainBitmap = bitmap;
        if (mMainBitmap != null && isRelevantState()) {
            mImageView.setImageBitmap(mMainBitmap);
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }
    private boolean isRelevantState(){
        return mImageView != null && mProgressBar != null;
    }
    public boolean
    
    @Override
    public boolean isRelevant() {
        
        return isRelevantState();
    }
}
