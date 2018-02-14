/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
 */
package com.hplasplas.cam_capture.setting;

import com.hplasplas.cam_capture.BuildConfig;

import java.util.concurrent.TimeUnit;

/**
 * Created by StarkinDG on 12.02.2017.
 */

public final class Constants {
    
    public static final boolean DEBUG = BuildConfig.DEBUG;
    
    public static final long BOTTOM_PANEL_IDLE_TIME = 10000;
    
    //animation setting
    public static final long PREVIEW_ANIMATION_DURATION = 800;
    public static final long PREVIEW_ANIMATION_START_DELAY = 300;
    public static final long FAB_ANIMATION_DURATION = 300;
    
    //file system constants
    public static final boolean NEED_PRIVATE_FOLDER = true;
    public static final String NO_PICTURE_FILE_NAME = "noPicture.jpg";
    public static final String PICTURE_FOLDER_NAME = "myPhotoFolder";
    public static final String FILE_NAME_SUFFIX = ".jpg";
    public static final String DEFAULT_FILE_NAME_PREFIX = "";
    public static final String NO_EXISTING_FILE_NAME = "";
    public static final String TIME_STAMP_PATTERN = "yyyyMMdd_HHmmss";
    
    //preferences values
    public static final String PREFERENCES_FILE = "myPref";
    public static final String PREF_FOR_LAST_FILE_NAME = "lastPictureName";
    
    //loaders values
    public static final int MAIN_PICTURE_INDEX = -1;
    public static  final float BITMAP_ROTATE_ANGLE = 90;
    public static final String FILE_NAME_TO_LOAD = "fileNameToLoad";
    public static final String REQUESTED_PICTURE_HEIGHT = "requestedHeight";
    public static final String REQUESTED_PICTURE_WIDTH = "requestedWidth";
    public static final String REQUESTED_SAMPLE_SIZE = "requestedSampleSize";
    public static final String REQUESTED_ORIENTATION = "checkOrientation";
    public static final String LIST_INDEX = "listIndex";
    
    //preview values
    public static final int PREVIEW_PICTURE_HEIGHT = 103;
    public static final int PREVIEW_PICTURE_WIDTH = 77;
    public static final int PREVIEW_SAMPLE_SIZE = 32;
    public static final boolean RESIZE_WITH_SAMPLE = false;
    
    //main bitmap values
    public static final int FIRST_LOAD_PICTURE_WIDTH = 720;
    public static final int FIRST_LOAD_PICTURE_HEIGHT = 960;
    
    //recycle view values
    public static final String FILE_NOT_EXIST = "NO File";
    
    //request codes
    public static final int GET_PICTURE_REQUEST_CODE = 1;
    public static final int PERMISSION_REQUEST_CODE = 0;
    
    //tread Pool values
    public static final int THREAD_START_TERM = 0;
    public static final int MIN_THREAD_NUMBER = 2;
    public static final long THREAD_IDLE_TIME = 30;
    public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    public static final int THREADS_PRIORITY = 7;
    public static final String THREAD_NAME_PREFIX = "BitmapLoader";
    public static final int QUEUE_CAPACITY = Integer.MAX_VALUE;
    public static final double POOL_MAX_SIZE_MULTIPLIER = 1.5;
    
    //handler values
    public static final int MESSAGE_BITMAP_LOAD = 1;
    public static final int MESSAGE_PANEL_MUST_HIDE = 2;
    
    //dialog values
    public static final String FILE_RENAME_DIALOG_TAG = "renameFileTag";
    public static final String ERROR_DIALOG_TAG = "ErrorTag";
    public static final String MUST_IMPLEMENT_INTERFACE_MESSAGE = " must implement NoticeDialogListener";
    public static final String LIST_FILE_NAME_TAG = "fileName";
    public static final String ERROR_DIALOG_TITLE_TAG = "errorDialogTitle";
}


