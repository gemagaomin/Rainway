package com.soft.railway.inspection.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.View.ShowViewPager;
import com.soft.railway.inspection.fragments.EmergencyDisposalFragment;
import com.soft.railway.inspection.fragments.PersonalHomepageFragment;
import com.soft.railway.inspection.fragments.PointTestingResultFragment;
import com.soft.railway.inspection.fragments.WorkFragment;
import com.soft.railway.inspection.models.AreaModel;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.KeyPersonModel;
import com.soft.railway.inspection.models.LineModel;
import com.soft.railway.inspection.models.PersonModel;
import com.soft.railway.inspection.models.PointItemModel;
import com.soft.railway.inspection.models.StationModel;
import com.soft.railway.inspection.models.TrainTypeModel;
import com.soft.railway.inspection.models.UnitModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.utils.AESUtil;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.DateTimeUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.LoadingDialog;
import com.soft.railway.inspection.utils.Log;
import com.soft.railway.inspection.utils.MyApplication;
import com.soft.railway.inspection.utils.MyException;
import com.soft.railway.inspection.utils.StringUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    private FileUtil fileUtil;
    private HttpUtil httpUtil=null;
    private DBUtil dbUtil=null;
    private DataUtil dataUtil;
    private boolean result=true;
    private List<FileModel> fileList;
    private Thread thread;
    private long time=0;
    private List<KeyPersonModel> list;
    private KeyPersonModel lastMonthModel;
    private KeyPersonModel monthModel;
    private KeyPersonModel quarterModel;
    private KeyPersonModel lastQuarterModel;
    private boolean isMonth=false;
    private boolean isQuarter=false;
    private String url;
    private final Timer timer = new Timer();
    private TimerTask task;
    public  List<Fragment> fragmentList=new ArrayList<Fragment>();
    private ShowViewPager myViewPager;
    private BottomNavigationView myBottomNavigationView;
    private MyReceiver receiver;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(monthModel==null||quarterModel==null){
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this)
                        .setMessage("前往设置关键人界面")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(MainActivity.this,KeyPersonSettingActivity.class);
                                startActivity(intent);
                            }
                        });
                alertDialog.create().show();
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
           /*     case R.id.navigation_home:
                    myViewPager.setCurrentItem(0);
                    return true;*/
                case R.id.navigation_work:
                    myViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_point:
                    myViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_dashboard:
                    myViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            myBottomNavigationView.getMenu().getItem(i).setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };

    private FragmentPagerAdapter myFragmentPagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(thread==null){
            thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    startFile();
                }
            });
        }
        getVersionData();
        if(!thread.isAlive()){
            thread.start();
        }
        hintKeyPerson();
        IntentFilter filter=new IntentFilter("com.soft.railway.inspection.activities.broadcast");
        if(receiver==null){
            receiver=new MyReceiver();
            registerReceiver(receiver,filter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        fileUtil=FileUtil.getInstance();
        dataUtil=DataUtil.getInstance();
        dbUtil=DBUtil.getInstance();
        httpUtil=HttpUtil.getInstance();
        list=new ArrayList<>();
        url="/app/getkeypersons";
        initFragment();
        initView();
        getKeyPersonInit();
        hintKeyPerson();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initView(){
        myBottomNavigationView=(BottomNavigationView) findViewById(R.id.navigation);
        myBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        myViewPager=(ShowViewPager) findViewById(R.id.vp);
        myViewPager.addOnPageChangeListener(mOnPageChangeListener);
        myViewPager.setAdapter(myFragmentPagerAdapter);
    }

    public void initFragment(){
        WorkFragment workFragment=WorkFragment.newInstance("","");
        PointTestingResultFragment pointTestingResultFragment=PointTestingResultFragment.newInstance("","");
        PersonalHomepageFragment personalHomepageFragment=PersonalHomepageFragment.newInstance("","");
        fragmentList.add(workFragment);
        fragmentList.add(pointTestingResultFragment);
        fragmentList.add(personalHomepageFragment);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            return exitBy2Click();      //调用双击退出函数
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean exitBy2Click(){
        if(System.currentTimeMillis()-time>2000){
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            time=System.currentTimeMillis();
        }else{
            MyApplication.finishAll();
        }
        return true;
    }

    public void startFile(){
        while (result){
           upDateFile();
        }
    }

    public void upDateFile(){
        if(dataUtil==null)
            dataUtil=DataUtil.getInstance();
        fileList=dataUtil.getFileModelList();
        if(fileList!=null&&fileList.size()>0){
            FileModel fileModel=fileList.get(0);
            String str=httpUtil.synchFile(fileModel);
            if(!TextUtils.isEmpty(str)){
                if("0".equals(str)){
                    if(fileUtil==null)
                        fileUtil=FileUtil.getInstance();
                    if(httpUtil==null)
                        httpUtil= HttpUtil.getInstance();
                    if(dbUtil==null)
                        dbUtil= DBUtil.getInstance();
                    dataUtil.deleteFileModel(fileModel.getFileId());
                    String fileType=fileModel.getFileType();
                    String filePath=fileModel.getFilePath();
                    if(FileUtil.FILE_TYPE_VIDEO.equals(fileType)){
                        fileUtil.deleteFile(filePath);
                        filePath=DataUtil.VIDEO_PATH+filePath.substring(DataUtil.PHOTO_PATH.length(),filePath.length()-4)+".mp4";
                    }
                    fileUtil.deleteFile(filePath);
                    dbUtil.delete(DataUtil.TableNameEnum.SUBMITFILE.toString()," fileid=? ",new String[]{fileModel.getFileId()});
                    fileList=dataUtil.getFileModelList();
                }else if("2".equals(str)){
                    dataUtil.deleteFileModel(fileModel.getFileId());
                    dbUtil.delete(DataUtil.TableNameEnum.SUBMITFILE.toString()," fileid=? ",new String[]{fileModel.getFileId()});
                    fileList=dataUtil.getFileModelList();
                }
            }
        }

    }

    public void getVersionData(){
        if(task==null){
            task = new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Map map=new HashMap();
                    UserModel userModel=dataUtil.getUser();
                    if(userModel!=null){
                        map.put("userId",userModel.getUserId());
                        Map parasMap=new HashMap();
                        parasMap.put("data", JSONObject.toJSONString(map));
                        Map<String,String> resultMap=httpUtil.synch("/app/getuserversion",httpUtil.TYPE_POST,parasMap);
                        String error=resultMap.get("error");
                        String request=resultMap.get("result");
                        if("0".equals(error)){
                            if(!TextUtils.isEmpty(request)){
                                JSONObject jsonObject=JSONObject.parseObject(request);
                                //TODO 修改时间每个字典表不一致，后期需改
                                String errorCode=jsonObject.getString("errorCode");
                                if("0".equals(errorCode)){
                                    SharedPreferences versionPreferences=getSharedPreferences(MyApplication.DATA_VERSION, Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=versionPreferences.edit();
                                    String personVersion=jsonObject.getString(   "personVersion"           );
                                    String routeVersion=jsonObject.getString(    "routeVersion"            );
                                    String areaVersion=jsonObject.getString(     "areaVersion"             );
                                    String unitVersion=jsonObject.getString(     "unitVersion"             );
                                    String pointVersion=jsonObject.getString(    "pointVersion"            );
                                    String trainTypeVersion=jsonObject.getString("trainTypeVersion"     );
                                    Map<String,String> versionMap=dataUtil.getVersionMap();
                                    if(getVersionChange("personVersion",personVersion,versionMap)){
                                        Map<String,String> personMap=httpUtil.synch("/app/getpersons",httpUtil.TYPE_POST,parasMap);
                                        String personError=personMap.get("error");
                                        String personRequest=personMap.get("result");
                                        Map<String, PersonModel> driverMap = new HashMap<String, PersonModel>();
                                        List<PersonModel> driverList = new ArrayList<PersonModel>();
                                        if("0".equals(personError)){
                                            if(!TextUtils.isEmpty(personRequest)) {
                                                JSONObject personJsonObject = JSONObject.parseObject(personRequest);
                                                String errorCodePerson=personJsonObject.getString("errorCode");
                                                if("0".equals(errorCodePerson)){
                                                    JSONArray personJsonArray = personJsonObject.getJSONArray("persons");
                                                    if (personJsonArray != null) {
                                                        int num = personJsonArray.size();
                                                        if(num>0){
                                                            String driver=DataUtil.TableNameEnum.PERSON.toString();
                                                            dbUtil.deleteAll(driver);
                                                            dbUtil.db.beginTransaction();
                                                            for (int i = 0; i < num; i++) {
                                                                PersonModel personModel = personJsonArray.getObject(i,PersonModel.class);
                                                                String Id = personModel.getPersonId();
                                                                driverMap.put(Id, personModel);
                                                                driverList.add(personModel);
                                                                dbUtil.db.insert(driver,null,personModel.getContentValues(personModel));
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
                                    if(getVersionChange("routeVersion",routeVersion,versionMap)){
                                        Map<String,String> routeStationMap=httpUtil.synch("/app/getroutestations",httpUtil.TYPE_POST,parasMap);
                                        String routeStationError=routeStationMap.get("error");
                                        String routeStationRequest=routeStationMap.get("result");
                                        if("0".equals(routeStationError)){
                                            if(!TextUtils.isEmpty(routeStationRequest)) {
                                                JSONObject routeStationJsonObject = JSONObject.parseObject(routeStationRequest);
                                                String errorCodeRouteStation=routeStationJsonObject.getString("errorCode");
                                                if("0".equals(errorCodeRouteStation)){
                                                    JSONArray routeStationJsonArray = routeStationJsonObject.getJSONArray("lines");
                                                    if (routeStationJsonArray != null) {
                                                        Map<String, LineModel> lineMap = new HashMap<String, LineModel>();
                                                        List<LineModel> lineList = new ArrayList<LineModel>();
                                                        int num = routeStationJsonArray.size();
                                                        if(num>0){
                                                            String line=DataUtil.TableNameEnum.LINE.toString();
                                                            dbUtil.deleteAll(line);
                                                            dbUtil.db.beginTransaction();
                                                            for (int i = 0; i < num; i++) {
                                                                LineModel lineModel = routeStationJsonArray.getObject(i,LineModel.class);
                                                                String Id = lineModel.getLineId();
                                                                lineMap.put(Id, lineModel);
                                                                lineList.add(lineModel);
                                                                dbUtil.db.insert(line,null,lineModel.getContentValues(lineModel));
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
                                                        if(num>0){
                                                            String station=DataUtil.TableNameEnum.STATION.toString();
                                                            dbUtil.deleteAll(station);
                                                            dbUtil.db.beginTransaction();
                                                            for (int i = 0; i < num; i++) {
                                                                StationModel stationModel = stationJsonArray.getObject(i,StationModel.class);
                                                                String Id = stationModel.getStationId();
                                                                stationMap.put(Id, stationModel);
                                                                stationModelList.add(stationModel);
                                                                dbUtil.db.insert(station,null,stationModel.getContentValues(stationModel));
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
                                    if(getVersionChange("areaVersion",areaVersion,versionMap)){
                                        Map<String,String> areaResultMap=httpUtil.synch("/app/getareas",httpUtil.TYPE_POST,parasMap);
                                        String areaError=areaResultMap.get("error");
                                        String areaRequest=areaResultMap.get("result");
                                        Map<String, AreaModel> areaMap = new HashMap<String, AreaModel>();
                                        List<AreaModel> areaList = new ArrayList<AreaModel>();
                                        if("0".equals(areaError)){
                                            if(!TextUtils.isEmpty(areaRequest)) {
                                                JSONObject areaJsonObject = JSONObject.parseObject(areaRequest);
                                                String errorCodeArea=areaJsonObject.getString("errorCode");
                                                if("0".equals(errorCodeArea)){
                                                    JSONArray areaJsonArray = areaJsonObject.getJSONArray("areas");
                                                    if (areaJsonArray != null) {
                                                        int num = areaJsonArray.size();
                                                        if(num>0){
                                                            String area=DataUtil.TableNameEnum.AREA.toString();
                                                            dbUtil.deleteAll(area);
                                                            dbUtil.db.beginTransaction();
                                                            for (int i = 0; i < num; i++) {
                                                                AreaModel areaModel = areaJsonArray.getObject(i,AreaModel.class);
                                                                String Id = areaModel.getAreaId();
                                                                areaMap.put(Id, areaModel);
                                                                areaList.add(areaModel);
                                                                dbUtil.db.insert(area,null,areaModel.getContentValues(areaModel));
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
                                    if(getVersionChange("unitVersion",unitVersion,versionMap)){
                                        Map<String,String> unitResultMap=httpUtil.synch("/app/getunits",httpUtil.TYPE_POST,parasMap);
                                        String unitError=unitResultMap.get("error");
                                        String unitRequest=unitResultMap.get("result");
                                        Map<String, UnitModel> unitMap = new HashMap<>();
                                        List<UnitModel> unitList = new ArrayList<>();
                                        if("0".equals(unitError)){
                                            if(!TextUtils.isEmpty(unitRequest)) {
                                                JSONObject unitJsonObject = JSONObject.parseObject(unitRequest);
                                                String errorCodeUnit=unitJsonObject.getString("errorCode");
                                                if("0".equals(errorCodeUnit)){
                                                    JSONArray unitJsonArray = unitJsonObject.getJSONArray("units");
                                                    if (unitJsonArray != null) {
                                                        int num = unitJsonArray.size();
                                                        if(num>0){
                                                            String unit=DataUtil.TableNameEnum.UNIT.toString();
                                                            dbUtil.deleteAll(unit);
                                                            dbUtil.db.beginTransaction();
                                                            for (int i = 0; i < num; i++) {
                                                                UnitModel unitModel = unitJsonArray.getObject(i,UnitModel.class);
                                                                String Id = unitModel.getgId();
                                                                unitMap.put(Id, unitModel);
                                                                unitList.add(unitModel);
                                                                dbUtil.db.insert(unit,null,unitModel.getContentValues(unitModel));
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
                                    if(getVersionChange("pointVersion",pointVersion,versionMap)){
                                        Map<String,String> pointItemResultMap=httpUtil.synch("/app/getpointitems",httpUtil.TYPE_POST,parasMap);
                                        String pointItemError=pointItemResultMap.get("error");
                                        String pointRequest=pointItemResultMap.get("result");
                                        Map<String, PointItemModel> pointItemMap = new HashMap<>();
                                        List<PointItemModel> pointItemList = new ArrayList<>();
                                        if("0".equals(pointItemError)){
                                            if(!TextUtils.isEmpty(pointRequest)) {
                                                JSONObject pointItemJsonObject = JSONObject.parseObject(pointRequest);
                                                String errorCodePointItem=pointItemJsonObject.getString("errorCode");
                                                if("0".equals(errorCodePointItem)){
                                                    JSONArray pointItemJsonArray = pointItemJsonObject.getJSONArray("pointitems");
                                                    if (pointItemJsonArray != null) {
                                                        int num = pointItemJsonArray.size();
                                                        if(num>0){
                                                            String pointItem=DataUtil.TableNameEnum.POINTITEM.toString();
                                                            dbUtil.deleteAll(pointItem);
                                                            dbUtil.db.beginTransaction();
                                                            for (int i = 0; i < num; i++) {
                                                                PointItemModel pointItemModel = pointItemJsonArray.getObject(i,PointItemModel.class);
                                                                String Id = pointItemModel.getItemTypeId();
                                                                pointItemMap.put(Id, pointItemModel);
                                                                pointItemList.add(pointItemModel);
                                                                dbUtil.db.insert(pointItem,null,pointItemModel.getContentValues(pointItemModel));
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
                                    if(getVersionChange("trainTypeVersion",trainTypeVersion,versionMap)){
                                        Map<String,String> trainTypeResultMap=httpUtil.synch("/app/gettraintypes",httpUtil.TYPE_POST,parasMap);
                                        String trainTypeError=trainTypeResultMap.get("error");
                                        String trainTypeRequest=trainTypeResultMap.get("result");
                                        Map<String, TrainTypeModel> trainTypeMap = new HashMap<>();
                                        List<TrainTypeModel> trainTypeList = new ArrayList<>();
                                        if("0".equals(trainTypeError)){
                                            if(!TextUtils.isEmpty(trainTypeRequest)) {
                                                JSONObject trainTypeJsonObject = JSONObject.parseObject(trainTypeRequest);
                                                String errorCodeTrainType=trainTypeJsonObject.getString("errorCode");
                                                if("0".equals(errorCodeTrainType)){
                                                    JSONArray trainTypeJsonArray = trainTypeJsonObject.getJSONArray("traintypes");
                                                    if (trainTypeJsonArray != null) {
                                                        int num = trainTypeJsonArray.size();
                                                        if(num>0){
                                                            String trainType=DataUtil.TableNameEnum.TRAIN_TYPE.toString();
                                                            dbUtil.deleteAll(trainType);
                                                            dbUtil.db.beginTransaction();
                                                            for (int i = 0; i < num; i++) {
                                                                TrainTypeModel trainTypeModel = trainTypeJsonArray.getObject(i,TrainTypeModel.class);
                                                                String Id = trainTypeModel.getTrainTypeId();
                                                                trainTypeMap.put(Id, trainTypeModel);
                                                                trainTypeList.add(trainTypeModel);
                                                                dbUtil.db.insert(trainType,null,trainTypeModel.getContentValues(trainTypeModel));
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
                                    editor.putString("personVersion",         personVersion);
                                    editor.putString("routeVersion",           routeVersion);
                                    editor.putString("areaVersion",           areaVersion);
                                    editor.putString("unitVersion",           unitVersion);
                                    editor.putString("pointVersion",          pointVersion);
                                    editor.putString("trainTypeVersion",      trainTypeVersion);
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onNoDoubleClick(View v) {

    }

    @Override
    protected void onDestroy() {
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        result=false;
        timer.cancel();
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
           if(fragmentList!=null&&fragmentList.size()>=2){
               WorkFragment workFragment=(WorkFragment) fragmentList.get(0);
               workFragment.refreshData();
               PointTestingResultFragment fragment=(PointTestingResultFragment)fragmentList.get(1);
               fragment.refreshData();
           }
        }
    }

    public void hintKeyPerson(){
        if(DateTimeUtil.inTimeFrame(DateTimeUtil.getMonthDay(26),DateTimeUtil.getMonthLastDay(),new Date())){
            isMonth=true;
        }else{
            isMonth=false;
        }
        if(DateTimeUtil.inTimeFrame(DateTimeUtil.getQuarterDay(26),DateTimeUtil.getQuarterCanChangeDay(),new Date())){
            isQuarter=true;
        }else{
            isQuarter=false;
        }
    }
    public void getKeyPersonInit(){
        Map params=new HashMap();
        String userId=dataUtil.getUser().getUserId();
        params.put("userId",userId);
        Map map=new HashMap();
        map.put("data",JSONObject.toJSONString(params));
        httpUtil.asynch(url, httpUtil.TYPE_POST, map, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                list.clear();
                MyException myException=new MyException();
                myException.buildException(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try{
                        String result= AESUtil.AESDecode(response.body().string());
                        JSONObject jsonObject=JSONObject.parseObject(result);
                        String errorCode=jsonObject.getString("errorCode");
                        if("0".equals(errorCode)){
                            List<KeyPersonModel> personList=JSONArray.parseArray(jsonObject.getString("datas"),KeyPersonModel.class);
                            if(personList!=null&&personList.size()>0){
                                list.addAll(personList);
                                for (KeyPersonModel o:list
                                ) {
                                    if(KeyPersonModel.KeyTypeEnum.MONTH.getId().equals(o.getKeyType())){
                                        monthModel=o;
                                        continue;
                                    }
                                    if(KeyPersonModel.KeyTypeEnum.LAST_MONTH.getId().equals(o.getKeyType())){
                                        lastMonthModel=o;
                                        continue;
                                    }
                                    if(KeyPersonModel.KeyTypeEnum.QUARTER.getId().equals(o.getKeyType())){
                                        quarterModel=o;
                                        continue;
                                    }
                                    if(KeyPersonModel.KeyTypeEnum.LAST_QUARTER.getId().endsWith(o.getKeyType())){
                                        lastQuarterModel=o;
                                        continue;
                                    }
                                }
                            }else{
                                list.clear();
                            }
                            if(isMonth||isQuarter||(lastMonthModel==null&&monthModel==null)||(lastQuarterModel==null&&quarterModel==null))
                                handler.sendEmptyMessage(0);
                        }
                    }catch (Exception e){
                        list.clear();
                        MyException myException=new MyException();
                        myException.buildException(e);
                    }
                }else{
                    list.clear();
                    MyException myException=new MyException();
                    myException.buildException("服务器异常",MainActivity.this);
                }
            }
        });
    }
}
