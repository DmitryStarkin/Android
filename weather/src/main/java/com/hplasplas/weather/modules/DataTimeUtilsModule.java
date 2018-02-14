/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
 */
package com.hplasplas.weather.modules;

import com.hplasplas.weather.utils.DataTimeUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by StarkinDG on 25.04.2017.
 */

@Module
public class DataTimeUtilsModule {
    
    @Provides
    @Singleton
    public DataTimeUtils provideDataTimeUtils(){
        
        return  new DataTimeUtils();
    }
}
