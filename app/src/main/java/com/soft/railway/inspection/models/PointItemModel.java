package com.soft.railway.inspection.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

public class PointItemModel {
    private String itemTypeId       ;//ID
    private String itemTypeName     ;//名称
    private String itemTypeCategory ;//类型
    private String isSelf;
    private String itemNumber;
    private String itemRiskGrade;

    public PointItemModel() {
    }

    public PointItemModel(Cursor cursor) {
        this.itemTypeId        = cursor.getString(cursor.getColumnIndex("itemtypeid"           ));
        this.itemTypeName      = cursor.getString(cursor.getColumnIndex("itemtypename"         ));
        this.itemTypeCategory  =cursor.getString(cursor.getColumnIndex( "itemtypecategory"     ));
        this.isSelf=cursor.getString(cursor.getColumnIndex(             "isself"               ));
        this.itemNumber=cursor.getString(cursor.getColumnIndex(         "itemnumber"               ));
    }

    public ContentValues getContentValues(PointItemModel pointModel){
        ContentValues contentValues=new ContentValues();
        contentValues.put("itemtypeid"      ,pointModel.getItemTypeId());
        contentValues.put("itemtypename"    ,pointModel.getItemTypeName());
        contentValues.put("itemtypecategory",pointModel.getItemTypeCategory());
        contentValues.put("isself"          ,pointModel.getIsSelf());
        contentValues.put("itemnumber"      ,pointModel.getItemNumber());
        return contentValues;
    }

    public String getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(String itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    public String getItemTypeName() {
        return itemTypeName;
    }

    public void setItemTypeName(String itemTypeName) {
        this.itemTypeName = itemTypeName;
    }

    public String getItemTypeCategory() {
        return itemTypeCategory;
    }

    public void setItemTypeCategory(String itemTypeCategory) {
        this.itemTypeCategory = itemTypeCategory;
    }

    public String getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(String isSelf) {
        this.isSelf = isSelf;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getItemRiskGrade() {
        return itemRiskGrade;
    }

    public void setItemRiskGrade(String itemRiskGrade) {
        this.itemRiskGrade = itemRiskGrade;
    }

    @Override
    public String toString() {
        if(TextUtils.isEmpty(itemNumber)){
            return itemTypeName;
        }else{
            return itemNumber+"."+itemTypeName;
        }

    }

}
