package com.soft.railway.inspection.utils;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.soft.railway.inspection.models.AreaModel;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.PersonModel;
import com.soft.railway.inspection.models.LineModel;
import com.soft.railway.inspection.models.PointItemModel;
import com.soft.railway.inspection.models.PointNumModel;
import com.soft.railway.inspection.models.PointTestingResultModel;
import com.soft.railway.inspection.models.PunishmentLevelModel;
import com.soft.railway.inspection.models.StationModel;
import com.soft.railway.inspection.models.TrainTypeModel;
import com.soft.railway.inspection.models.UnitModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.pojos.PersonPojo;
import com.soft.railway.inspection.pojos.Plan;
import com.soft.railway.inspection.pojos.WorkItemPojo;
import com.soft.railway.inspection.pojos.WorkPojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataUtil {
    public static String VersionUpdateUrl = "/app/loadFile";
    public static final boolean DEBUG = true;//false 自己的；true别人的
    public static boolean IS_DEBUG = true;//true 服务器版；false 测试版
    public static final boolean IS_SETTING_IP = false;//是否要设置IP功能
    public static final boolean IS_UPDATE_VERSION_TEST = false;//是否有版本更新测试
    //todo 添加问题项点，问题是否可以为空，项点是否可以为空
    public static final boolean ADD_PROBLEM_IS_CAN_NULL = false;
    public static final boolean ADD_POINT_IS_CAN_NULL = true;
    //todo 软件内部更行数据库更新处理
    public static final boolean IS_UPDATA_DATABASE = false;//是否更新数据库

    public static final String IS_SLEF_POINTITME = "1";
    public static final String IS_NOT_SLEF_POINTITME = "0";
    public static final String IS_MISSION_TRAIN = "1";//是任务车
    public static final String IS_NOT_MISSION_TRAIN = "0";//不是任务车
    public static final String GET_TRAIN_INFO_URL = "/app/gettraininfos";
    public static final String POINT_NO_ZR = "非责任项点";
    //TODO 下载文件地址
    public  static String SERVER_FILE_PATH_MYSELF="http://192.168.137.1:8090/jzgkDemo_war_exploded/downloadjzgk";
   public static String SERVER_FILE_PATH = "http://218.206.94.241:18989/perfect_jzgk/files/";
    public static final int WORK_STATUS_RUNNING = 0;//工作进行中
    public static final int WORK_STATUS_FINISH = 3;//工作结束
    public static final int WORK_STATUS_UNFINISH = 2;//工作检查结束，未完成（没有发牌）
    public static final int WORK_STATUS_UNSTART = 1;//工作未开始
    public static String CAMERA_ORIENTATION = "cameraOrientation";
    public static String BASE_PATH = Environment.getExternalStorageDirectory().getPath();
    public static String PHOTO_PATH = BASE_PATH + "/YJZH/photo";
    public static String VIDEO_PATH = BASE_PATH + "/YJZH/video";
    public static String GPS_PATH = BASE_PATH + "/YJZH/map";
    public static final String RECORDER_PATH = BASE_PATH + "/YJZH/recorder";
    public static String DB_PATH = BASE_PATH + "/YJZH/database";
    public static final String ERROR_TIPS_INTNET_DATA = "errorData";
    public static String httpKey = "qVahl/VD1Ay8OeY4TigYnw==";
    public static Map<String, String> WorkTypeMap = new HashMap();
    public static Map<String, PunishmentLevelModel> punishmentLevelModelMap = new HashMap<>();
    public static List<PunishmentLevelModel> punishmentLevelList = new ArrayList<>();
    public static Map<String, String> VersionMap = new HashMap<>();
    public static Uri newUri;
    public static final String WORK_TYPE_TC = "1";
    public static final String WORK_TYPE_XCJC = "2";
    public static final String WORK_TYPE_SMALL_TC = "3";
    public static final String WORK_TYPE_ZXJC = "4";

    static {
        WorkTypeMap.put(WORK_TYPE_TC, "添乘检查");
        WorkTypeMap.put(WORK_TYPE_XCJC, "现场检查");
        WorkTypeMap.put(WORK_TYPE_SMALL_TC, "小添乘");
        WorkTypeMap.put(WORK_TYPE_ZXJC, "专项检查");
    }

    private PointNumModel pointNumModel;
    private Map<String, FileModel> fileModelMap;
    private List<FileModel> fileModelList;
    private Map<String, UnitModel> unitMap;//存放基础单位信息
    private List<UnitModel> unitList;
    private List<PointTestingResultModel> pointTestingResultModelList;
    private Map<String, PersonModel> driverMap;//存放司机信息
    private List<PersonModel> driverList;
    private Map<String, LineModel> lineMap;
    private List<LineModel> lineList;
    private Map<String, Plan> planMap;
    private List<Plan> planList;
    private Map<String, StationModel> stationMap;
    private List<StationModel> stationList;
    private Map<String, TrainTypeModel> trainTypeMap;
    private List<TrainTypeModel> trainTypeList;
    private Map<String, PointItemModel> pointMap;
    private List<PointItemModel> pointList;
    private Map<String, AreaModel> areaMap;
    private List<AreaModel> areaList;
    public static String selectSearchTime = "";
    private Map<String, WorkItemPojo> pointDetailPojoMap;
    private List<WorkItemPojo> workItemPojoList;
    private UserModel user;
    private List<WorkPojo> workPojoList;
    private static DataUtil myDataUtil;
    private static WorkObserverableUtil workObserverableUtil;

    private DataUtil() {
    }

    public static DataUtil getInstance() {
        if (myDataUtil == null) {
            synchronized (DataUtil.class) {
                if (myDataUtil == null) {
                    myDataUtil = new DataUtil();
                    workObserverableUtil = WorkObserverableUtil.getInstance();
                    if(!DEBUG){
                        SERVER_FILE_PATH=SERVER_FILE_PATH_MYSELF;
                    }
                }
            }
        }
        return myDataUtil;
    }

    public PointNumModel getPointNumModel() {
        return pointNumModel;
    }

    public void setPointNumModel(PointNumModel pointNumModel) {
        this.pointNumModel = pointNumModel;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public Map<String, UnitModel> getUnitMap() {
        return unitMap;
    }

    public void setUnitMap(Map<String, UnitModel> unitMap) {
        this.unitMap = unitMap;
    }

    public List<UnitModel> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<UnitModel> unitList) {
        this.unitList = unitList;
    }

    public Map<String, PersonModel> getDriverMap() {
        return driverMap;
    }

    public void setDriverMap(Map<String, PersonModel> driverMap) {
        this.driverMap = driverMap;
    }

    public List<PersonModel> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<PersonModel> driverList) {
        this.driverList = driverList;
    }

    public Map<String, LineModel> getLineMap() {
        return lineMap;
    }

    public void setLineMap(Map<String, LineModel> lineMap) {
        this.lineMap = lineMap;
    }

    public List<LineModel> getLineList() {
        return lineList;
    }

    public void setLineList(List<LineModel> lineList) {
        this.lineList = lineList;
    }

    public Map<String, StationModel> getStationMap() {
        return stationMap;
    }

    public void setStationMap(Map<String, StationModel> stationMap) {
        this.stationMap = stationMap;
    }

    public List<StationModel> getStationList() {
        return stationList;
    }

    public void setStationList(List<StationModel> stationList) {
        this.stationList = stationList;
    }

    public Map<String, TrainTypeModel> getTrainTypeMap() {
        return trainTypeMap;
    }

    public void setTrainTypeMap(Map<String, TrainTypeModel> trainTypeMap) {
        this.trainTypeMap = trainTypeMap;
    }

    public List<TrainTypeModel> getTrainTypeList() {
        return trainTypeList;
    }

    public void setTrainTypeList(List<TrainTypeModel> trainTypeList) {
        this.trainTypeList = trainTypeList;
    }

    public Map<String, PointItemModel> getPointMap() {
        return pointMap;
    }

    public void setPointMap(Map<String, PointItemModel> pointMap) {
        this.pointMap = pointMap;
    }

    public List<PointItemModel> getPointList() {
        return pointList;
    }

    public List<PointItemModel> getSelfPointList(boolean hasOther) {
        List<PointItemModel> list = new ArrayList<>();
        if (pointList != null) {
            for (PointItemModel o : pointList) {
                if (DataUtil.IS_SLEF_POINTITME.equals(o.getIsSelf())) {
                    list.add(o);
                }
            }
        }
        if (hasOther) {
            PointItemModel pointItemModel = new PointItemModel();
            pointItemModel.setItemTypeId(POINT_NO_ZR);
            pointItemModel.setItemTypeName(POINT_NO_ZR);
            list.add(pointItemModel);
        }
        return list;
    }

    public List<PointItemModel> getNotSelfPointList() {
        List<PointItemModel> list = new ArrayList<>();
        if (pointList != null) {
            for (PointItemModel o : pointList
            ) {
                if (DataUtil.IS_NOT_SLEF_POINTITME.equals(o.getIsSelf())) {
                    list.add(o);
                }
            }
        }
        return list;
    }

    public void setPointList(List<PointItemModel> pointList) {
        this.pointList = pointList;
    }

    public static String getOnlyId() {
        UUID uuid = UUID.randomUUID();

        return uuid.toString();
    }

    public Map<String, Plan> getPlanMap() {
        return planMap;
    }

    public void setPlanMap(Map<String, Plan> planMap) {
        this.planMap = planMap;
    }

    public List<Plan> getPlanList() {
        return planList;
    }

    public void setPlanList(List<Plan> planList) {
        this.planList = planList;
    }

    public List<WorkPojo> getWorkPojoList() {
        return workPojoList;
    }

    public void setWorkPojoList(List<WorkPojo> workPojoList) {
        this.workPojoList = workPojoList;
    }

    public void addWorkPojoList(WorkPojo workPojo) {
        if (workPojoList == null) {
            workPojoList = new ArrayList<>();
        }
        if (ifHasRunningWork()) {
            this.workPojoList.add(1, workPojo);
        } else {
            this.workPojoList.add(0, workPojo);
        }
        refreshWorkView();
    }

    public void editWorkPojoList(WorkPojo workPojo) {
        if (!workPojoList.isEmpty()) {
            for (int i = 0, num = workPojoList.size(); i < num; i++) {
                WorkPojo workPojoOne = workPojoList.get(i);
                if (workPojoOne.getWorkId().equals(workPojo.getWorkId())) {
                    workPojoList.set(i, workPojo);
                    break;
                }
            }
        }
        refreshWorkView();
    }

    public void editWorkPojoListNoRefresh(WorkPojo workPojo) {
        if (!workPojoList.isEmpty()) {
            for (int i = 0, num = workPojoList.size(); i < num; i++) {
                WorkPojo workPojoOne = workPojoList.get(i);
                if (workPojoOne.getWorkId().equals(workPojo.getWorkId())) {
                    workPojoList.set(i, workPojo);
                    break;
                }
            }
        }
    }

    public void removeWorkPojoList(WorkPojo workPojo) {
        for (WorkPojo o : workPojoList
        ) {
            if (o.getWorkId().equals(workPojo.getWorkId())) {
                workPojoList.remove(o);
                break;
            }
        }
        refreshWorkView();
    }

    public Map<String, WorkItemPojo> getPointDetailPojoMap() {
        return pointDetailPojoMap;
    }

    public void setPointDetailPojoMap(Map<String, WorkItemPojo> pointDetailPojoMap) {
        this.pointDetailPojoMap = pointDetailPojoMap;
    }

    public List<WorkItemPojo> getWorkItemPojoList() {
        return workItemPojoList;
    }

    public void setWorkItemPojoList(List<WorkItemPojo> workItemPojoList) {
        this.workItemPojoList = workItemPojoList;
    }

    public void addPointDetail(WorkItemPojo workItemPojo) {
        if (this.workItemPojoList == null) {
            this.workItemPojoList = new ArrayList<>();
        }
        if (this.workItemPojoList.isEmpty()) {
            this.workItemPojoList = new ArrayList<>();
            this.pointDetailPojoMap = new HashMap<>();
        }
        this.workItemPojoList.add(0, workItemPojo);
        this.pointDetailPojoMap.put(workItemPojo.getItemId(), workItemPojo);
        //todo 2019-11-20 晚上修改
        // workObserverableUtil.refresh();
    }

    public void editPointDetail(WorkItemPojo workItemPojo) {
        if (!workItemPojoList.isEmpty()) {
            for (int i = 0, num = workItemPojoList.size(); i < num; i++) {
                WorkItemPojo o = workItemPojoList.get(i);
                if (o.getItemId().equals(workItemPojo.getItemId())) {
                    workItemPojoList.set(i, workItemPojo);
                    pointDetailPojoMap.put(o.getItemId(), workItemPojo);
                    break;
                }
            }
        }
        //todo 2019-11-20 晚上修改
        // workObserverableUtil.refresh();
    }

    public void removePointDetail(WorkItemPojo workItemPojo) {
        Iterator iterator = workItemPojoList.iterator();
        while (iterator.hasNext()) {
            WorkItemPojo data = (WorkItemPojo) iterator.next();
            if (workItemPojo.getItemId().equals(data.getItemId())) {
                iterator.remove();
                return;
            }
        }
        pointDetailPojoMap.remove(workItemPojo.getItemId());
        //todo 2019-11-20 晚上修改
        // workObserverableUtil.refresh();
    }

    public void removePointDetail(String id) {
        if(pointDetailPojoMap!=null&&pointDetailPojoMap.size()>0){
            pointDetailPojoMap.remove(id);
        }
        if(workItemPojoList==null||workItemPojoList.size()==0)
            return;
        Iterator iterator = workItemPojoList.iterator();
        while (iterator.hasNext()) {
            WorkItemPojo data = (WorkItemPojo) iterator.next();
            if (id.equals(data.getWorkId())) {
                iterator.remove();
                return;
            }
        }

        //todo 2019-11-20 晚上修改
        //workObserverableUtil.refresh();
    }

    public String getPointIdByName(String name) {
        if (!pointList.isEmpty()) {
            for (PointItemModel o : pointList
            ) {
                if (o.getItemTypeName().equals(name)) {
                    return o.getItemTypeId();
                }
            }
        }
        return null;
    }

    public void refreshWorkView() {
        workObserverableUtil.refresh();
    }

    public Map<String, AreaModel> getAreaMap() {
        return areaMap;
    }

    public void setAreaMap(Map<String, AreaModel> areaMap) {
        this.areaMap = areaMap;
    }

    public List<AreaModel> getAreaList() {
        return areaList;
    }

    public List<AreaModel> getAreaListByUnitId() {
        String unitId = user.getUnitId();
        List<AreaModel> areaModels = new ArrayList<>();
        if (areaList != null) {
            for (AreaModel o : areaList
            ) {
                areaModels.add(o);
            }
        }
        return areaModels;
    }

    public void setAreaList(List<AreaModel> areaList) {
        this.areaList = areaList;
    }

    public static void setMyDataUtil(DataUtil myDataUtil) {
        String id = DataUtil.getOnlyId();
    }

    private boolean ifHasRunningWork() {
        if (workPojoList != null && workPojoList.size() > 0) {
            for (WorkPojo o :
                    workPojoList
            ) {
                if (o.getWorkStatus().equals(WORK_STATUS_RUNNING + "")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String StringData(String s) {
        String re = "";
        if (!TextUtils.isEmpty(s)) {
            re = s;
        }
        return re;
    }

    public List<WorkPojo> getWorkPojoListForData(String time) {
        List<WorkPojo> list = new ArrayList<>();
        if (!workPojoList.isEmpty()) {
            for (WorkPojo o : workPojoList
            ) {
                String startTime = o.getStartTime();
                if (startTime.indexOf(time) >= 0) {
                    list.add(o);
                }
            }
        }
        return list;
    }

    public enum TableNameEnum {
        TRAIN_TYPE("t_traintype"),
        STATION("t_station"),
        POINTITEM("t_point_item"),
        PERSON("t_person"),
        LINE("t_line"),
        AREA("t_area"),
        WORK("work"),
        ITEM("item"),
        UNIT("t_unit"),
        SUBMITFILE("submitfile");
        private final String data;

        private TableNameEnum(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return data;
        }
    }

    public static Map<String, String> getVersionMap() {
        return VersionMap;
    }

    public static void setVersionMap(Map<String, String> versionMap) {
        VersionMap = versionMap;
    }

    public static void setVersionMap(SharedPreferences sharedPreferences) {
        if (sharedPreferences != null) {
            String personVersion = sharedPreferences.getString("personVersion", "");
            String routeVersion = sharedPreferences.getString("routeVersion", "");
            String areaVersion = sharedPreferences.getString("areaVersion", "");
            String unitVersion = sharedPreferences.getString("unitVersion", "");
            String pointVersion = sharedPreferences.getString("pointVersion", "");
            String trainTypeVersion = sharedPreferences.getString("trainTypeVersion", "");
            VersionMap.put("personVersion", personVersion);
            VersionMap.put("routeVersion", routeVersion);
            VersionMap.put("areaVersion", areaVersion);
            VersionMap.put("unitVersion", unitVersion);
            VersionMap.put("pointVersion", pointVersion);
            VersionMap.put("trainTypeVersion", trainTypeVersion);
        }
    }

    public List<PointTestingResultModel> getPointTestingResultModelList() {
        return pointTestingResultModelList;
    }

    public void setPointTestingResultModelList(List<PointTestingResultModel> pointTestingResultModelList) {
        this.pointTestingResultModelList = pointTestingResultModelList;
    }

    public static List<PunishmentLevelModel> getPunishmentLevelList() {
        if (punishmentLevelList == null || punishmentLevelList.size() == 0) {
            PunishmentLevelModel punishmentLevelModel = new PunishmentLevelModel("0", "批评教育");
            punishmentLevelList.add(punishmentLevelModel);
            punishmentLevelModel = new PunishmentLevelModel("2", "红牌");
            punishmentLevelList.add(punishmentLevelModel);
            punishmentLevelModel = new PunishmentLevelModel("3", "黄牌");
            punishmentLevelList.add(punishmentLevelModel);
            punishmentLevelModel = new PunishmentLevelModel("4", "白牌");
            punishmentLevelList.add(punishmentLevelModel);
            punishmentLevelModel = new PunishmentLevelModel("1", "苗子");
            punishmentLevelList.add(punishmentLevelModel);
            punishmentLevelModelMap.put("0", new PunishmentLevelModel("0", "批评教育"));
            punishmentLevelModelMap.put("1", new PunishmentLevelModel("1", "苗子"));
            punishmentLevelModelMap.put("2", new PunishmentLevelModel("2", "红牌"));
            punishmentLevelModelMap.put("3", new PunishmentLevelModel("3", "黄牌"));
            punishmentLevelModelMap.put("4", new PunishmentLevelModel("4", "白牌"));
        }
        return punishmentLevelList;
    }

    public Map<String, FileModel> getFileModelMap() {
        return fileModelMap;
    }

    public void setFileModelMap(Map<String, FileModel> fileModelMap) {
        this.fileModelMap = fileModelMap;
    }

    public List<FileModel> getFileModelList() {
        return fileModelList;
    }

    public void setFileModelList(List<FileModel> fileModelList) {
        this.fileModelList = fileModelList;
    }

    public void addFileModel(FileModel fileModel) {
        if (this.fileModelList == null) {
            this.fileModelList = new ArrayList<>();
        }
        if (this.fileModelMap == null) {
            this.fileModelMap = new HashMap<>();
        }
        this.fileModelList.add(fileModel);
        this.fileModelMap.put(fileModel.getFileId(), fileModel);
    }

    public void deleteFileModel(String id) {
        Iterator iterator = this.fileModelList.iterator();
        while (iterator.hasNext()) {
            FileModel fileModel = (FileModel) iterator.next();
            if (id.equals(fileModel.getFileId())) {
                iterator.remove();
                return;
            }
        }
        this.fileModelMap.remove(id);
    }

    public Plan getPlan() {
        if (planList == null || planList.size() == 0) {
            return null;
        }
        Plan plan = null;
        for (Plan o : planList
        ) {
            String t = o.getCreateTime();
            if (!TextUtils.isEmpty(t) && t.length() >= selectSearchTime.length() && t.indexOf(selectSearchTime) != -1) {
                plan = o;
                break;
            }
        }
        return plan;
    }


}
