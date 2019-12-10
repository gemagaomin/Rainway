package com.soft.railway.inspection.pojos;

import com.soft.railway.inspection.models.WorkModel;

import java.io.Serializable;
import java.util.List;

public class Plan implements Serializable {
    private String createTime;
    private String inCheck;
    private String inCheckFinished;
    private String trainCheck;
    private String trainCheckFinished;
    private List<WorkModel> works;
    private String startTime;
    private String endTime;
    private String userId;
    private String type;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getInCheck() {
        return inCheck;
    }

    public void setInCheck(String inCheck) {
        this.inCheck = inCheck;
    }

    public String getInCheckFinished() {
        return inCheckFinished;
    }

    public void setInCheckFinished(String inCheckFinished) {
        this.inCheckFinished = inCheckFinished;
    }

    public String getTrainCheck() {
        return trainCheck;
    }

    public void setTrainCheck(String trainCheck) {
        this.trainCheck = trainCheck;
    }

    public String getTrainCheckFinished() {
        return trainCheckFinished;
    }

    public void setTrainCheckFinished(String trainCheckFinished) {
        this.trainCheckFinished = trainCheckFinished;
    }

    public List<WorkModel> getWorks() {
        return works;
    }

    public void setWorks(List<WorkModel> works) {
        this.works = works;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
