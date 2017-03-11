package com.hplasplas.task6.Setting;

/**
 * Created by StarkinDG on 12.02.2017.
 */

public final class Constants {
    
    public static final boolean DEBUG = true;
    
    //file system constants
    public static final boolean NEED_PRIVATE_FOLDER = true;
    public static final String NO_PICTURE_FILE_NAME = "noPicture.jpg";
    public static final String PICTURE_FOLDER_NAME = "myPhotoFolder";
    public static final String FILE_NAME_SUFFIX = ".jpg";
    public static final String DEFAULT_FILE_NAME_PREFIX = "";
    public static final String NO_EXISTING_FILE_NAME = "file";
    public static final String TIME_STAMP_PATTERN = "yyyyMMdd_HHmmss";
    
    //preferences constants
    public static final String PREFERENCES_FILE = "myPref";
    public static final String PREF_FOR_LAST_FILE_NAME = "lastPictureName";
    
    //loaders values
    public static final int MAIN_PICTURE_LOADER_ID = 0;
    public static final int PREVIEW_PICTURE_LOADER_START_ID = 1;
    public static final String FILE_NAME_TO_LOAD = "fileNameToLoad";
    public static final String REQUESTED_PICTURE_HEIGHT = "requestedHeight";
    public static final String REQUESTED_PICTURE_WIDTH = "requestedWidth";
    public static final String CROP_TO_ASPECT_RATIO = "cropToAspectRatio";
    
    //preview values
    public static final int PREVIEW_PICTURE_HEIGHT = 192;
    public static final int PREVIEW_PICTURE_WIDTH = 192;
    
    //recycle view values
    public static final String FILE_NOT_EXIST = "NO File";
    
    //request codes
    public static final int GET_PICTURE_REQUEST_CODE = 1;
    
    //dialog value
    public static final String FILE_RENAME_DIALOG_TAG = "renameFileTag";
    public static final String ERROR_DIALOG_TAG = "ErrorTag";
    public static final String MUST_IMPLEMENT_INTERFACE_MESSAGE = " must implement NoticeDialogListener";
    public static final String LIST_FILE_NAME_TAG = "fileName";
    public static final String ERROR_DIALOG_TITLE_TAG = "errorDialogTitle";
}


