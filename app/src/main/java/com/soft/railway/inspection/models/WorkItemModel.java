package com.soft.railway.inspection.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.pojos.WorkItemPojo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkItemModel implements Serializable {
    private String itemId;
    private String pointId;//项点类型Id
    private String pointContent;//项点翻译
    private List<PersonModel> peoples;//相关人员
    private String unitId;
    private String unitName;
    private List<FileModel> photos;//照片路径
    private List<FileModel> videos;//视频路径
    private String remarks;//问题的描述
    private String insertTime;//
    private String userId;
    private String workId;
    private String card="0";
    private String isSelf;
    private String cardTime;
    private TrainInfoModel itemTrainInfo;

    public WorkItemModel() {
        peoples=new ArrayList<>();
        photos=new ArrayList<>();
        videos=new ArrayList<>();
    }

    public WorkItemModel(WorkItemPojo workItemPojo){
        this.itemId         =workItemPojo.getItemId();
        this.pointId        =workItemPojo.getPointId();
        this.pointContent   =workItemPojo.getPointContent();
        this.peoples        =workItemPojo.getPeoples();
        this.unitId         =workItemPojo.getUnitId();
        this.unitName       =workItemPojo.getUnitName();
        this.photos         =workItemPojo.getPhotos();
        this.videos         =workItemPojo.getVideos();
        this.remarks        =workItemPojo.getRemarks();
        this.insertTime     =workItemPojo.getInsertTime();
        this.userId         =workItemPojo.getUserId();
        this.workId         =workItemPojo.getWorkId();
        this.card           =workItemPojo.getCard();
        this.isSelf         =workItemPojo.getIsSelf();
        this.itemTrainInfo =workItemPojo.getTrainInfoModel();
    }

    public ContentValues getContentValues(WorkItemModel workItem, boolean isEdit){
        ContentValues contentValues=new ContentValues();
        if(!isEdit){
            contentValues.put("itemid", workItem.getItemId());
        }
        contentValues.put("pointid", workItem.getPointId());
        contentValues.put("pointcontent",workItem.getPointContent());
        contentValues.put("inserttime", workItem.getInsertTime());
        contentValues.put("photos", JSONArray.toJSONString( workItem.getPhotos()));
        contentValues.put("peoples", JSONArray.toJSONString(workItem.getPeoples()));
        contentValues.put("userid", workItem.getUserId());
        contentValues.put("workid", workItem.getWorkId());
        contentValues.put("videos", JSONArray.toJSONString(workItem.getVideos()));
        contentValues.put("remark", workItem.getRemarks());
        contentValues.put("unitid", workItem.getUnitId());
        contentValues.put("unitname",workItem.getUnitName());
        contentValues.put("card", workItem.getCard());
        contentValues.put("isself", workItem.getIsSelf());
        contentValues.put("itemtraininfo", JSONObject.toJSONString(workItem.getItemTrainInfo()));
        return contentValues;
    }

    public WorkItemModel(Cursor cursor) {
        this.itemId=cursor.getString(cursor.getColumnIndex("itemid"));
        this.pointId =cursor.getString(cursor.getColumnIndex("pointid"));
        this.pointContent = cursor.getString(cursor.getColumnIndex("pointcontent"));
        this.unitId=cursor.getString(cursor.getColumnIndex("unitid"));
        this.unitName=cursor.getString(cursor.getColumnIndex("unitname"));
        this.remarks= cursor.getString(cursor.getColumnIndex("remark"));
        this.isSelf= cursor.getString(cursor.getColumnIndex("isself"));
        this.insertTime=cursor.getString(cursor.getColumnIndex("inserttime"));
        this.userId=cursor.getString(cursor.getColumnIndex("userid"));
        this.workId=cursor.getString(cursor.getColumnIndex("workid"));
        this.card=cursor.getString(cursor.getColumnIndex("card"));
        String peoplesString= cursor.getString(cursor.getColumnIndex("peoples"));
        this.peoples = JSONArray.parseArray(peoplesString,PersonModel.class);
        String photosString=cursor.getString(cursor.getColumnIndex("photos"));
        this.photos =JSONArray.parseArray(photosString,FileModel.class);
        String videosString=cursor.getString(cursor.getColumnIndex("videos"));
        this.videos=JSONArray.parseArray(videosString,FileModel.class);
        String trainInfoString=cursor.getString(cursor.getColumnIndex("itemtraininfo"));
        if(TextUtils.isEmpty(trainInfoString)){
            this.itemTrainInfo=new TrainInfoModel();
        }else{
            this.itemTrainInfo=JSONObject.parseObject(trainInfoString,TrainInfoModel.class);
        }
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getPointContent() {
        return pointContent;
    }

    public void setPointContent(String pointContent) {
        this.pointContent = pointContent;
    }

    public List<PersonModel> getPeoples() {
        return peoples;
    }

    public void setPeoples(List<PersonModel> peoples) {
        this.peoples = peoples;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public List<FileModel> getPhotos() {
        return photos;
    }

    public void setPhotos(List<FileModel> photos) {
        this.photos = photos;
    }

    public List<FileModel> getVideos() {
        return videos;
    }

    public void setVideos(List<FileModel> videos) {
        this.videos = videos;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(String isSelf) {
        this.isSelf = isSelf;
    }

    public String getCardTime() {
        return cardTime;
    }

    public void setCardTime(String cardTime) {
        this.cardTime = cardTime;
    }

    public TrainInfoModel getItemTrainInfo() {
        return itemTrainInfo;
    }

    public void setItemTrainInfo(TrainInfoModel itemTrainInfo) {
        this.itemTrainInfo = itemTrainInfo;
    }


}
