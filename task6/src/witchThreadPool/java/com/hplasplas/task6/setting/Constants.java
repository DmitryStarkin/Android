package com.hplasplas.task6.setting;

import com.hplasplas.task6.BuildConfig;

import java.util.concurrent.TimeUnit;

/**
 * Created by StarkinDG on 12.02.2017.
 */

public final class Constants {
    
    public static final boolean DEBUG = BuildConfig.DEBUG;
    
    //file system constants
    public static final boolean NEED_PRIVATE_FOLDER = true;
    public static final String NO_PICTURE_FILE_NAME = "noPicture.jpg";
    public static final String PICTURE_FOLDER_NAME = "myPhotoFolder";
    public static final String FILE_NAME_SUFFIX = ".jpg";
    public static final String DEFAULT_FILE_NAME_PREFIX = "";
    public static final String NO_EXISTING_FILE_NAME = "";
    public static final String TIME_STAMP_PATTERN = "yyyyMMdd_HHmmss";
    
    //preferences constants
    public static final String PREFERENCES_FILE = "myPref";
    public static final String PREF_FOR_LAST_FILE_NAME = "lastPictureName";
    
    //loaders values
    public static final int MAIN_PICTURE_INDEX = -1;
    public static  final float BITMAP_ROTATE_ANGLE = 90;
    public static final String FILE_NAME_TO_LOAD = "fileNameToLoad";
    public static final String REQUESTED_PICTURE_HEIGHT = "requestedHeight";
    public static final String REQUESTED_PICTURE_WIDTH = "requestedWidth";
    public static final String LIST_INDEX = "listIndex";
    
    //preview values
    public static final int PREVIEW_PICTURE_HEIGHT = 103;
    public static final int PREVIEW_PICTURE_WIDTH = 77;
    
    //main bitmap values
    public static final int FIRST_LOAD_PICTURE_WIDTH = 540;
    public static final int FIRST_LOAD_PICTURE_HEIGHT = 720;
    
    //recycle view values
    public static final String FILE_NOT_EXIST = "NO File";
    public static final int ROWS_IN_TABLE = 2;
    
    //request codes
    public static final int GET_PICTURE_REQUEST_CODE = 1;
    public static final int PERMISSION_REQUEST_CODE = 0;
    
    //tread Pool values
    public static final int POSSESSORS_MULTIPLIER = 2;
    public static final long THREAD_IDLE_TIME = 30;
    public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    public static final int THREADS_PRIORITY = 8;
    public static final String THREAD_NAME_PREFIX = "BitmapLoader";
    public static final int MESSAGE_BITMAP_LOAD = 1;
    public static final int QUEUE_CAPACITY = 32;
    public static final double MAX_POOL_SIZE_MULTIPLIER = 0.5;
    
    //dialog value
    public static final String FILE_RENAME_DIALOG_TAG = "renameFileTag";
    public static final String ERROR_DIALOG_TAG = "ErrorTag";
    public static final String MUST_IMPLEMENT_INTERFACE_MESSAGE = " must implement NoticeDialogListener";
    public static final String LIST_FILE_NAME_TAG = "fileName";
    public static final String ERROR_DIALOG_TITLE_TAG = "errorDialogTitle";
}


