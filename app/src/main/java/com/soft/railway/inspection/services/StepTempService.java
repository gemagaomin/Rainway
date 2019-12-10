package com.soft.railway.inspection.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.utils.DataUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StepTempService extends Service {
    public final static String TAG = "Myapp StepTempService";
    private String str="";
    private AMapLocationClient aMapLocationClient=null;
    private AMapLocation mAMapLocation=null;
    private String gpsPath="";
    private String errorPath="";
    PendingIntent mPendingIntent;
    private final IBinder mBinder = new StepTempBinder();
    private ServiceConnection mServiceConnection;

    private AMapLocationListener aMapLocationListener=new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            mAMapLocation=location;
            try{
                String time=formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss");
                if (null != location) {
                    StringBuffer sb = new StringBuffer();
                    //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                    if(location.getErrorCode() == 0){
                        File file=new File(gpsPath);
                        JSONArray jsonArray=null;
                        if(file.exists()){
                            String jsonStr = readJsonFile(gpsPath);
                            if(!TextUtils.isEmpty(jsonStr)){
                                jsonArray = new JSONArray(jsonStr);
                            }
                        }else{
                            jsonArray=new JSONArray();
                        }
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put("longitude",location.getLongitude());
                        jsonObject.put("latitude",location.getLatitude());
                        jsonObject.put("time",time);
                        jsonArray.put(jsonObject);
                        writeJsonFile(jsonArray.toString(),gpsPath);
                        sb.append("定位成功" + "\n");
                        sb.append("定位类型: " + location.getLocationType() + "\n");
                        sb.append("经    度    : " + location.getLongitude() + "\n");
                        sb.append("纬    度    : " + location.getLatitude() + "\n");
                        sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                        sb.append("提供者    : " + location.getProvider() + "\n");
                        sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                        sb.append("角    度    : " + location.getBearing() + "\n");
                        // 获取当前提供定位服务的卫星个数
                        sb.append("星    数    : " + location.getSatellites() + "\n");
                        sb.append("国    家    : " + location.getCountry() + "\n");
                        sb.append("省            : " + location.getProvince() + "\n");
                        sb.append("市            : " + location.getCity() + "\n");
                        sb.append("城市编码 : " + location.getCityCode() + "\n");
                        sb.append("区            : " + location.getDistrict() + "\n");
                        sb.append("区域 码   : " + location.getAdCode() + "\n");
                        sb.append("地    址    : " + location.getAddress() + "\n");
                        sb.append("地    址    : " + location.getDescription() + "\n");
                        sb.append("兴趣点    : " + location.getPoiName() + "\n");
                        //定位完成的时间
                        sb.append("定位时间: " + time + "\n");
                        //解析定位结果，
                    } else {
                        //定位失败
                        sb.append("定位时间: " + time + "\n");
                        sb.append("定位失败" + "\n");
                        sb.append("错误码:" + location.getErrorCode() + "\n");
                        sb.append("错误信息:" + location.getErrorInfo() + "\n");
                        sb.append("错误描述:" + location.getLocationDetail() + "\n");
                    }
                    sb.append("***定位质量报告***").append("\n");
                    sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启":"关闭").append("\n");
                    sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
                    sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
                    sb.append("****************").append("\n");
                    //定位之后的回调时间
                    sb.append("回调时间: " + formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
                    str = sb.toString();
                } else {
                    str="定位时间: " + time + "\n"+"定位失败，loc is null";
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            if(!DataUtil.DEBUG){
                writeTxtFile(str,errorPath);
            }

        }
    };

    private NotificationManager notificationManager = null;

    @SuppressLint("NewApi")
    private Notification buildNotification() {
        Notification.Builder builder = null;
        Notification notification = null;
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            builder = new Notification.Builder(getApplicationContext());
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("干部履责")
                .setContentText("正在后台运行")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(mPendingIntent)
                .setWhen(System.currentTimeMillis());
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        gpsPath=intent.getStringExtra("gps");
        errorPath=intent.getStringExtra("error");
        mPendingIntent= PendingIntent.getService(this,0,intent,0);
        File file=new File(gpsPath);
        File file1=file.getParentFile();
        if(!file1.exists()){
            file1.mkdirs();
        }
        if(aMapLocationClient==null){
            aMapLocationClient=new AMapLocationClient(getApplicationContext());
        }
        aMapLocationClient.enableBackgroundLocation(2001, buildNotification());
        aMapLocationClient.setLocationListener(aMapLocationListener);
        AMapLocationClientOption aMapLocationClientOption=new AMapLocationClientOption();
        aMapLocationClientOption.setInterval(5000);
        aMapLocationClient.setLocationOption(aMapLocationClientOption);
        aMapLocationClient.startLocation();
        return Service.START_STICKY;
    }

    public class StepTempBinder extends Binder {
        StepTempService getService() {
            return StepTempService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(mServiceConnection==null){
            mServiceConnection=new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    //链接上
                    android.util.Log.d("test","StepService:建立链接");
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    //断开链接
                    startService(new Intent(StepTempService.this,StepService.class));
                }
            };
        }
    }

    @Override
    public void onDestroy() {
        aMapLocationClient.stopLocation();
        aMapLocationClient.disableBackgroundLocation(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * 获取GPS状态的字符串
     * @param statusCode GPS状态码
     * @return
     */
    private String getGPSStatusString(int statusCode){
        String str = "";
        switch (statusCode){
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }

    private static SimpleDateFormat sdf = null;

    public  static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }

    /**
     * 读取json文件
     */
    public static String readJsonFile(String path){
        StringBuffer lastJsonStringBuffer =new StringBuffer();
        BufferedReader reader;
        try {
               reader = new BufferedReader(new FileReader(new File(path)));
               String tempString = null;
               // 一次读入一行，直到读入null为文件结束
               while ((tempString = reader.readLine()) != null) {
                   lastJsonStringBuffer.append(tempString);
               }
               reader.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return lastJsonStringBuffer.toString();
    }

    /**
     * 写出json文件
     */
    public static void writeJsonFile(String newJsonString, String path){
        try {
            FileWriter fw = new FileWriter(path);
            PrintWriter out = new PrintWriter(fw);
            out.write(newJsonString);
            out.println();
            fw.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写出json文件
     */
    public static void writeTxtFile(String newJsonString, String path){
        try {
            FileWriter fw = new FileWriter(path,true);
            PrintWriter out = new PrintWriter(fw);
            out.write(newJsonString);
            out.println();
            fw.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
