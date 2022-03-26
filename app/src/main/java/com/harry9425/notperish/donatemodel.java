package com.harry9425.notperish;

public class donatemodel {
    String location,latltd,details,quantity,qynchng,type,edible,dp,donationid,uid,time,name;
    float distance;

    public donatemodel(String donationid, String uid) {
        this.donationid = donationid;
        this.uid = uid;
    }

    public donatemodel(String location, String latltd, String details, String quantity, String qynchng, String type, String edible, String dp, String donationid, String uid,String time,String name) {
        this.location = location;
        this.latltd = latltd;
        this.details = details;
        this.name=name;
        this.quantity = quantity;
        this.qynchng = qynchng;
        this.type = type;
        this.edible = edible;
        this.dp = dp;
        this.donationid = donationid;
        this.uid = uid;
        this.time=time;
    }

    public donatemodel() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatltd() {
        return latltd;
    }

    public void setLatltd(String latltd) {
        this.latltd = latltd;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQynchng() {
        return qynchng;
    }

    public void setQynchng(String qynchng) {
        this.qynchng = qynchng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEdible() {
        return edible;
    }

    public void setEdible(String edible) {
        this.edible = edible;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getDonationid() {
        return donationid;
    }

    public void setDonationid(String donationid) {
        this.donationid = donationid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
