package com.hplasplas.task6.loaders;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;

import com.hplasplas.task6.ThisApplication;
import com.hplasplas.task6.util.MainHandler;
import com.starsoft.bmutil.BitmapTools;

import java.lang.ref.WeakReference;

import static com.hplasplas.task6.setting.Constants.BITMAP_ROTATE_ANGLE;
import static com.hplasplas.task6.setting.Constants.FILE_NAME_TO_LOAD;
import static com.hplasplas.task6.setting.Constants.LIST_INDEX;
import static com.hplasplas.task6.setting.Constants.MESSAGE_BITMAP_LOAD;
import static com.hplasplas.task6.setting.Constants.MUST_IMPLEMENT_INTERFACE_MESSAGE;
import static com.hplasplas.task6.setting.Constants.NO_PICTURE_FILE_NAME;
import static com.hplasplas.task6.setting.Constants.REQUESTED_ORIENTATION;
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
    private int requestedOrientation;
    private String mFileName;
    private WeakReference<BitmapLoaderListener> mListener;
    private Bitmap mBitmap;
    
    public BitmapInThreadLoader(BitmapLoaderListener listener, Bundle args) {
        
        mListener = new WeakReference<>(listener);
        mFileName = args.getString(FILE_NAME_TO_LOAD);
        mRequestedHeight = args.getInt(REQUESTED_PICTURE_HEIGHT);
        mRequestedWidth = args.getInt(REQUESTED_PICTURE_WIDTH);
        mSampleSize = args.getInt(REQUESTED_SAMPLE_SIZE);
        requestedOrientation = args.getInt(REQUESTED_ORIENTATION);
        mIndex = args.getInt(LIST_INDEX);
    }
    
    @Override
    public void run() {
        
        if (mListener.get() != null && mListener.get().isRelevant()) {
            BitmapTools bitmapTools = new BitmapTools();
            mBitmap = bitmapTools.LoadPictureFromFile(mFileName, mRequestedWidth, mRequestedHeight, mSampleSize);
            if (mBitmap == null) {
                mBitmap = bitmapTools.loadPictureFromAssets(ThisApplication.getInstance().getApplicationContext(),
                        NO_PICTURE_FILE_NAME, mRequestedWidth, mRequestedHeight, mSampleSize);
            }
            if (mBitmap != null) {
                if ((requestedOrientation == Configuration.ORIENTATION_PORTRAIT && mBitmap.getHeight() < mBitmap.getWidth()) ||
                        (requestedOrientation == Configuration.ORIENTATION_LANDSCAPE && mBitmap.getHeight() > mBitmap.getWidth())) {
                    mBitmap = bitmapTools.rotate(mBitmap, BITMAP_ROTATE_ANGLE);
                }
                if (mListener.get() != null && mListener.get().isRelevant()) {
                    MainHandler handler = MainHandler.getHandler();
                    synchronized (handler) {
                        Message message = MainHandler.getHandler().obtainMessage(MESSAGE_BITMAP_LOAD, this);
                        message.sendToTarget();
                    }
                }
            }
        }
    }
    
    //TODO to understand maybe it's not necessary
    private void clearReference() {
        
        mBitmap = null;
        mFileName = null;
    }
    
    public void onPostBitmapLoad() {
        
        if (mListener.get() != null && mListener.get().isRelevant()) {
            try {
                mListener.get().onBitmapLoadFinished(mIndex, mFileName, mBitmap);
            } catch (ClassCastException e) {
                throw new ClassCastException(mListener.get().toString() + MUST_IMPLEMENT_INTERFACE_MESSAGE);
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
    
    @Override
    public boolean equals(Object obj) {
        
        //TODO to think it may not need to compare listeners
        return (obj instanceof BitmapInThreadLoader) && ((BitmapInThreadLoader) obj).mFileName.equals(this.mFileName) &&
                (((BitmapInThreadLoader) obj).mListener.get() == this.mListener.get());
    }
    
    public interface BitmapLoaderListener {
        
        void onBitmapLoadFinished(int index, String fileName, Bitmap bitmap);
        boolean  isRelevant();
    }
}

