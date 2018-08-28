package com.im.jhonjson.radaranimation;

import android.app.Application;

import com.im.jhonjson.radaranimation.app.AppMaster;
import com.im.jhonjson.radaranimation.app.IApp;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppMaster.getInstance().setApp(MyApplication.this);
    }
}
