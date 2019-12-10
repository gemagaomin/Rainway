package com.soft.railway.inspection.models;

import java.io.Serializable;

public class HeaderModel implements Serializable {
    private String userId;
    private String unitId;
    private String jwdw;
    private String from;

    public HeaderModel() {
    }

    public HeaderModel(UserModel userModel) {
        String userId="";
        String unitId="";
        if(userModel!=null){
            userId=userModel.getUserId();
            unitId=userModel.getUnitId();
        }
        this.unitId=unitId;
        this.userId=userId;
        this.jwdw="";
        this.from="phone";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getJwdw() {
        return jwdw;
    }

    public void setJwdw(String jwdw) {
        this.jwdw = jwdw;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "HeaderModel{" +
                "userId='" + userId + '\'' +
                ", unitId='" + unitId + '\'' +
                ", jwdw='" + jwdw + '\'' +
                ", from='" + from + '\'' +
                '}';
    }

}
