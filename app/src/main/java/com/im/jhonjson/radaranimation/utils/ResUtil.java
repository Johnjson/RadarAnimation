/*
 * Copyright (c) 2018.
 * Author：Zhao
 * Email：joeyzhao1005@gmail.com
 */

package com.im.jhonjson.radaranimation.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.im.jhonjson.radaranimation.app.AppMaster;


/**
 * Created by jhonjson
 * 获取资源的工具类
 */
public class ResUtil {

    public static String getString(@StringRes int resId) {
        return AppMaster.getInstance().getAppContext().getString(resId);
    }

    public static String getString(Context context, @StringRes int resId) {
        return context.getApplicationContext().getString(resId);
    }

    public static String getString(Context context, @StringRes int resId, Object... obj) {
        return context.getApplicationContext().getString(resId, obj);
    }

    public static String getString(@StringRes int resId, Object... obj) {
        return AppMaster.getInstance().getAppContext().getString(resId, obj);
    }


    public static int getColor(Context context, @ColorRes int resId) {
        return ContextCompat.getColor(context.getApplicationContext(), resId);
    }

    public static int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(AppMaster.getInstance().getAppContext(), resId);
    }


    public static Drawable getDrawable(Context context, @DrawableRes int resId) {
        return ContextCompat.getDrawable(context.getApplicationContext(), resId);
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(AppMaster.getInstance().getAppContext(), resId);

    }

    public static int getDimens(Context context, @DimenRes int resId) {
        return context.getApplicationContext().getResources().getDimensionPixelSize(resId);
    }

}
