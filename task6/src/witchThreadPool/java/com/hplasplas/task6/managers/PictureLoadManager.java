package com.hplasplas.task6.managers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.hplasplas.task6.loaders.BitmapInThreadLoader;
import com.hplasplas.task6.util.MainExecutor;

import java.io.File;

import static com.hplasplas.task6.setting.Constants.FILE_NAME_TO_LOAD;
import static com.hplasplas.task6.setting.Constants.FIRST_LOAD_PICTURE_HEIGHT;
import static com.hplasplas.task6.setting.Constants.FIRST_LOAD_PICTURE_WIDTH;
import static com.hplasplas.task6.setting.Constants.LIST_INDEX;
import static com.hplasplas.task6.setting.Constants.MAIN_PICTURE_INDEX;
import static com.hplasplas.task6.setting.Constants.NO_EXISTING_FILE_NAME;
import static com.hplasplas.task6.setting.Constants.PREF_FOR_LAST_FILE_NAME;
import static com.hplasplas.task6.setting.Constants.PREVIEW_PICTURE_HEIGHT;
import static com.hplasplas.task6.setting.Constants.PREVIEW_PICTURE_WIDTH;
import static com.hplasplas.task6.setting.Constants.PREVIEW_SAMPLE_SIZE;
import static com.hplasplas.task6.setting.Constants.REQUESTED_PICTURE_HEIGHT;
import static com.hplasplas.task6.setting.Constants.REQUESTED_PICTURE_WIDTH;
import static com.hplasplas.task6.setting.Constants.REQUESTED_SAMPLE_SIZE;
import static com.hplasplas.task6.setting.Constants.RESIZE_WITH_SAMPLE;

/**
 * Created by StarkinDG on 26.03.2017.
 */

public class PictureLoadManager implements {
    private void beforeLoadMainBitmap() {
        
        mMainPictureLoaded = true;
        mainProgressBar.setVisibility(View.VISIBLE);
    }
    private void loadMainBitmap() {
        
        if (!mCurrentPictureFile.exists() && !mFilesItemList.isEmpty()) {
            mCurrentPictureFile = mFilesItemList.get(mFilesItemList.size() - 1).getPictureFile();
            loadMainBitmap(mCurrentPictureFile.getPath());
        } else if (mFilesItemList.isEmpty()) {
            loadMainBitmap(NO_EXISTING_FILE_NAME);
        }
    }
    
    private File loadMainBitmap(SharedPreferences preferences) {
        
        beforeLoadMainBitmap();
        File mCurrentPictureFile = new File(preferences.getString(PREF_FOR_LAST_FILE_NAME, NO_EXISTING_FILE_NAME));
        MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(mCurrentPictureFile.getPath(), MAIN_PICTURE_INDEX)));
        return mCurrentPictureFile;
    }
    
    private void loadMainBitmap(String fileName) {
        
        beforeLoadMainBitmap();
        MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, MAIN_PICTURE_INDEX)));
    }
    
    private void loadMainBitmap(String fileName, int requestedHeight, int requestedWidth) {
        
        beforeLoadMainBitmap();
        MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, MAIN_PICTURE_INDEX, requestedHeight, requestedWidth)));
    }
    private void loadPreview(String fileName, int index) {
        
        if (RESIZE_WITH_SAMPLE) {
            MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, index, PREVIEW_SAMPLE_SIZE)));
        } else {
            MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, index, PREVIEW_PICTURE_HEIGHT, PREVIEW_PICTURE_WIDTH)));
        }
    }
    private Bundle createBundleBitmap(String fileName, int index) {
        
        return createBundleBitmap(fileName, index, getMainBitmapRequestedHeight(), getMainBitmapRequestedWidth());
    }
    
    private Bundle createBundleBitmap(String fileName, int index, int requestedHeight, int requestedWidth) {
        
        return createBundleBitmap(fileName, index, requestedHeight, requestedWidth, 0);
    }
    
    private Bundle createBundleBitmap(String fileName, int index, int requestedHeight, int requestedWidth, int sampleSize) {
        
        Bundle bundle = new Bundle();
        bundle.putString(FILE_NAME_TO_LOAD, fileName);
        bundle.putInt(LIST_INDEX, index);
        bundle.putInt(REQUESTED_PICTURE_HEIGHT, requestedHeight);
        bundle.putInt(REQUESTED_PICTURE_WIDTH, requestedWidth);
        bundle.putInt(REQUESTED_SAMPLE_SIZE, sampleSize);
        return bundle;
    }
    
    private Bundle createBundleBitmap(String fileName, int index, int sampleSize) {
        
        return createBundleBitmap(fileName, index, 0, 0, sampleSize);
    }
    
}
