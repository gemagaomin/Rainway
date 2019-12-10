package com.soft.railway.inspection.utils;

import com.soft.railway.inspection.BuildConfig;

public class Log {
    private static String TAG="myApp";
    private static boolean sDebug= BuildConfig.DEBUG;
    public static void d(String msg,Object ...args){
        if(!sDebug)
            return;
        android.util.Log.d(TAG, String.format(msg,args));
    }
}
