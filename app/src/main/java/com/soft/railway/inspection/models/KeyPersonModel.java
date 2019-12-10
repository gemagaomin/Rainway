package com.soft.railway.inspection.models;

import java.io.Serializable;
import java.util.List;

public class KeyPersonModel implements Serializable {
    private String keyPersonId ;
    private String driverId    ;
    private String driverName  ;
    private String keyType     ;
    private String times       ;
    private String cause       ;
    private String stepOne     ;
    private String stepTwo     ;
    private String stepThree   ;
    private String stepFour    ;
    private String stepFive    ;
    private String evaluate    ;
    private String teach       ;
    private String changeCause ;
    public enum  KeyTypeEnum{
        LAST_MONTH("0","LASTMONTH"),//上月关键人
        MONTH("1","MONTH"),//本月关键人
        QUARTER("2","QUARTER"),//本季度关键人
        LAST_QUARTER("3","LASTQUARTER");//上季度关键人
        private final String id;

        private final String value;
        private KeyTypeEnum(final String status,final String desc){
            this.id = status;
            this.value = desc;
        }

        public String getId() {
            KeyTypeEnum[] businessModeEnums = values();
            for (KeyTypeEnum businessModeEnum : businessModeEnums) {
                if (businessModeEnum.value.equals(value)) {
                    return businessModeEnum.id;
                }
            }
            return null;
        }

        public String getValue() {
            KeyTypeEnum[] businessModeEnums = values();
            for (KeyTypeEnum businessModeEnum : businessModeEnums) {
                if (businessModeEnum.id.equals(id)) {
                    return businessModeEnum.value;
                }
            }
            return null;
        }
    }

    public String getKeyPersonId() {
        return keyPersonId;
    }

    public void setKeyPersonId(String keyPersonId) {
        this.keyPersonId = keyPersonId;
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

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getStepOne() {
        return stepOne;
    }

    public void setStepOne(String stepOne) {
        this.stepOne = stepOne;
    }

    public String getStepTwo() {
        return stepTwo;
    }

    public void setStepTwo(String stepTwo) {
        this.stepTwo = stepTwo;
    }

    public String getStepThree() {
        return stepThree;
    }

    public void setStepThree(String stepThree) {
        this.stepThree = stepThree;
    }

    public String getStepFour() {
        return stepFour;
    }

    public void setStepFour(String stepFour) {
        this.stepFour = stepFour;
    }

    public String getStepFive() {
        return stepFive;
    }

    public void setStepFive(String stepFive) {
        this.stepFive = stepFive;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public KeyPersonModel getTypeKeyPerson(Enum type, List<KeyPersonModel> list){
        if(type==null ||list==null||list.size()==0){
            return null;
        }
        for (KeyPersonModel o:list
             ) {
            if(type.toString().equals(o.getKeyType())){
                return o;
            }
        }
        return null;
    }

    public String getTeach() {
        return teach;
    }

    public void setTeach(String teach) {
        this.teach = teach;
    }

    public String getChangeCause() {
        return changeCause;
    }

    public void setChangeCause(String changeCause) {
        this.changeCause = changeCause;
    }
}
