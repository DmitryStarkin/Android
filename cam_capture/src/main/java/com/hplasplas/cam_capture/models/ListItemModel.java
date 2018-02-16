/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * This file is part of cam_capture
 *
 *     cam_capture is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *    cam_capture is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cam_capture  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hplasplas.cam_capture.models;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by StarkinDG on 07.03.2017.
 */

public class ListItemModel {
    
    private File mPictureFile;
    private Bitmap mPicturePreview;
    
    public ListItemModel(File pictureFile) {
        
        this.mPictureFile = pictureFile;
    }
    
    public File getPictureFile() {
        
        return mPictureFile;
    }
    
    public void setPictureFile(File pictureFile) {
        
        this.mPictureFile = pictureFile;
    }
    
    public Bitmap getPicturePreview() {
        
        return mPicturePreview;
    }
    
    public void setPicturePreview(Bitmap picturePreview) {
        
        this.mPicturePreview = picturePreview;
    }
    
    public void clearPreview() {
        
            setPicturePreview(null);
    }
}
