package com.hplasplas.task7.modules;

import android.content.Context;

import com.hplasplas.task7.managers.MessageManager;

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
