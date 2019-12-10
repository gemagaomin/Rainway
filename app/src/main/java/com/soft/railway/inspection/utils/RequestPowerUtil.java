package com.soft.railway.inspection.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class RequestPowerUtil {
    private static RequestPowerUtil requestPowerUtil;

    private RequestPowerUtil() {
    }
    public static RequestPowerUtil getInstance(){
        if(requestPowerUtil==null){
            synchronized (RequestPowerUtil.class){
                if(requestPowerUtil==null){
                    requestPowerUtil=new RequestPowerUtil();
                }
            }
        }
        return requestPowerUtil;
    }

    public boolean requestAllPower(Activity activity, Context context){
        if(       ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE            )!=PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_WIFI_STATE                 )!=PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,Manifest.permission.CHANGE_WIFI_STATE                 )!=PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,Manifest.permission.REQUEST_INSTALL_PACKAGES           )!=PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION             )!=PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION               )!=PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,Manifest.permission.WAKE_LOCK                          )!=PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE              )!=PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_NETWORK_STATE               )!=PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE                   )!=PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,Manifest.permission.INTERNET                           )!= PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA                            )!= PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS     )!=PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO     )!=PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(context,"android.permission.DOWNLOAD_WITHOUT_NOTIFICATION")!=PERMISSION_GRANTED
            /*||ContextCompat.checkSelfPermission(this,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)!=PERMISSION_GRANTED*/)
        {
            ActivityCompat.requestPermissions(activity,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE         ,
                    Manifest.permission.READ_EXTERNAL_STORAGE          ,
                    Manifest.permission.ACCESS_WIFI_STATE              ,
                    Manifest.permission.CHANGE_WIFI_STATE              ,
                    Manifest.permission.ACCESS_COARSE_LOCATION         ,
                    Manifest.permission.ACCESS_FINE_LOCATION           ,
                    Manifest.permission.INTERNET                       ,
                    Manifest.permission.CAMERA                         ,
                    Manifest.permission.ACCESS_NETWORK_STATE           ,
                    Manifest.permission.READ_PHONE_STATE               ,
                    "android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" ,
                    Manifest.permission.WAKE_LOCK                      ,
                    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS ,
                    Manifest.permission.RECORD_AUDIO/*,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS*/
            },107);
            return false;
        }else{
            return true;
        }
    }

}
