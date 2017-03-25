package com.starsoft.bmutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by StarkinDG on 13.03.2017.
 */

public class BitmapTools {
    
    /**
     * calculate inSampleSize
     * until all sides of picture are less then the requested size
     * taking a biggest size as the height
     */
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        
        final int width = options.outHeight > options.outWidth ? options.outWidth : options.outHeight;
        final int height = options.outHeight > options.outWidth ? options.outHeight : options.outWidth;
        
        int inSampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
            inSampleSize = 2;
            while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    /**
     * calculate inSampleSize for picture from file
     * until all sides of picture are less then the requested size
     * taking a biggest size as the height
     */
    public int calculateInSampleSize(String filename, int reqWidth, int reqHeight) {
        
        BitmapFactory.Options options = readBitmapOptionsFromFile(filename);
        return calculateInSampleSize(options, reqWidth, reqHeight);
    }
    
    /**
     * Read bitmapFactory.Options from image file
     */
    public BitmapFactory.Options readBitmapOptionsFromFile(String filename) {
        
        BitmapFactory.Options targetOptions = new BitmapFactory.Options();
        targetOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, targetOptions);
        targetOptions.inJustDecodeBounds = false;
        return targetOptions;
    }
    
    /**
     * Crop Bitmap to target Aspect Ratio
     */
    public Bitmap cropToAspectRatio(Bitmap bitmap, AspectRatio aspectRatio) {
        
        //TODO
        return bitmap;
    }
    
    /**
     * rotate Bitmap
     */
    public Bitmap rotate(Bitmap bitmap, float degrees) {
        
        Bitmap newBitmap;
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        try {
            newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
        } catch (OutOfMemoryError e) {
            newBitmap = bitmap;
        }
        return newBitmap;
    }
    
    /**
     * reduce Bitmap size Twice (return new BitmapFactory.Options for load)
     */
    private BitmapFactory.Options reduceSizeTwice(BitmapFactory.Options options) {
        
        int inSampleSize = options.inSampleSize;
        if (inSampleSize < 2) {
            inSampleSize = 2;
        } else {
            inSampleSize *= 2;
        }
        options.inSampleSize = inSampleSize;
        return options;
    }
    
    /**
     * load bitmap from file in case OutOfMemoryError reduces the size Twice
     */
    public Bitmap LoadPictureFromFile(String fileName, BitmapFactory.Options bitmapOptions) {
        
        Bitmap newBitmap;
        try {
            newBitmap = BitmapFactory.decodeFile(fileName, bitmapOptions);
        } catch (OutOfMemoryError e) {
            newBitmap = LoadPictureFromFile(fileName, reduceSizeTwice(bitmapOptions));
        }
        return newBitmap;
    }
    
    /**
     * load bitmap from file witch default options
     */
    public Bitmap LoadPictureFromFile(String fileName) {
        
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        return LoadPictureFromFile(fileName, bitmapOptions);
    }
    
    /**
     * load bitmap from InputStream
     */
    public Bitmap loadPictureFromInputStream(String fileName, InputStream inputStream, BitmapFactory.Options bitmapOptions) {
        
        Bitmap newBitmap = null;
        
        try {
            newBitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
        } catch (OutOfMemoryError e) {
            newBitmap = loadPictureFromInputStream(fileName, inputStream, bitmapOptions);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return newBitmap;
    }
    
    /**
     * load bitmap from Assets
     */
    public Bitmap loadPictureFromAssets(Context context, String fileName, BitmapFactory.Options currentBitmapOptions) {
        
        Bitmap newBitmap = null;
        InputStream inputStream = null;
        try {
                inputStream = context.getAssets().open(fileName);
                newBitmap = loadPictureFromInputStream(fileName, inputStream, currentBitmapOptions);
        } catch (IOException e) {
            e.printStackTrace();
            newBitmap = null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return newBitmap;
    }
    
    /**
     * load bitmap from InputStream witch default options
     */
    public Bitmap loadPictureFromInputStream(String fileName, InputStream inputStream) {
        
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        return loadPictureFromInputStream(fileName, inputStream, bitmapOptions);
    }
}
