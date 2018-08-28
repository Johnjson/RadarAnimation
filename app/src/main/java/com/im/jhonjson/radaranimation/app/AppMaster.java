package com.im.jhonjson.radaranimation.app;

import android.content.Context;

import com.im.jhonjson.radaranimation.MyApplication;


/**
 * Created by jhonjson
 */

public class AppMaster implements IApp {


    @Override
    public Context getAppContext() {
        if (app == null) {
            return null;
        }
        return app.getApplicationContext();
    }


    public void setApp(MyApplication app) {
        this.app = app;
    }

    private MyApplication app;


    private static AppMaster mInstance;

    private AppMaster() {
    }

    public static AppMaster getInstance() {
        if (mInstance == null) {
            mInstance = new AppMaster();
        }
        return mInstance;
    }
}
