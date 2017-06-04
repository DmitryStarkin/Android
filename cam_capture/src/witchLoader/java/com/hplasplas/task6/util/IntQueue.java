package com.hplasplas.cam_capture.util;

/**
 * Created by StarkinDG on 09.03.2017.
 */

public class IntQueue {
    
    private final double DEFAULT_RESIZE_FACTOR = 0.5;
    private final int DEFAULT_SIZE = 16;
    private int[] mValues;
    private double mResizeFactor;
    private int mHeadIndex = 0;
    private int mTailIndex = -1;
    private int mSize = 0;
    
    public IntQueue() {
        
        mValues = new int[DEFAULT_SIZE];
        mResizeFactor = DEFAULT_RESIZE_FACTOR;
    }
    
    public IntQueue(int size) {
        
        mValues = new int[size];
        mResizeFactor = DEFAULT_RESIZE_FACTOR;
    }
    
    public IntQueue(int size, double resizeFactor) {
        
        mValues = new int[size];
        this.mResizeFactor = resizeFactor;
    }
    
    public void add(int value) {
        if(mSize == mValues.length){
            resize();
        }
        mTailIndex++;
        if(mTailIndex == mValues.length){
            mTailIndex = 0;
        }
        mValues[mTailIndex] = value;
        mSize++;
    }
    
    public int poll() {
        
        if (isEmpty()) {
            throw new IllegalStateException("Queue Empty");
        }
        int value = mValues[mHeadIndex];
        mSize--;
        if (mHeadIndex == mTailIndex) {
            mTailIndex = -1;
            mHeadIndex = 0;
        } else if (++mHeadIndex == mValues.length) {
            mHeadIndex = 0;
        }
        return value;
    }
    
    public int peek() {
        
        if (isEmpty()) {
            throw new IllegalStateException("Queue Empty");
        }
        return mValues[mHeadIndex];
    }
    
    public int size() {
        
        return mSize;
    }
    
    public boolean isEmpty() {
        
        return size() == 0;
    }
    
    private void resize() {
        
        int[] newSizeValues = new int[(int)(mValues.length + mValues.length * mResizeFactor)];
        for (int i = 0, y = mHeadIndex; i < mSize; i++, y++){
            if (y == mValues.length){
                y = 0;
            }
            newSizeValues[i] = mValues[y];
        }
        mHeadIndex = 0;
        mTailIndex = mSize - 1;
        mValues = newSizeValues;
    }
}
