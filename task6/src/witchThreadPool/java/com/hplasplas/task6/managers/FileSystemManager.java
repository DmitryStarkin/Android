package com.hplasplas.task6.managers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.hplasplas.task6.ThisApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.hplasplas.task6.setting.Constants.DEFAULT_FILE_NAME_PREFIX;
import static com.hplasplas.task6.setting.Constants.FILE_NAME_SUFFIX;
import static com.hplasplas.task6.setting.Constants.NEED_PRIVATE_FOLDER;
import static com.hplasplas.task6.setting.Constants.PICTURE_FOLDER_NAME;
import static com.hplasplas.task6.setting.Constants.TIME_STAMP_PATTERN;

/**
 * Created by StarkinDG on 26.03.2017.
 */

public class FileSystemManager {
    
    
    private File generateFileForPicture() {
        
        String fileName = DEFAULT_FILE_NAME_PREFIX + new SimpleDateFormat(TIME_STAMP_PATTERN, Locale.getDefault()).format(new Date()) + FILE_NAME_SUFFIX;
        return generateFileForPicture(fileName);
    }
    
    private File generateFileForPicture(String fileName) {
        
        return new File(getDirectory().getPath() + "/" + fileName);
    }
    
    private File getDirectory(boolean needPrivate) {
        
        File dir;
        if (needPrivate) {
            dir = ThisApplication.getInstance().getExternalFilesDir(PICTURE_FOLDER_NAME);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && ContextCompat.checkSelfPermission(ThisApplication.getInstance().getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //TODO request permissions in onResume and check it
                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            } else {
                dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PICTURE_FOLDER_NAME);
            }
        }
        if (dir != null && !dir.exists() && !dir.mkdir()) {
            throw new IllegalStateException("Dir create error");
        }
        return dir;
    }
    private File getDirectory() {
        
        //if (mPictureDirectory == null) {
            //mPictureDirectory = getDirectory(NEED_PRIVATE_FOLDER);
        //}
        return getDirectory(NEED_PRIVATE_FOLDER);
   }
}
