package com.soft.railway.inspection.models;


import java.io.Serializable;
public class AreaAndLineModel implements Serializable {
    private String lineId;
    private String lineName;
    private String stationId;
    private String stationName;
    private String endStationId;
    private String endStationName;
    private String areaId;
    private String areaName;

    public void initData(){
        lineId ="";
        lineName  ="";
        stationId  ="";
        stationName="";
        areaId     ="";
        areaName   ="";
        endStationId="";
        endStationName="";
    }

    public AreaAndLineModel() {
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getEndStationId() {
        return endStationId;
    }

    public void setEndStationId(String endStationId) {
        this.endStationId = endStationId;
    }

    public String getEndStationName() {
        return endStationName;
    }

    public void setEndStationName(String endStationName) {
        this.endStationName = endStationName;
    }

    @Override
    public String toString() {
        return "AreaAndLineModel{" +
                "lineId='" + lineId + '\'' +
                ", lineName='" + lineName + '\'' +
                ", stationId='" + stationId + '\'' +
                ", stationName='" + stationName + '\'' +
                ", areaId='" + areaId + '\'' +
                ", areaName='" + areaName + '\'' +
                ", endStationId='" + endStationId + '\'' +
                ", endStationName='" + endStationName + '\'' +
                '}';
    }

}
