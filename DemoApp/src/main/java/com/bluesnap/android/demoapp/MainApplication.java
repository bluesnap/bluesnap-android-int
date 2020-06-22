package com.bluesnap.android.demoapp;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.multidex.MultiDexApplication;

import com.bugfender.sdk.Bugfender;

/**
 * This class is for demo crash reporting and analytics.
 * You don't need to use an Application object to use the SDK.
 */
public class MainApplication extends MultiDexApplication /*Application*/ {

    static HandlerThread handlerThread;
    static Handler mainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        handlerThread = new HandlerThread("MerchantTokenURLConnection");
        handlerThread.start();
        ;
        mainHandler = new Handler(handlerThread.getLooper());

        Bugfender.init(this, "dBGWHcBAnkKI60HcZnnUuQb38M19XVzR", BuildConfig.DEBUG);
        Bugfender.enableCrashReporting();
        Bugfender.enableUIEventLogging(this);
        Bugfender.enableLogcatLogging(); // optional, if you want logs automatically collected from logcat

    }


}