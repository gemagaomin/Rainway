package com.soft.railway.inspection.models;

import android.content.ContentValues;
import android.database.Cursor;

public class StationModel {
    private String stationId;
    private String stationName;
    private String lineId;

    public StationModel() {
    }

    public StationModel(Cursor cursor) {
        this.stationId=cursor.getString(cursor.getColumnIndex("stationid"));
        this.stationName=cursor.getString(cursor.getColumnIndex("stationname"));
        this.lineId=cursor.getString(cursor.getColumnIndex("lineid"));
    }

    public ContentValues getContentValues(StationModel stationModel){
        ContentValues contentValues=new ContentValues();
        contentValues.put("stationid",stationModel.getStationId());
        contentValues.put("stationname",stationModel.getStationName());
        contentValues.put("lineid",stationModel.getLineId());
        return contentValues;
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

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }
}
