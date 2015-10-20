package com.letv.recorder.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.util.List;


public class AppInfo {

    private static AppInfo instance = null;


    public static String screen = "";           // 屏幕信息
    public static float density;                // 屏幕密度
    public static float scaledDensity;
    public static int screenResolution;         // 屏幕分辨率
    public static int screenWidthForPortrait;   // 屏幕宽度
    public static int screenHeightForPortrait;  // 屏幕高度
    public static int screenStatusBarHeight;    //屏幕通知栏高度


    private AppInfo() {

    }


    public synchronized static AppInfo initApp() {
        if (instance == null) {
            instance = new AppInfo();
        }
        return instance;
    }


    public void initScreenInfo(Activity activity) {
        if (density != 0) {
            return;
        }
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        density = metric.density;
        scaledDensity = metric.scaledDensity;
        screen = "" + metric.widthPixels + "*" + metric.heightPixels;
        screenResolution = metric.widthPixels * metric.heightPixels;
        if (metric.heightPixels >= metric.widthPixels) {
            screenWidthForPortrait = metric.widthPixels;
            screenHeightForPortrait = metric.heightPixels;
        } else {
            screenWidthForPortrait = metric.heightPixels;
            screenHeightForPortrait = metric.widthPixels;
        }
        screenStatusBarHeight = Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
    }


}
