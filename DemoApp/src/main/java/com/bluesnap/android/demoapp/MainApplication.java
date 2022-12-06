package com.bluesnap.android.demoapp;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;

//import androidx.multidex.MultiDexApplication;

/**
 * This class is for demo crash reporting and analytics.
 * You don't need to use an Application object to use the SDK.
 */
public class MainApplication extends Application {

    static HandlerThread handlerThread;
    static Handler mainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        handlerThread = new HandlerThread("MerchantTokenURLConnection");
        handlerThread.start();
        ;
        mainHandler = new Handler(handlerThread.getLooper());

    }


}