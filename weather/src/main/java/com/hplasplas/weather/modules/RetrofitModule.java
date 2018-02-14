/**
 * Copyright © 2017 Dmitry Starkin Contacts: t0506803080@gmail.com. All rights reserved
 *
 */
package com.hplasplas.weather.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.hplasplas.weather.setting.Constants.BASE_URL;

/**
 * Created by StarkinDG on 15.04.2017.
 */

@Module
public class RetrofitModule {
    
    @Provides
    @Singleton
    public Retrofit provideRetrofit(){
       return  new Retrofit.Builder()
               .baseUrl(BASE_URL)
               .addConverterFactory(ScalarsConverterFactory.create())
               .build();
    }
}
