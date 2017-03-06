package com.hplasplas.task6.Setting;

/**
 * Created by StarkinDG on 12.02.2017.
 */

public final class Constants {
    public static final boolean DEBUG = true;
    
    //file system constants
    public static final String NO_PICTURE = "noPicture.jpg";
    public static final String PICTURE_FOLDER_NAME = "myPhotoFolder";
    public static final String FILE_NAME_SUFFIX = ".jpg";
    public static final String FILE_NAME_PREFIX = "JPEG_";
    public static final String FIRST_INIT_FILE_NAME = "Name";
    public static final boolean GET_PRIVATE_FOLDER = true;
    
    //preferences constants
    public static final String PREFERENCES_FILE = "myPref";
    public static final String PREF_FOR_LAST_FILE_NAME = "lastPictureName";
    
    //loaders values
    public static final int MAIN_PICTURE_LOADER_ID = 0;
    public static final int PREVIEW_PICTURE_LOADER_ID = 1;
    public static final String ARG_FILE_NAME_TO_LOAD = "fileNameToLoad";
    public static final String ARG_REQUESTED_PICTURE_HEIGHT = "requestedHeight";
    public static final String ARG_REQUESTED_PICTURE_WIDTH = "requestedWidth";
    public static final String ARG_REQUESTED_PICTURE_ASPECT_RATIO = "requestedWidth";
    
    //request codes
    public static final int GET_PICTURE_REQUEST_CODE = 1;
}


