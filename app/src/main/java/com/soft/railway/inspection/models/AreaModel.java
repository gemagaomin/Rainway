package com.soft.railway.inspection.models;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;

public class AreaModel implements Serializable {
    private String areaId;
    private String areaName;
    public AreaModel() {
    }

    public AreaModel(Cursor cursor) {
        this.areaId = cursor.getString(cursor.getColumnIndex("areaid"));
        this.areaName = cursor.getString(cursor.getColumnIndex("areaname"));
    }

    public ContentValues getContentValues(AreaModel areaModel){
        ContentValues contentValues=new ContentValues();
        contentValues.put("areaid", areaModel.getAreaId());
        contentValues.put("areaname", areaModel.getAreaName());
        return contentValues;
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

    @Override
    public String toString() {
        return areaName;
    }


}
