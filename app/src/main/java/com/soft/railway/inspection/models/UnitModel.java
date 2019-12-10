package com.soft.railway.inspection.models;

import android.content.ContentValues;
import android.database.Cursor;

public class UnitModel {
    private String gId;
    private String gName;
    private String parentId;
    private String level;


    public UnitModel() {
    }

    public UnitModel(Cursor cursor) {
        this.gId=cursor.getString(cursor.getColumnIndex("gid"));
        this.gName=cursor.getString(cursor.getColumnIndex("gname"));
        this.parentId=cursor.getString(cursor.getColumnIndex("parentid"));
        this.level=cursor.getString(cursor.getColumnIndex("level"));
    }

    public ContentValues getContentValues(UnitModel unitModel){
        ContentValues contentValues=new ContentValues();
        contentValues.put("gid",unitModel.getgId());
        contentValues.put("gname",unitModel.getgName());
        contentValues.put("parentid",unitModel.getParentId());
        contentValues.put("level",unitModel.getLevel());
        return contentValues;
    }

    public String getgId() {
        return gId;
    }

    public void setgId(String gId) {
        this.gId = gId;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    @Override
    public String toString() {
        return gName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
