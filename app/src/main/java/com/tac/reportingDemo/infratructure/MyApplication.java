package com.tac.reportingDemo.infratructure;

import android.app.Application;
import android.content.Context;

import com.tac.reportingDemo.storage.MySharedPreferences;


/**
 * Created by Dell on 11/28/2017.
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static MyApplication mInstance;
    private MySharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;


    }




    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }
}
