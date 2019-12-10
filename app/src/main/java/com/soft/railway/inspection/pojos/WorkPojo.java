package com.soft.railway.inspection.pojos;

import android.text.TextUtils;
import com.soft.railway.inspection.models.AreaAndLineModel;
import com.soft.railway.inspection.models.AreaModel;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.LineStationModel;
import com.soft.railway.inspection.models.PointItemModel;
import com.soft.railway.inspection.models.TrainInfoModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.models.WorkItemModel;
import com.soft.railway.inspection.models.WorkModel;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.DateTimeUtil;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkPojo implements Serializable {
    private String                  workId;
    private String                  workType;
    private String                  workStatus;
    private String                  startTime;
    private String                  endTime;
    private String                  userId;
    private String                  unitId;
    private String                  workSuggest;
    private List<WorkItemPojo>      items;
    private List<AreaModel>         areas;
    private List<LineStationModel>  routeStations;
    private TrainInfoModel          trainInfo;
    private List<AreaAndLineModel>  areaList;
    private List<AreaAndLineModel>  routeStationList;
    private List<FileModel>         capturePhotos;
    private String                  gpsFileName;
    private String                  place;
    private String                  showTime;
    private String                  missionTrain;
    private String                  teach;//专教内容
    private String                  workExplain;//具体工作类型名称

    private List<PointItemPojo>    planWorkItems;
    private String   planWorkItemsStr;
    private List<FileModel>         recorders;
    public WorkPojo(UserModel userModel) {
            this.items=new ArrayList<>();
            this.areas=new ArrayList<>();
            this.routeStationList=new ArrayList<>();
            this.trainInfo=new TrainInfoModel();
            this.capturePhotos=new ArrayList<>();
            this.routeStations=new ArrayList<>();
            this.areaList=new ArrayList<>();
            this.userId=userModel.getUserId();
            this.unitId=userModel.getUnitId();
            this.missionTrain= DataUtil.IS_NOT_MISSION_TRAIN;
            this.planWorkItems=new ArrayList<>();
            this.recorders=new ArrayList<>();
    }

    public WorkPojo(WorkModel workModel,Map<String,PointItemModel> pointItemModelMap) {
        this.workId            =workModel.getWorkId();
        this.workType          =workModel.getWorkType();
        this.workStatus        =workModel.getWorkStatus();
        this.startTime         =workModel.getStartTime();
        this.endTime           =workModel.getEndTime();
        this.userId            =workModel.getUserId();
        this.workSuggest       =workModel.getWorkSuggest();
        this.areas             =workModel.getAreas();
        this.routeStations     =workModel.getRouteStations();
        this.trainInfo         =workModel.getTrainInfo();
        this.unitId            =workModel.getUnitId();
        this.missionTrain    =workModel.getMissionTrain();
        this.teach             =workModel.getTeach();
        this.workExplain       =workModel.getWorkExplain();
        StringBuffer placeBf=new StringBuffer();
        List<WorkItemPojo> list=new ArrayList<>();
        if(workModel.getItems()!=null&&workModel.getItems().size()>0){
            for (WorkItemModel o:workModel.getItems()
                 ) {
                WorkItemPojo workItemPojo=new WorkItemPojo(o);
                list.add(workItemPojo);
            }
        }
        this.items             =list;
        List<AreaAndLineModel> areaList=new ArrayList<>();
        if(this.areas!=null&&this.areas.size()>0){
            for (AreaModel o:this.areas
            ) {
                AreaAndLineModel one=new AreaAndLineModel();
                placeBf.append(o.getAreaName()).append(";");
                one.setAreaId(o.getAreaId());
                one.setAreaName(o.getAreaName());
                areaList.add(one);
            }
        }
        this.areaList=areaList;
        List<AreaAndLineModel> routeStationList=new ArrayList<>();
        if(this.routeStations!=null&&this.routeStations.size()>0){
            for (LineStationModel o:this.routeStations
            ) {
                AreaAndLineModel one=new AreaAndLineModel();
                one.setLineId(o.getLineId());
                one.setLineName(o.getLineName());
                placeBf.append(o.getLineName()).append(";");
                one.setStationId(o.getStationId());
                one.setStationName(o.getStationName());
                placeBf.append(o.getStationName());
                one.setEndStationId(o.getEndStationId());
                one.setEndStationName(o.getEndStationName());
                String end=o.getEndStationId();
                if(!TextUtils.isEmpty(end)){
                   placeBf.append(" -> ").append(o.getEndStationName());
                }
                routeStationList.add(one);
                placeBf.append(";");
            }
        }
        this.place=placeBf.toString();
        this.routeStationList=routeStationList;
        String time="";
        if(!TextUtils.isEmpty(this.startTime)){
            time+=DateTimeUtil.getNewDate(this.startTime);
            if(!TextUtils.isEmpty(this.endTime)){
                time+=" - "+ DateTimeUtil.getNewDate(this.endTime);
            }
        }
        this.showTime=time;
        List<FileModel> fileModels=new ArrayList<>();
        if(workModel.getCapturePhotos()!=null&&workModel.getCapturePhotos().size()>0){
            fileModels.addAll(workModel.getCapturePhotos());
        }
        this.capturePhotos=fileModels;

        List<FileModel> recorderFileModels=new ArrayList<>();
        if(workModel.getRecorders()!=null&&workModel.getRecorders().size()>0){
            recorderFileModels.addAll(workModel.getRecorders());
        }

        this.recorders=recorderFileModels;

        List<PointItemPojo> pointItems=new ArrayList<>();
        String planWorkItems=workModel.getPlanWorkItems();
        if(!TextUtils.isEmpty(planWorkItems)){
            String[] pointIdArr=planWorkItems.split(",");
            if(pointIdArr!=null&&pointIdArr.length>0){
                for (String pointId:pointIdArr
                     ) {
                    PointItemModel pointItemModel=pointItemModelMap.get(pointId);
                    if(pointItemModel!=null){
                        pointItems.add(new PointItemPojo(pointItemModel));
                    }
                }
            }
        }
        this.planWorkItems=pointItems;
        this.planWorkItemsStr=workModel.getPlanWorkItems();
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

    public TrainInfoModel getTrainInfo() {
        return trainInfo;
    }

    public void setTrainInfo(TrainInfoModel trainInfo) {
        this.trainInfo = trainInfo;
    }

    public List<AreaAndLineModel> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<AreaAndLineModel> areaList) {
        this.areaList = areaList;
    }

    public List<AreaAndLineModel> getRouteStationList() {
        return routeStationList;
    }

    public void setRouteStationList(List<AreaAndLineModel> routeStationList) {
        this.routeStationList = routeStationList;
    }

    public List<FileModel> getCapturePhotos() {
        return capturePhotos;
    }

    public void setCapturePhotos(List<FileModel> capturePhotos) {
        this.capturePhotos = capturePhotos;
    }

    public String getGpsFileName() {
        return gpsFileName;
    }

    public void setGpsFileName(String gpsFileName) {
        this.gpsFileName = gpsFileName;
    }

    public List<WorkItemPojo> getItems() {
        return items;
    }

    public void setItems(List<WorkItemPojo> items) {
        this.items = items;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getMissionTrain() {
        return missionTrain;
    }

    public void setMissionTrain(String missionTrain) {
        this.missionTrain = missionTrain;
    }

    public String getTeach() {
        return teach;
    }

    public void setTeach(String teach) {
        this.teach = teach;
    }

    public String getWorkExplain() {
        return workExplain;
    }

    public void setWorkExplain(String workExplain) {
        this.workExplain = workExplain;
    }

    public List<PointItemPojo> getPlanWorkItems() {
        return planWorkItems;
    }

    public void setPlanWorkItems(List<PointItemPojo> planWorkItems) {
        this.planWorkItems = planWorkItems;
    }

    public List<FileModel> getRecorders() {
        return recorders;
    }

    public void setRecorders(List<FileModel> recorders) {
        this.recorders = recorders;
    }

    public String getPlanWorkItemsStr() {
        return planWorkItemsStr;
    }

    public void setPlanWorkItemsStr(String planWorkItemsStr) {
        this.planWorkItemsStr = planWorkItemsStr;
    }
}
