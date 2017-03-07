package com.hplasplas.task6.Models;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by StarkinDG on 07.03.2017.
 */

public class ListItemModel {
    
    private File pictureFile;
    private Bitmap picturePreview;
    
    public ListItemModel(File pictureFile) {
        
        this.pictureFile = pictureFile;
    }
    
    public File getPictureFile() {
        
        return pictureFile;
    }
    
    public void setPictureFile(File pictureFile) {
        
        this.pictureFile = pictureFile;
    }
    
    public Bitmap getPicturePreview() {
        
        return picturePreview;
    }
    
    public void setPicturePreview(Bitmap picturePreview) {
        
        this.picturePreview = picturePreview;
    }
}
