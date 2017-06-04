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
