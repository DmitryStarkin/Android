/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
 */
package com.hplasplas.weather.modules;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Downloader;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by StarkinDG on 15.04.2017.
 */

@Module
public class DownloaderModule {
    
    @Provides
    @Singleton
    public Downloader provideDownloader(Context context){
        
        return  new OkHttp3Downloader(context, Integer.MAX_VALUE);
    }
}
