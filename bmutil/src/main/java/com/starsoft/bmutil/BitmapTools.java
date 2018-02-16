/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the «License»);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * //www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an «AS IS» BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        
        //TODO here implement crop
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
    
    public Bitmap LoadPictureFromFile(String fileName, int reqWidth, int reqHeight, int sampleSize) {
        
        return LoadPictureFromFile(fileName, createBitmapOptions(fileName, reqWidth, reqHeight, sampleSize));
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
    
    public Bitmap loadPictureFromAssets(Context context, String fileName, int reqWidth, int reqHeight, int sampleSize){
       
        return loadPictureFromAssets(context, fileName, createBitmapOptions(fileName, reqWidth, reqHeight, sampleSize));
    }
    
    private BitmapFactory.Options createBitmapOptions(String fileName, int reqWidth, int reqHeight, int sampleSize){
        
        BitmapFactory.Options currentBitmapOptions;
        if (reqHeight != 0 & reqWidth != 0) {
            currentBitmapOptions = readBitmapOptionsFromFile(fileName);
            currentBitmapOptions.inSampleSize = calculateInSampleSize(currentBitmapOptions, reqWidth, reqHeight);
        } else {
            currentBitmapOptions = new BitmapFactory.Options();
            if(sampleSize != 0){
                currentBitmapOptions.inSampleSize = sampleSize;
            }
        }
        return currentBitmapOptions;
    }
    /**
     * load bitmap from InputStream witch default options
     */
    public Bitmap loadPictureFromInputStream(String fileName, InputStream inputStream) {
        
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        return loadPictureFromInputStream(fileName, inputStream, bitmapOptions);
    }
}
