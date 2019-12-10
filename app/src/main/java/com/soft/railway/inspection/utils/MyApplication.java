package com.soft.railway.inspection.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.services.StepService;
import com.soft.railway.inspection.services.StepTempService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    public static final String USER_NAME="user";
    public static final String DATA_VERSION="dataVersion";
    public static final String DATA_IP="ip";
    public static List<Activity> list=new ArrayList<>();
    public  static MyApplication instance = null;
    private String pathStep="";
    private String fileName=".gps";
    private String errorFileName=".txt";
    public  static String gpsFileName="";

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        Fresco.initialize(this);
    }
    /**
     * 隐藏软键盘(可用于Activity)
     */
    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public List<Activity> getList() {
        return list;
    }

    public void setList(List<Activity> list) {
        this.list = list;
    }

    public static void addActivity(Activity activity){
        list.add(activity);
    }

    public static void removeActivity(Activity activity){
        list.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity:list){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    public String begin(String workId){
        pathStep= DataUtil.GPS_PATH;
        gpsFileName=workId+fileName;
        String gpsFilePath=pathStep+ File.separator+gpsFileName;
        String errorFilePath=pathStep+File.separator+workId+errorFileName;
        Intent intent=new Intent(MyApplication.this, StepService.class);
        intent.putExtra("gps",gpsFilePath);
        intent.putExtra("error",errorFilePath);
        startService(intent);
        Intent intentTemp=new Intent(MyApplication.this, StepTempService.class);
        intentTemp.putExtra("gps",gpsFilePath);
        intentTemp.putExtra("error",errorFilePath);
        startService(intentTemp);
        return gpsFileName;
    }

    public void end(WorkPojo workPojo){
        if(!TextUtils.isEmpty(workPojo.getGpsFileName())){
            FileUtil fileUtil=FileUtil.getInstance();
            File file=new File(DataUtil.GPS_PATH+File.separator+gpsFileName);
            FileModel fileModel =new FileModel();
            fileModel.setFileId(gpsFileName);
            fileModel.setFileName(gpsFileName);
            fileModel.setFilePath(file.getAbsolutePath());
            fileModel.setFileStatus(FileUtil.FILE_STATUS_NOT_CAN_UPLOADED);
            fileModel.setFileRank(FileUtil.FILE_RANK_HIGH);
            fileModel.setFileType(FileUtil.FILE_TYPE_GPS);
            fileModel.setItemId("");
            fileModel.setWorkId(workPojo.getWorkId());
            fileModel.setUserId(workPojo.getUserId());
            fileModel.setFileTime(DateTimeUtil.getNewDateShow());
            fileUtil.insertFile(fileModel);
            Intent intent=new Intent(MyApplication.this, StepService.class);
            stopService(intent);
            Intent intentTemp=new Intent(MyApplication.this, StepTempService.class);
            stopService(intentTemp);
        }
    }

    /**
     * 返回当前程序版本号
     */
    public static String getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            // versionName = pi.versionName;
            versioncode = pi.versionCode;
        } catch (Exception e) {
            Log.d("VersionInfo", "Exception", e);
        }
        return versioncode + "";
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName=null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.d("VersionInfo", "Exception", e);
        }
        return versionName;
    }
}
