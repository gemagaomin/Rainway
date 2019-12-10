package com.soft.railway.inspection.models;


import android.text.TextUtils;

import com.soft.railway.inspection.utils.StringUtil;

import java.io.Serializable;
import java.util.Map;


public class TrainInfoModel implements Serializable {
    private String driverId;
    private String driverName;
    private String trainTypeId;
    private String trainOrder;
    private String trainId;
    private String assistantDriverName;
    private String assistantDriverId;
    private String jc;
    private String zz;
    private String ls;

    public TrainInfoModel() {
    }


    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getTrainTypeId() {
        return trainTypeId;
    }

    public void setTrainTypeId(String trainTypeId) {
        this.trainTypeId = trainTypeId;
    }

    public String getTrainOrder() {
        return trainOrder;
    }

    public void setTrainOrder(String trainOrder) {
        this.trainOrder = trainOrder;
    }

    public String getAssistantDriverName() {
        return assistantDriverName;
    }

    public void setAssistantDriverName(String assistantDriverName) {
        this.assistantDriverName = assistantDriverName;
    }

    public String getAssistantDriverId() {
        return assistantDriverId;
    }

    public void setAssistantDriverId(String assistantDriverId) {
        this.assistantDriverId = assistantDriverId;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getJc() {
        return jc;
    }

    public void setJc(String jc) {
        this.jc = jc;
    }

    public String getZz() {
        return zz;
    }

    public void setZz(String zz) {
        this.zz = zz;
    }

    public String getLs() {
        return ls;
    }

    public void setLs(String ls) {
        this.ls = ls;
    }

    public String getTrainTypeIdName(Map<String,TrainTypeModel> trainTypeModelMap){
        StringBuffer str=new StringBuffer();
        if(TextUtils.isEmpty(trainId))
            return "";
        TrainTypeModel trainTypeModel=trainTypeModelMap.get(trainTypeId);
        if(trainTypeModel!=null){
            if(trainTypeModel==null|| TextUtils.isEmpty(trainTypeModel.getTrainTypeName())){
                str.append(trainTypeId);
            }else{
                str.append(trainTypeModel.getTrainTypeName());
            }
            str.append("-").append(trainId);
        }
        return str.toString();
    }
}
