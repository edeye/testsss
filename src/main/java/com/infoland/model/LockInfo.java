package com.infoland.model;

public class LockInfo {
    private Integer id;

    private String cardId;

    private String password;

    private String time;

    private String dbool;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId == null ? null : cardId.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time == null ? null : time.trim();
    }

    public String getDbool() {
        return dbool;
    }

    public void setDbool(String dbool) {
        this.dbool = dbool == null ? null : dbool.trim();
    }
}