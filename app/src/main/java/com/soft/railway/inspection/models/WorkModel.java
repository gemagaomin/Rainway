package com.soft.railway.inspection.models;

import android.content.ContentValues;
import android.database.Cursor;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.pojos.PointItemPojo;
import com.soft.railway.inspection.pojos.WorkItemPojo;
import com.soft.railway.inspection.pojos.WorkPojo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkModel implements Serializable {
    private String workId;
    private String workType;
    private String workStatus;
    private String startTime;
    private String endTime;
    private String userId;
    private String workSuggest;
    private List<AreaModel> areas;
    private List<LineStationModel> routeStations;
    private List<FileModel> capturePhotos;

    private String gpsFileName="";
    private TrainInfoModel trainInfo;
    private List<WorkItemModel>  items;
    private String unitId;
    private String insertTime;
    private String area;
    private String routes;
    private String trains;
    private String missionTrain;
    private String teach;//专教内容
    private String workExplain;//具体工作类型名称

    private List<FileModel> recorders;//专教录音文件
    private String planWorkItems;//自主选择完成的项点

    public WorkModel() {
    }

    public WorkModel(Cursor cursor) {
        this.workId=cursor.getString(cursor.getColumnIndex("workid"));
        this.workType =cursor.getString(cursor.getColumnIndex("worktype"));
        this.workStatus = cursor.getString(cursor.getColumnIndex("workstatus"));
        this.startTime=cursor.getString(cursor.getColumnIndex("starttime"));
        this.endTime=cursor.getString(cursor.getColumnIndex("endtime"));
        this.userId= cursor.getString(cursor.getColumnIndex("userid"));
        this.unitId=cursor.getString(cursor.getColumnIndex("unitid"));
        this.workSuggest=cursor.getString(cursor.getColumnIndex("worksuggest"));
        String areaStr=cursor.getString(cursor.getColumnIndex("areas"));
        this.areas=JSONArray.parseArray(areaStr,AreaModel.class);
        String routeStationsString= cursor.getString(cursor.getColumnIndex("routestations"));
        this.routeStations = JSONArray.parseArray(routeStationsString, LineStationModel.class);
        String capturePhotosString=cursor.getString(cursor.getColumnIndex("capturephotos"));
        this.capturePhotos =JSONArray.parseArray(capturePhotosString,FileModel.class);
        this.gpsFileName=cursor.getString(cursor.getColumnIndex("gpsfilename"));
        this.trainInfo= JSONObject.parseObject(cursor.getString(cursor.getColumnIndex("traininfo")),TrainInfoModel.class);
        this.missionTrain=cursor.getString(cursor.getColumnIndex("missiontrain"));
        this.teach=cursor.getString(cursor.getColumnIndex("teach"));
        this.workExplain=cursor.getString(cursor.getColumnIndex("workexplain"));
        String recorders=cursor.getString(cursor.getColumnIndex("recorders"));
        this.recorders=JSONObject.parseArray(recorders,FileModel.class);
        this.planWorkItems=cursor.getString(cursor.getColumnIndex("planworkitems"));
    }

    public ContentValues getContentValues(WorkModel workModel){
        ContentValues contentValues=new ContentValues();
        contentValues.put("workid",workModel.getWorkId());
        contentValues.put("worktype",workModel.getWorkType());
        contentValues.put("workstatus",workModel.getWorkStatus());
        contentValues.put("starttime",workModel.getStartTime());
        contentValues.put("endtime",workModel.getEndTime());
        contentValues.put("userid",workModel.getUserId());
        contentValues.put("unitid",workModel.getUnitId());
        contentValues.put("worksuggest",workModel.getWorkSuggest());
        contentValues.put("areas",JSONObject.toJSONString(workModel.getAreas()));
        contentValues.put("routestations",JSONObject.toJSONString(workModel.getRouteStations()));
        contentValues.put("gpsfilename",workModel.getGpsFileName());
        contentValues.put("capturephotos",JSONObject.toJSONString(workModel.getCapturePhotos()));
        contentValues.put("traininfo",JSONObject.toJSONString(workModel.getTrainInfo()));
        contentValues.put("missiontrain",workModel.getMissionTrain());
        contentValues.put("teach",workModel.getTeach());
        contentValues.put("workexplain",workModel.getWorkExplain());
        contentValues.put("recorders",JSONObject.toJSONString(workModel.getRecorders()));
        contentValues.put("planworkitems",workModel.getPlanWorkItems());
        return contentValues;
    }

    public WorkModel(WorkPojo workPojo){
        this.workId            =workPojo.getWorkId();
        this.teach             =workPojo.getTeach();
        this.missionTrain      =workPojo.getMissionTrain();
        this.workExplain       =workPojo.getWorkExplain();
        this.gpsFileName       =workPojo.getGpsFileName();
        this.workType          =workPojo.getWorkType();
        this.workStatus        =workPojo.getWorkStatus();
        this.startTime         =workPojo.getStartTime();
        this.endTime           =workPojo.getEndTime();
        this.userId            =workPojo.getUserId();
        this.workSuggest       =workPojo.getWorkSuggest();
        this.capturePhotos     =workPojo.getCapturePhotos();
        this.areas             =workPojo.getAreas();
        this.routeStations     =workPojo.getRouteStations();
        this.trainInfo         =workPojo.getTrainInfo();
        this.unitId            =workPojo.getUnitId();
        List<WorkItemModel> list=new ArrayList<>();
        if(workPojo.getItems()!=null&&workPojo.getItems().size()>0){
            for (WorkItemPojo o:workPojo.getItems()
            ) {
                WorkItemModel workItemModel=new WorkItemModel(o);
                list.add(workItemModel);
            }
        }
        this.items=list;
        List<AreaModel> areaList=new ArrayList<>();
        if(workPojo.getAreaList()!=null&&workPojo.getAreaList().size()>0){
            for (AreaAndLineModel o:workPojo.getAreaList()
            ) {
                AreaModel one=new AreaModel();
                one.setAreaId(o.getAreaId());
                one.setAreaName(o.getAreaName());
                areaList.add(one);
            }
        }
        this.areas=areaList;
        List<LineStationModel> routeStationList=new ArrayList<>();
        if(workPojo.getRouteStationList()!=null&&workPojo.getRouteStationList().size()>0){
            for (AreaAndLineModel o:workPojo.getRouteStationList()
            ) {
                LineStationModel one=new LineStationModel();
                one.setLineId(o.getLineId());
                one.setLineName(o.getLineName());
                one.setStationId(o.getStationId());
                one.setStationName(o.getStationName());
                one.setEndStationId(o.getEndStationId());
                one.setEndStationName(o.getEndStationName());
                routeStationList.add(one);
            }
        }
        this.routeStations=routeStationList;
         List<WorkItemModel> workItemModels=new ArrayList<>();
        if(workPojo.getItems()!=null&&workPojo.getItems().size()>0){
            for (WorkItemPojo o:workPojo.getItems()
            ) {
                WorkItemModel one=new WorkItemModel(o);
                workItemModels.add(one);
            }
        }
        this.items=workItemModels;

        this.recorders=workPojo.getRecorders();
        StringBuffer plans=new StringBuffer();
        List<PointItemPojo> pointItems=workPojo.getPlanWorkItems();
        if(pointItems!=null&&pointItems.size()>0){
            for (int i=0,num=pointItems.size();i<num;i++ ) {
                PointItemPojo pointItemModel=pointItems.get(i);
                plans.append(pointItemModel.getItemTypeId());
                if(i<num-1){
                    plans.append(",");
                }
            }
        }
        this.planWorkItems=plans.toString();
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkSuggest() {
        return workSuggest;
    }

    public void setWorkSuggest(String workSuggest) {
        this.workSuggest = workSuggest;
    }

    public List<AreaModel> getAreas() {
        return areas;
    }

    public void setAreas(List<AreaModel> areas) {
        this.areas = areas;
    }

    public List<LineStationModel> getRouteStations() {
        return routeStations;
    }

    public void setRouteStations(List<LineStationModel> routeStations) {
        this.routeStations = routeStations;
    }

    public List<FileModel> getCapturePhotos() {
        return capturePhotos;
    }

    public void setCapturePhotos(List<FileModel> capturePhotos) {
        this.capturePhotos = capturePhotos;
    }

    public TrainInfoModel getTrainInfo() {
        return trainInfo;
    }

    public void setTrainInfo(TrainInfoModel trainInfo) {
        this.trainInfo = trainInfo;
    }

    public String getGpsFileName() {
        return gpsFileName;
    }

    public void setGpsFileName(String gpsFileName) {
        this.gpsFileName = gpsFileName;
    }

    public List<WorkItemModel> getItems() {
        return items;
    }

    public void setItems(List<WorkItemModel> items) {
        this.items = items;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRoutes() {
        return routes;
    }

    public void setRoutes(String routes) {
        this.routes = routes;
    }

    public String getTrains() {
        return trains;
    }

    public void setTrains(String trains) {
        this.trains = trains;
    }

    public String getMissionTrain() {
        return missionTrain;
    }

    public void setMissionTrain(String missionTrain) {
        this.missionTrain = missionTrain;
    }

    public String getWorkExplain() {
        return workExplain;
    }

    public void setWorkExplain(String workExplain) {
        this.workExplain = workExplain;
    }

    public String getTeach() {
        return teach;
    }

    public void setTeach(String teach) {
        this.teach = teach;
    }

    public List<FileModel> getRecorders() {
        return recorders;
    }

    public void setRecorders(List<FileModel> recorders) {
        this.recorders = recorders;
    }

    public String getPlanWorkItems() {
        return planWorkItems;
    }

    public void setPlanWorkItems(String planWorkItems) {
        this.planWorkItems = planWorkItems;
    }
}
