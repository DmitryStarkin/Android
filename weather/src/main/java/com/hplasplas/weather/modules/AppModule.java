/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
 */
package com.hplasplas.weather.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by StarkinDG on 15.04.2017.
 */

@Module
public class AppModule {
    
    private Context appContext;
    
    public AppModule(@NonNull Context context){
        appContext = context;
    }
    
    @Provides
    @Singleton
    Context provideContext(){
        return appContext;
    }
}
