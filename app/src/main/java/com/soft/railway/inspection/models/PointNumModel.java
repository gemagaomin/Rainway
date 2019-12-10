package com.soft.railway.inspection.models;

import java.io.Serializable;

public class PointNumModel implements Serializable {
    private String allCheck;
    private String allFinished;
    private String allOtherFinished;

    public String getAllCheck() {
        return allCheck;
    }

    public void setAllCheck(String allCheck) {
        this.allCheck = allCheck;
    }

    public String getAllFinished() {
        return allFinished;
    }

    public void setAllFinished(String allFinished) {
        this.allFinished = allFinished;
    }

    public String getAllOtherFinished() {
        return allOtherFinished;
    }

    public void setAllOtherFinished(String allOtherFinished) {
        this.allOtherFinished = allOtherFinished;
    }
}
