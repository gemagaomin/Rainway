package com.soft.railway.inspection.models;

import java.io.Serializable;
import java.util.List;

public class PointTestingResultModel implements Serializable {
    private String pointId;
    private String pointName;
    private String isSelf;
    private String countNum;
    private String cardNum;
    private String redCard;
    private String yellowCard;
    private String whiteCard;
    private String noCard;
    private String seed;
    private List<String> itemPoints;

    public PointTestingResultModel() {
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(String isSelf) {
        this.isSelf = isSelf;
    }

    public String getCountNum() {
        return countNum;
    }

    public void setCountNum(String countNum) {
        this.countNum = countNum;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getRedCard() {
        return redCard;
    }

    public void setRedCard(String redCard) {
        this.redCard = redCard;
    }

    public String getYellowCard() {
        return yellowCard;
    }

    public void setYellowCard(String yellowCard) {
        this.yellowCard = yellowCard;
    }

    public String getWhiteCard() {
        return whiteCard;
    }

    public void setWhiteCard(String whiteCard) {
        this.whiteCard = whiteCard;
    }

    public String getNoCard() {
        return noCard;
    }

    public void setNoCard(String noCard) {
        this.noCard = noCard;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public List<String> getItemPoints() {
        return itemPoints;
    }

    public void setItemPoints(List<String> itemPoints) {
        this.itemPoints = itemPoints;
    }

}
