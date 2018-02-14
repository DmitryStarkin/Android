/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
 */
package com.hplasplas.weather.modules;

import android.content.Context;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by StarkinDG on 15.04.2017.
 */

@Module
public class PicassoModule {
    @Provides
    @Singleton
    public Picasso provideRetrofit(Context context, Downloader downloader){
        
        return  new Picasso.Builder(context)
                .downloader(downloader)
                .build();
    }
}
