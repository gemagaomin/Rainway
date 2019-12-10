package com.soft.railway.inspection.pojos;

import android.text.TextUtils;

import com.soft.railway.inspection.models.PointItemModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PointItemPojo implements Serializable {
    private String itemTypeId       ;//ID
    private String itemTypeName     ;//名称
    private String itemTypeCategory ;//类型
    private String isSelf;
    private String itemNumber;
    private String itemRiskGrade;
    private boolean isSelect;

    public PointItemPojo() {
    }

    public PointItemPojo(PointItemModel pointItemModel) {
        this.itemTypeId      =pointItemModel.getItemTypeId();
        this.itemTypeName     =pointItemModel.getItemTypeName();
        this.itemTypeCategory =pointItemModel.getItemTypeCategory();
        this.isSelf          =pointItemModel.getIsSelf();
        this.itemNumber      =pointItemModel.getItemNumber();
        this.itemRiskGrade   =pointItemModel.getItemRiskGrade();
        this.isSelect       =false;
    }

    public static List<PointItemPojo> getList(List<PointItemModel> list,String selectString){
        List<PointItemPojo> pointList=new ArrayList<>();
        boolean isNull=!TextUtils.isEmpty(selectString);
        if(list!=null&&list.size()>0){
            for (PointItemModel p:list) {
                PointItemPojo pointItemPojo=new PointItemPojo(p);
                if(isNull&&selectString.indexOf(pointItemPojo.getItemTypeId())!=-1){
                    pointItemPojo.setSelect(true);
                }
                pointList.add(pointItemPojo);
            }
        }
        return pointList;
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

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
