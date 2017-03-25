package com.hplasplas.task6.loaders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.hplasplas.task6.util.MainHandler;
import com.starsoft.bmutil.BitmapTools;

import java.lang.ref.WeakReference;

import static com.hplasplas.task6.setting.Constants.BITMAP_ROTATE_ANGLE;
import static com.hplasplas.task6.setting.Constants.FILE_NAME_TO_LOAD;
import static com.hplasplas.task6.setting.Constants.LIST_INDEX;
import static com.hplasplas.task6.setting.Constants.MESSAGE_BITMAP_LOAD;
import static com.hplasplas.task6.setting.Constants.MUST_IMPLEMENT_INTERFACE_MESSAGE;
import static com.hplasplas.task6.setting.Constants.NO_PICTURE_FILE_NAME;
import static com.hplasplas.task6.setting.Constants.REQUESTED_PICTURE_HEIGHT;
import static com.hplasplas.task6.setting.Constants.REQUESTED_PICTURE_WIDTH;
import static com.hplasplas.task6.setting.Constants.REQUESTED_SAMPLE_SIZE;

/**
 * Created by StarkinDG on 15.03.2017.
 */

public class BitmapInThreadLoader implements Runnable {
    
    private final String TAG = getClass().getSimpleName();
    
    private int mIndex;
    private int mRequestedHeight;
    private int mRequestedWidth;
    private int mSampleSize;
    private String mFileName;
    private WeakReference<AppCompatActivity> mActivity;
    private Bitmap mBitmap;
    
    public BitmapInThreadLoader(AppCompatActivity activity, Bundle args) {
        
        mActivity = new WeakReference<>(activity);
        mFileName = args.getString(FILE_NAME_TO_LOAD);
        mRequestedHeight = args.getInt(REQUESTED_PICTURE_HEIGHT);
        mRequestedWidth = args.getInt(REQUESTED_PICTURE_WIDTH);
        mSampleSize = args.getInt(REQUESTED_SAMPLE_SIZE);
        mIndex = args.getInt(LIST_INDEX);
    }
    
    @Override
    public void run() {
        
        BitmapTools bitmapTools = new BitmapTools();
        BitmapFactory.Options currentBitmapOptions;
        if (mRequestedHeight != 0 & mRequestedWidth != 0) {
            currentBitmapOptions = bitmapTools.readBitmapOptionsFromFile(mFileName);
            currentBitmapOptions.inSampleSize = bitmapTools.calculateInSampleSize(currentBitmapOptions, mRequestedWidth, mRequestedHeight);
        } else {
            currentBitmapOptions = new BitmapFactory.Options();
            if(mSampleSize != 0){
                currentBitmapOptions.inSampleSize = mSampleSize;
            }
        }
        mBitmap = bitmapTools.LoadPictureFromFile(mFileName, currentBitmapOptions);
        
        if (mBitmap == null && mActivity.get() != null) {
            mBitmap = bitmapTools.loadPictureFromAssets(mActivity.get().getApplicationContext(), NO_PICTURE_FILE_NAME, currentBitmapOptions);
        }
        if (mBitmap != null ) {
            if (mBitmap.getHeight() < mBitmap.getWidth()) {
                mBitmap = bitmapTools.rotate(mBitmap, BITMAP_ROTATE_ANGLE);
            }
            if (mActivity.get() != null && !mActivity.get().isFinishing()) {
                Message message = MainHandler.getHandler().obtainMessage(MESSAGE_BITMAP_LOAD, this);
                message.sendToTarget();
            }
        }
    }
    
    //TODO to understand maybe it's not necessary
    private void clearReference() {
        
        mBitmap = null;
        mFileName = null;
    }
    
    public void onPostBitmapLoad() {
        
        if (mActivity.get() != null && !mActivity.get().isFinishing()) {
            try {
                BitmapLoaderListener listener = (BitmapLoaderListener) mActivity.get();
                listener.onBitmapLoadFinished(mIndex, mFileName, mBitmap);
            } catch (ClassCastException e) {
                throw new ClassCastException(mActivity.get().toString() + MUST_IMPLEMENT_INTERFACE_MESSAGE);
            } finally {
                clearReference();
            }
        } else {
            if (mBitmap != null) {
                mBitmap.recycle();
            }
            clearReference();
        }
    }
    
    public interface BitmapLoaderListener {
        
        void onBitmapLoadFinished(int index, String fileName, Bitmap bitmap);
    }
}

