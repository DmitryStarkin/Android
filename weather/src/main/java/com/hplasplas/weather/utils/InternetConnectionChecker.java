/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * This file is part of weather
 *
 *     weather is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *    weather is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with weather  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hplasplas.weather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by StarkinDG on 07.04.2017.
 */

public class InternetConnectionChecker {
    
    private static final String URL_FOR_CHECKING = "http://www.google.com/";
    private static final int CONNECTION_TIMEOUT = 1000;
    
    private static boolean isConnected(Context context) {
        
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    
    private static NetworkInfo getNetworkInfo(Context context) {
        
        if (isConnected(context)) {
            return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        } else {
            return null;
        }
    }
    
    //TODO in Thread need
    private static boolean isOnline() {
        
        //TODO why this throws an IOException
        
        //try {
           // URL url = new URL(URL_FOR_CHECKING);
           // HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            //urlc.setConnectTimeout(CONNECTION_TIMEOUT);
           // urlc.connect();
           // return urlc.getResponseCode() == HttpURLConnection.HTTP_OK;
        //} catch (SocketTimeoutException e) {
            //e.printStackTrace();
           // return false;
       // } catch (IOException e) {
           // e.printStackTrace();
           // return false;
       // }
        return true;
    }
    
    public static boolean isInternetAvailable(Context context) {
        
        return isConnected(context) && isOnline();
    }
    
    public static boolean isWiFi() {
        //TODO
        return false;
    }
    
    public static boolean isMobile() {
        //TODO
        return false;
    }
}
