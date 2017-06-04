package com.hplasplas.weather.modules;

import android.content.Context;

import com.hplasplas.weather.managers.MessageManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by StarkinDG on 25.04.2017.
 */

@Module
public class MessageManagerModule {
    
    @Provides
    @Singleton
    public MessageManager provideMessageManager(Context context){
        
        return  new MessageManager(context);
    }
}
