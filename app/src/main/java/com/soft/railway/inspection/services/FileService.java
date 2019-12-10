package com.soft.railway.inspection.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.AreaModel;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.LineModel;
import com.soft.railway.inspection.models.PersonModel;
import com.soft.railway.inspection.models.PointItemModel;
import com.soft.railway.inspection.models.StationModel;
import com.soft.railway.inspection.models.TrainTypeModel;
import com.soft.railway.inspection.models.UnitModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.Log;
import com.soft.railway.inspection.utils.MyApplication;
import com.soft.railway.inspection.utils.StringUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FileService extends Service {
    private DataUtil dataUtil;
    private HttpUtil httpUtil;
    private FileUtil fileUtil;
    private DBUtil dbUtil;
    private final Timer timer = new Timer();
    private boolean result=true;
    private MyServiceConnection mServiceConnection;
    private NotificationManager notificationManager = null;
    private Binder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mBinder == null) {
            mBinder = new FileService.FileBinder();
        }
        mServiceConnection = new FileService.MyServiceConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dataUtil=DataUtil.getInstance();
        fileUtil=FileUtil.getInstance();
        dbUtil=DBUtil.getInstance();
        httpUtil=HttpUtil.getInstance();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                startFile();
                getVersionData();
            }
        });
        thread.start();
        FileService.this.bindService(new Intent(FileService.this,FileTempService.class),mServiceConnection, Context.BIND_IMPORTANT);
        Notification.Builder builder;
        Notification notification ;
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            builder = new Notification.Builder(getApplicationContext());
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        builder.setTicker("守护服务FileService启动中")
                .setContentText("我是来守护服务FileTempService的")
                .setContentTitle("守护服务FileService")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            notification= builder.getNotification();
        }
        startForeground(startId,notification);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        result=false;
        timer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        {
            return mBinder;
        }
    }

    public class FileBinder extends Binder {
        FileService getService() {
            return FileService.this;
        }
    }

    public void startFile(){
        while (result){
            upDateFile();
        }
    }

    public void upDateFile(){
        if(dataUtil==null)
            dataUtil=DataUtil.getInstance();
        if(fileUtil==null)
            fileUtil=FileUtil.getInstance();
        if(httpUtil==null)
            httpUtil=HttpUtil.getInstance();
        if(dbUtil==null)
            dbUtil=DBUtil.getInstance();
        List<FileModel> list = dataUtil.getFileModelList();
        if(list !=null&& list.size()>0){
            FileModel fileModel= list.get(0);
            String str=httpUtil.synchFile(fileModel);
            if(!TextUtils.isEmpty(str)){
                if("0".equals(str)){
                    dataUtil.deleteFileModel(fileModel.getFileId());
                    String fileType=fileModel.getFileType();
                    String filePath=fileModel.getFilePath();
                    if(FileUtil.FILE_TYPE_VIDEO.equals(fileType)){
                        filePath=filePath.replace(".jpg",".mp4");
                    }
                    fileUtil.deleteFile(fileModel.getFilePath());
                    fileUtil.deleteFile(filePath);
                    dbUtil.delete(DataUtil.TableNameEnum.SUBMITFILE.toString()," fileid=? ",new String[]{fileModel.getFileId()});
                    list =dataUtil.getFileModelList();
                }
            }
        }
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            android.util.Log.d("test","FileService:建立链接");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            FileService.this.startService(new Intent(FileService.this,FileTempService.class));
            FileService.this.bindService(new Intent(FileService.this,FileTempService.class),mServiceConnection, Context.BIND_IMPORTANT);
        }
    }

    public void getVersionData(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Map map = new HashMap();
                UserModel userModel = dataUtil.getUser();
                if (userModel != null) {
                    map.put("userId", userModel.getUserId());
                    Map parasMap = new HashMap();
                    parasMap.put("data", JSONObject.toJSONString(map));
                    Map<String, String> resultMap = httpUtil.synch("/app/getuserversion", httpUtil.TYPE_POST, parasMap);
                    String error = resultMap.get("error");
                    String request = resultMap.get("result");
                    if ("0".equals(error)) {
                        if (!TextUtils.isEmpty(request)) {
                            JSONObject jsonObject = JSONObject.parseObject(request);
                            String errorCode = jsonObject.getString("errorCode");
                            if ("0".equals(errorCode)) {
                                SharedPreferences versionPreferences = getSharedPreferences(MyApplication.DATA_VERSION, Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = versionPreferences.edit();
                                String personVersion = jsonObject.getString("personVersion");
                                String routeVersion = jsonObject.getString("routeVersion");
                                String areaVersion = jsonObject.getString("areaVersion");
                                String unitVersion = jsonObject.getString("unitVersion");
                                String pointVersion = jsonObject.getString("pointVersion");
                                String trainTypeVersion = jsonObject.getString("trainTypeVersion");
                                Map<String, String> versionMap = dataUtil.getVersionMap();
                                if (getVersionChange("personVersion", personVersion, versionMap)) {
                                    Map<String, String> personMap = httpUtil.synch("/app/getpersons", httpUtil.TYPE_POST, parasMap);
                                    String personError = personMap.get("error");
                                    String personRequest = personMap.get("result");
                                    Map<String, PersonModel> driverMap = new HashMap<String, PersonModel>();
                                    List<PersonModel> driverList = new ArrayList<PersonModel>();
                                    if ("0".equals(personError)) {
                                        if (!TextUtils.isEmpty(personRequest)) {
                                            JSONObject personJsonObject = JSONObject.parseObject(personRequest);
                                            String errorCodePerson = personJsonObject.getString("errorCode");
                                            if ("0".equals(errorCodePerson)) {
                                                JSONArray personJsonArray = personJsonObject.getJSONArray("persons");
                                                if (personJsonArray != null) {
                                                    int num = personJsonArray.size();
                                                    if (num > 0) {
                                                        String driver = DataUtil.TableNameEnum.PERSON.toString();
                                                        dbUtil.deleteAll(driver);
                                                        dbUtil.db.beginTransaction();
                                                        for (int i = 0; i < num; i++) {
                                                            PersonModel personModel = personJsonArray.getObject(i, PersonModel.class);
                                                            String Id = personModel.getPersonId();
                                                            driverMap.put(Id, personModel);
                                                            driverList.add(personModel);
                                                            dbUtil.db.insert(driver, null, personModel.getContentValues(personModel));
                                                        }
                                                        dbUtil.db.setTransactionSuccessful();
                                                        dbUtil.db.endTransaction();
                                                    }
                                                }
                                                dataUtil.setDriverMap(driverMap);
                                                dataUtil.setDriverList(driverList);
                                            }
                                        }
                                    }
                                }
                                if (getVersionChange("routeVersion", routeVersion, versionMap)) {
                                    Map<String, String> routeStationMap = httpUtil.synch("/app/getroutestations", httpUtil.TYPE_POST, parasMap);
                                    String routeStationError = routeStationMap.get("error");
                                    String routeStationRequest = routeStationMap.get("result");
                                    if ("0".equals(routeStationError)) {
                                        if (!TextUtils.isEmpty(routeStationRequest)) {
                                            JSONObject routeStationJsonObject = JSONObject.parseObject(routeStationRequest);
                                            String errorCodeRouteStation = routeStationJsonObject.getString("errorCode");
                                            if ("0".equals(errorCodeRouteStation)) {
                                                JSONArray routeStationJsonArray = routeStationJsonObject.getJSONArray("lines");
                                                if (routeStationJsonArray != null) {
                                                    Map<String, LineModel> lineMap = new HashMap<String, LineModel>();
                                                    List<LineModel> lineList = new ArrayList<LineModel>();
                                                    int num = routeStationJsonArray.size();
                                                    if (num > 0) {
                                                        String line = DataUtil.TableNameEnum.LINE.toString();
                                                        dbUtil.deleteAll(line);
                                                        dbUtil.db.beginTransaction();
                                                        for (int i = 0; i < num; i++) {
                                                            LineModel lineModel = routeStationJsonArray.getObject(i, LineModel.class);
                                                            String Id = lineModel.getLineId();
                                                            lineMap.put(Id, lineModel);
                                                            lineList.add(lineModel);
                                                            dbUtil.db.insert(line, null, lineModel.getContentValues(lineModel));
                                                        }
                                                        dbUtil.db.setTransactionSuccessful();
                                                        dbUtil.db.endTransaction();
                                                    }
                                                    dataUtil.setLineList(lineList);
                                                    dataUtil.setLineMap(lineMap);
                                                }
                                                JSONArray stationJsonArray = routeStationJsonObject.getJSONArray("stations");
                                                if (stationJsonArray != null) {
                                                    Map<String, StationModel> stationMap = new HashMap<String, StationModel>();
                                                    List<StationModel> stationModelList = new ArrayList<StationModel>();
                                                    int num = stationJsonArray.size();
                                                    if (num > 0) {
                                                        String station = DataUtil.TableNameEnum.STATION.toString();
                                                        dbUtil.deleteAll(station);
                                                        dbUtil.db.beginTransaction();
                                                        for (int i = 0; i < num; i++) {
                                                            StationModel stationModel = stationJsonArray.getObject(i, StationModel.class);
                                                            String Id = stationModel.getStationId();
                                                            stationMap.put(Id, stationModel);
                                                            stationModelList.add(stationModel);
                                                            dbUtil.db.insert(station, null, stationModel.getContentValues(stationModel));
                                                        }
                                                        dbUtil.db.setTransactionSuccessful();
                                                        dbUtil.db.endTransaction();
                                                    }
                                                    dataUtil.setStationList(stationModelList);
                                                    dataUtil.setStationMap(stationMap);
                                                }

                                            }
                                        }
                                    }
                                }
                                if (getVersionChange("areaVersion", areaVersion, versionMap)) {
                                    Map<String, String> areaResultMap = httpUtil.synch("/app/getareas", httpUtil.TYPE_POST, parasMap);
                                    String areaError = areaResultMap.get("error");
                                    String areaRequest = areaResultMap.get("result");
                                    Map<String, AreaModel> areaMap = new HashMap<String, AreaModel>();
                                    List<AreaModel> areaList = new ArrayList<AreaModel>();
                                    if ("0".equals(areaError)) {
                                        if (!TextUtils.isEmpty(areaRequest)) {
                                            JSONObject areaJsonObject = JSONObject.parseObject(areaRequest);
                                            String errorCodeArea = areaJsonObject.getString("errorCode");
                                            if ("0".equals(errorCodeArea)) {
                                                JSONArray areaJsonArray = areaJsonObject.getJSONArray("areas");
                                                if (areaJsonArray != null) {
                                                    int num = areaJsonArray.size();
                                                    if (num > 0) {
                                                        String area = DataUtil.TableNameEnum.AREA.toString();
                                                        dbUtil.deleteAll(area);
                                                        dbUtil.db.beginTransaction();
                                                        for (int i = 0; i < num; i++) {
                                                            AreaModel areaModel = areaJsonArray.getObject(i, AreaModel.class);
                                                            String Id = areaModel.getAreaId();
                                                            areaMap.put(Id, areaModel);
                                                            areaList.add(areaModel);
                                                            dbUtil.db.insert(area, null, areaModel.getContentValues(areaModel));
                                                        }
                                                        dbUtil.db.setTransactionSuccessful();
                                                        dbUtil.db.endTransaction();
                                                    }
                                                }
                                                dataUtil.setAreaMap(areaMap);
                                                dataUtil.setAreaList(areaList);
                                            }
                                        }
                                    }

                                }
                                if (getVersionChange("unitVersion", unitVersion, versionMap)) {
                                    Map<String, String> unitResultMap = httpUtil.synch("/app/getunits", httpUtil.TYPE_POST, parasMap);
                                    String unitError = unitResultMap.get("error");
                                    String unitRequest = unitResultMap.get("result");
                                    Map<String, UnitModel> unitMap = new HashMap<>();
                                    List<UnitModel> unitList = new ArrayList<>();
                                    if ("0".equals(unitError)) {
                                        if (!TextUtils.isEmpty(unitRequest)) {
                                            JSONObject unitJsonObject = JSONObject.parseObject(unitRequest);
                                            String errorCodeUnit = unitJsonObject.getString("errorCode");
                                            if ("0".equals(errorCodeUnit)) {
                                                JSONArray unitJsonArray = unitJsonObject.getJSONArray("units");
                                                if (unitJsonArray != null) {
                                                    int num = unitJsonArray.size();
                                                    if (num > 0) {
                                                        String unit = DataUtil.TableNameEnum.UNIT.toString();
                                                        dbUtil.deleteAll(unit);
                                                        dbUtil.db.beginTransaction();
                                                        for (int i = 0; i < num; i++) {
                                                            UnitModel unitModel = unitJsonArray.getObject(i, UnitModel.class);
                                                            String Id = unitModel.getgId();
                                                            unitMap.put(Id, unitModel);
                                                            unitList.add(unitModel);
                                                            dbUtil.db.insert(unit, null, unitModel.getContentValues(unitModel));
                                                        }
                                                        dbUtil.db.setTransactionSuccessful();
                                                        dbUtil.db.endTransaction();
                                                    }
                                                }
                                                dataUtil.setUnitMap(unitMap);
                                                dataUtil.setUnitList(unitList);
                                            }
                                        }
                                    }
                                }
                                if (getVersionChange("pointVersion", pointVersion, versionMap)) {
                                    Map<String, String> pointItemResultMap = httpUtil.synch("/app/getpointitems", httpUtil.TYPE_POST, parasMap);
                                    String pointItemError = pointItemResultMap.get("error");
                                    String pointRequest = pointItemResultMap.get("result");
                                    Map<String, PointItemModel> pointItemMap = new HashMap<>();
                                    List<PointItemModel> pointItemList = new ArrayList<>();
                                    if ("0".equals(pointItemError)) {
                                        if (!TextUtils.isEmpty(pointRequest)) {
                                            JSONObject pointItemJsonObject = JSONObject.parseObject(pointRequest);
                                            String errorCodePointItem = pointItemJsonObject.getString("errorCode");
                                            if ("0".equals(errorCodePointItem)) {
                                                JSONArray pointItemJsonArray = pointItemJsonObject.getJSONArray("pointitems");
                                                if (pointItemJsonArray != null) {
                                                    int num = pointItemJsonArray.size();
                                                    if (num > 0) {
                                                        String pointItem = DataUtil.TableNameEnum.POINTITEM.toString();
                                                        dbUtil.deleteAll(pointItem);
                                                        dbUtil.db.beginTransaction();
                                                        for (int i = 0; i < num; i++) {
                                                            PointItemModel pointItemModel = pointItemJsonArray.getObject(i, PointItemModel.class);
                                                            String Id = pointItemModel.getItemTypeId();
                                                            pointItemMap.put(Id, pointItemModel);
                                                            pointItemList.add(pointItemModel);
                                                            dbUtil.db.insert(pointItem, null, pointItemModel.getContentValues(pointItemModel));
                                                        }
                                                        dbUtil.db.setTransactionSuccessful();
                                                        dbUtil.db.endTransaction();
                                                    }
                                                }
                                                dataUtil.setPointMap(pointItemMap);
                                                dataUtil.setPointList(pointItemList);
                                            }
                                        }
                                    }
                                }
                                if (getVersionChange("trainTypeVersion", trainTypeVersion, versionMap)) {
                                    Map<String, String> trainTypeResultMap = httpUtil.synch("/app/gettraintypes", httpUtil.TYPE_POST, parasMap);
                                    String trainTypeError = trainTypeResultMap.get("error");
                                    String trainTypeRequest = trainTypeResultMap.get("result");
                                    Map<String, TrainTypeModel> trainTypeMap = new HashMap<>();
                                    List<TrainTypeModel> trainTypeList = new ArrayList<>();
                                    if ("0".equals(trainTypeError)) {
                                        if (!TextUtils.isEmpty(trainTypeRequest)) {
                                            JSONObject trainTypeJsonObject = JSONObject.parseObject(trainTypeRequest);
                                            String errorCodeTrainType = trainTypeJsonObject.getString("errorCode");
                                            if ("0".equals(errorCodeTrainType)) {
                                                JSONArray trainTypeJsonArray = trainTypeJsonObject.getJSONArray("traintypes");
                                                if (trainTypeJsonArray != null) {
                                                    int num = trainTypeJsonArray.size();
                                                    if (num > 0) {
                                                        String trainType = DataUtil.TableNameEnum.TRAIN_TYPE.toString();
                                                        dbUtil.deleteAll(trainType);
                                                        dbUtil.db.beginTransaction();
                                                        for (int i = 0; i < num; i++) {
                                                            TrainTypeModel trainTypeModel = trainTypeJsonArray.getObject(i, TrainTypeModel.class);
                                                            String Id = trainTypeModel.getTrainTypeId();
                                                            trainTypeMap.put(Id, trainTypeModel);
                                                            trainTypeList.add(trainTypeModel);
                                                            dbUtil.db.insert(trainType, null, trainTypeModel.getContentValues(trainTypeModel));
                                                        }
                                                        dbUtil.db.setTransactionSuccessful();
                                                        dbUtil.db.endTransaction();
                                                    }
                                                }
                                                dataUtil.setTrainTypeMap(trainTypeMap);
                                                dataUtil.setTrainTypeList(trainTypeList);
                                            }
                                        }
                                    }
                                }
                                editor.putString("personVersion", personVersion);
                                editor.putString("routeVersion", routeVersion);
                                editor.putString("areaVersion", areaVersion);
                                editor.putString("unitVersion", unitVersion);
                                editor.putString("pointVersion", pointVersion);
                                editor.putString("trainTypeVersion", trainTypeVersion);
                                editor.commit();
                                dataUtil.setVersionMap(versionPreferences);
                            }
                        }
                    }
                }
            }
        };
        timer.schedule(task, 0, 300000);
    }

    public boolean getVersionChange(String key,String str,Map<String,String> versionMap){
        if(versionMap==null||versionMap.get(key)==null){
            return false;
        }
        if(TextUtils.isEmpty(versionMap.get(key))){
            return true;
        }
        boolean result= StringUtil.RESULT_TRUE.equals(StringUtil.CompareDateSize(str,versionMap.get(key)));
        return result;
    }
}
