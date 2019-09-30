package com.example.android.loa;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

public class LoaApp extends MultiDexApplication {

    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return mContext;
    }
}
