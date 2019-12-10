package com.soft.railway.inspection.pojos;

import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.PersonModel;
import com.soft.railway.inspection.models.TrainInfoModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.models.WorkItemModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkItemPojo implements Serializable {
    private String itemId;
    private String pointId;//项点类型Id
    private String pointContent;//项点翻译
    private List<PersonModel> peoples;//相关人员
    private String showPeoples;
    private String unitId;
    private String unitName;
    private List<FileModel> photos;//照片路径
    private List<FileModel> videos;//视频路径
    private String remarks;//说明
    private String insertTime;//
    private String userId;
    private String workId;
    private String card;
    private String isSelf;
    private TrainInfoModel trainInfoModel;

    public WorkItemPojo(UserModel userModel) {
        this.unitId = userModel.getUnitId();
        this.unitName = userModel.getUnitName();
        this.userId = userModel.getUserId();
        this.peoples = new ArrayList<>();
        this.photos = new ArrayList<>();
        this.videos = new ArrayList<>();
        this.trainInfoModel = new TrainInfoModel();
        this.card = "0";
    }

    public WorkItemPojo(WorkItemModel workItemModel) {
        this.itemId = workItemModel.getItemId();
        this.pointId = workItemModel.getPointId();
        this.pointContent = workItemModel.getPointContent();
        this.unitId = workItemModel.getUnitId();
        this.unitName = workItemModel.getUnitName();
        this.remarks = workItemModel.getRemarks();
        this.insertTime = workItemModel.getInsertTime();
        this.userId = workItemModel.getUserId();
        this.workId = workItemModel.getWorkId();
        this.card = workItemModel.getCard();
        this.peoples = workItemModel.getPeoples();
        this.photos = workItemModel.getPhotos();
        this.videos = workItemModel.getVideos();
        this.isSelf = workItemModel.getIsSelf();
        this.trainInfoModel = workItemModel.getItemTrainInfo();
        if (this.trainInfoModel == null)
            this.trainInfoModel = new TrainInfoModel();
        StringBuffer show = new StringBuffer();
        if (this.peoples == null) {
            this.peoples = new ArrayList<>();
        } else {
            for (PersonModel o : this.peoples
            ) {
                show.append(o.getPersonName()).append(";");
            }
        }
        this.showPeoples = show.toString();
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

    public String getShowPeoples() {
        return showPeoples;
    }

    public String getShowPeopleByPeoples() {
        if (this.peoples == null || this.peoples.size() <= 0)
            return "";
        StringBuffer ret = new StringBuffer();
        for (PersonModel o : this.peoples
        ) {
            ret.append(o.getPersonName()).append(";");
        }
        return ret.toString();
    }

    public void setShowPeoples(String showPeoples) {
        this.showPeoples = showPeoples;
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

    @Override
    public String toString() {
        return "WorkItemPojo{" +
                "itemId='" + itemId + '\'' +
                ", pointId='" + pointId + '\'' +
                ", pointContent='" + pointContent + '\'' +
                ", peoples=" + peoples +
                ", showPeoples='" + showPeoples + '\'' +
                ", unitId='" + unitId + '\'' +
                ", unitName='" + unitName + '\'' +
                ", photos=" + photos +
                ", videos=" + videos +
                ", remarks='" + remarks + '\'' +
                ", insertTime='" + insertTime + '\'' +
                ", userId='" + userId + '\'' +
                ", workId='" + workId + '\'' +
                ", card='" + card + '\'' +
                ", isSelf='" + isSelf + '\'' +
                '}';
    }

    public String getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(String isSelf) {
        this.isSelf = isSelf;
    }

    public TrainInfoModel getTrainInfoModel() {
        return trainInfoModel;
    }

    public void setTrainInfoModel(TrainInfoModel trainInfoModel) {
        this.trainInfoModel = trainInfoModel;
    }
}
