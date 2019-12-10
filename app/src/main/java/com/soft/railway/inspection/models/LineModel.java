package com.soft.railway.inspection.models;

import android.content.ContentValues;
import android.database.Cursor;

public class LineModel {
    private String lineId    ;
    private String lineName   ;

    public LineModel() {
    }

    public LineModel(Cursor cursor){
        this.lineId=cursor.getString(cursor.getColumnIndex("lineid"));
        this.lineName=cursor.getString(cursor.getColumnIndex("linename"));
    }

    public ContentValues getContentValues(LineModel lineModel){
        ContentValues contentValues=new ContentValues();
        contentValues.put("lineid",lineModel.getLineId());
        contentValues.put("linename",lineModel.getLineName());
        return contentValues;
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
}
