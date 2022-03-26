package com.harry9425.notperish;

public class productmodel {
    String name,date,category,uid,pno,dp,expiry,userid,expiryinms;
    Long exrem;
    int select;

    public productmodel(String uid, String pno) {
        this.uid = uid;
        this.pno = pno;
    }

    public productmodel() {
    }

    public productmodel(String name, String date, String category, String uid, String pno, String dp, String expiry,String expiryinms) {
        this.name = name;
        this.date = date;
        this.category = category;
        this.uid = uid;
        this.pno = pno;
        this.dp = dp;
        this.expiry = expiry;
        this.expiryinms=expiryinms;
    }

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public Long getExrem() {
        return exrem;
    }

    public void setExrem(Long exrem) {
        this.exrem = exrem;
    }

    public String getExpiryinms() {
        return expiryinms;
    }

    public void setExpiryinms(String expiryinms) {
        this.expiryinms = expiryinms;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPno() {
        return pno;
    }

    public void setPno(String pno) {
        this.pno = pno;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
