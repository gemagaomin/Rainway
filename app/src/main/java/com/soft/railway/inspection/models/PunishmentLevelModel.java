package com.soft.railway.inspection.models;

import java.io.Serializable;

public class PunishmentLevelModel implements Serializable {
    private String cardId;
    private String cardName;

    public PunishmentLevelModel(String cardId, String cardName) {
        this.cardId = cardId;
        this.cardName = cardName;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    @Override
    public String toString() {
        return "PunishmentLevelModel{" +
                "cardId='" + cardId + '\'' +
                ", cardName='" + cardName + '\'' +
                '}';
    }
}
